package org.gluu.idp.externalauth;

import net.shibboleth.idp.authn.context.AuthenticationContext;
import org.opensaml.profile.context.ProfileRequestContext;

public class ReuseAuthnResultContext {
    
    private String usedAcr;
    private String requestedAcr;
    private ProfileRequestContext profileRequestContext;
    private AuthenticationContext authnContext;

    public ReuseAuthnResultContext() {

        usedAcr = null;
        requestedAcr = null;
    }

    public void setUsedAcr(final String usedAcr) {

        this.usedAcr = usedAcr;
    }

    public String getUsedAcr() {

        return usedAcr;
    }

    public void setRequestedAcr(final String requestedAcr) {

        this.requestedAcr = requestedAcr;
    }

    public String getRequestedAcr() {

        return requestedAcr;
    }

    public ProfileRequestContext getProfileRequestContext() {

        return profileRequestContext;
    }

    public void setProfileRequestContext(final ProfileRequestContext profileRequestContext) {

        this.profileRequestContext = profileRequestContext;
    }

    public AuthenticationContext getAuthenticationContext() {

        return authnContext;
    }

    public void setAuthenticationContext(final AuthenticationContext authnContext) {

        this.authnContext= authnContext;
    }
}
