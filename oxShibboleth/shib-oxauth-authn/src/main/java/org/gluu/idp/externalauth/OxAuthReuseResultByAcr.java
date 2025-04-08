package org.gluu.idp.externalauth;

import java.security.Principal;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;

import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.authn.AuthenticationResult;
import net.shibboleth.idp.profile.config.ProfileConfiguration;
import net.shibboleth.idp.profile.context.RelyingPartyContext;
import net.shibboleth.idp.saml.saml2.profile.config.BrowserSSOProfileConfiguration;

import org.gluu.idp.context.GluuScratchContext;
import org.gluu.idp.script.service.IdpCustomScriptManager;
import org.gluu.idp.script.service.external.IdpExternalScriptService;
import org.gluu.orm.util.StringHelper;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OxAuthReuseResultByAcr implements Predicate<ProfileRequestContext> { 
    
    private final Logger LOG = LoggerFactory.getLogger(OxAuthReuseResultByAcr.class);

    private Function<ProfileRequestContext,AuthenticationContext> authnContextLookupStrategy;
    private Function<ProfileRequestContext,GluuScratchContext> gluuScratchContextLookupStrategy;
    private IdpExternalScriptService externalScriptService;

    private static final String OX_AUTH_FLOW_ID  = "authn/oxAuth";
    private static final String FORCE_AUTHN_QUERY_PARAM = "forceAuthn_";

    public OxAuthReuseResultByAcr(IdpCustomScriptManager customScriptManager) {

        externalScriptService = customScriptManager.getIdpExternalScriptService();
        authnContextLookupStrategy = new ChildContextLookup<ProfileRequestContext,AuthenticationContext>(AuthenticationContext.class);
        gluuScratchContextLookupStrategy = new ChildContextLookup<>(GluuScratchContext.class);
    }

    @Override
    public boolean test(ProfileRequestContext profileRequestContext) {
        
        final AuthenticationContext authnContext = authnContextLookupStrategy.apply(profileRequestContext);
        final GluuScratchContext gScratchContext = gluuScratchContextLookupStrategy.apply(profileRequestContext);
        if(authnContext == null) {
            //In principle this should not happen 
            LOG.debug("No Authentication context found. Re-using result");
            return true;
        }

        if(isForceAuthn(authnContext, gScratchContext)) {
            // do not re-use the authentication result
            LOG.debug("forcedAuthn enabled. Not-reusing results.");
            return false;
        }

        Map<String,AuthenticationResult> activeResultsMap = authnContext.getActiveResults();
        if(!activeResultsMap.containsKey(OX_AUTH_FLOW_ID)) {
            LOG.debug("Session does not have any authn/oxAuth results. Re-using authentication result");
            return true;
        }

        AuthenticationResult authnResult  = authnContext.getActiveResults().get(OX_AUTH_FLOW_ID);
        String usedAcr = authnResult.getAdditionalData().get(ShibOxAuthAuthServlet.OXAUTH_ACR_USED);
        String previouslyRequestedAcr = authnResult.getAdditionalData().get(ShibOxAuthAuthServlet.OXAUTH_ACR_REQUESTED);

        List<String> requestedAcrs = determineAcrs(profileRequestContext, authnContext);
        LOG.debug("Used ACR {}:{}, requested ACRs: {}",usedAcr,previouslyRequestedAcr,requestedAcrs);

        if((requestedAcrs == null) || (requestedAcrs.size() == 0) ) {
            LOG.debug("There are no requested acrs. Re-using authentication result");
            final ReuseAuthnResultContext scriptctx = new ReuseAuthnResultContext();
            scriptctx.setUsedAcr(usedAcr);
            scriptctx.setRequestedAcr(null);
            scriptctx.setProfileRequestContext(profileRequestContext);
            scriptctx.setAuthenticationContext(authnContext); 
            return externalScriptService.executeOnReuseAuthnResult(scriptctx); 
        }

        for(String requestedAcr : requestedAcrs) {
            if(StringHelper.equals(usedAcr,requestedAcr) || StringHelper.equals(previouslyRequestedAcr,requestedAcr)) {
                LOG.debug("Used and requested ACR are the same: {}, Re-using authentication result",usedAcr);
                final ReuseAuthnResultContext scriptctx = new ReuseAuthnResultContext();
                scriptctx.setUsedAcr(usedAcr);
                scriptctx.setRequestedAcr(requestedAcr);
                scriptctx.setProfileRequestContext(profileRequestContext);
                scriptctx.setAuthenticationContext(authnContext);
                return externalScriptService.executeOnReuseAuthnResult(scriptctx);
            }
        }

        LOG.debug("Will not be re-using authentication result. Auth request will be created with ACRs: {}",requestedAcrs);
        return false;
    }

    private final List<String> determineAcrs(ProfileRequestContext profileRequestContext, AuthenticationContext authnContext) {

        AuthnRequest authnRequest = (AuthnRequest) profileRequestContext.getInboundMessageContext().getMessage();
        if(authnRequest == null) {
            return null;
        }
        List<String> acrs = null;
        RequestedAuthnContext requestedAuthnContext = authnRequest.getRequestedAuthnContext();
        if(requestedAuthnContext == null) {
            Function<ProfileRequestContext,RelyingPartyContext> rpContextLookupStrategy = 
                    new ChildContextLookup<ProfileRequestContext,RelyingPartyContext>(RelyingPartyContext.class);
            final RelyingPartyContext relyingPartyContext = rpContextLookupStrategy.apply(profileRequestContext);
            
            if(relyingPartyContext != null) {
                ProfileConfiguration profileConfiguration = relyingPartyContext.getProfileConfig();
                if(profileConfiguration instanceof BrowserSSOProfileConfiguration) {
                    List<Principal> principals = ((BrowserSSOProfileConfiguration) profileConfiguration).getDefaultAuthenticationMethods(profileRequestContext);
                    acrs = principals.stream().map(Principal::getName).collect(Collectors.toList());
                }
            }
        }else {
        
            acrs = requestedAuthnContext.getAuthnContextClassRefs().stream()
                               .map(AuthnContextClassRef::getAuthnContextClassRef).collect(Collectors.toList());
        }

        return acrs;
    }

    private final boolean isForceAuthn(final AuthenticationContext authnContext, final GluuScratchContext gsContext) {

        if(authnContext.isForceAuthn() && gsContext == null) {
            return true;
        }

        if(gsContext == null) {
            
            return authnContext.isForceAuthn();
        }

        final String forceauthnqueryparam = gsContext.getExtraHttpParameter(FORCE_AUTHN_QUERY_PARAM);
        if(forceauthnqueryparam == null) {

            return authnContext.isForceAuthn();
        }

        return forceauthnqueryparam.equalsIgnoreCase("true");
    }
    
}
