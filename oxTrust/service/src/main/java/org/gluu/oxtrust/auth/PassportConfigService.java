package org.gluu.oxtrust.auth;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gluu.config.oxtrust.PassportConfigEndpointMode;
import org.gluu.oxtrust.auth.none.NoProtectionService;
import org.gluu.oxtrust.auth.oauth.DefaultOAuthProtectionService;
import org.gluu.oxtrust.auth.uma.PassportUmaProtectionService;
import org.gluu.oxtrust.service.*;

import org.slf4j.Logger;

@ApplicationScoped
@BindingUrls({"/passport/config"})
public class PassportConfigService implements GluuRestService {

    public static final String OAUTH_SCOPE = "https://gluu.org/passport/config.read";
    
    @Inject
    private Logger log;
    
    @Inject
    private ConfigurationService configurationService;
    
    @Inject
    private PassportUmaProtectionService passportUmaProtectionService;
    
    @Inject
    private DefaultOAuthProtectionService oauthProtectionService;
    
    @Inject
    private NoProtectionService noProtectionService;

	@Inject
	private PassportService passportService;
    
    @Override
    public String getName() {
        return "Passport configuration";
    }
    
    @Override
    public boolean isEnabled() {
        return configurationService.getConfiguration().isPassportEnabled();
    }
    
    @Override    
    public IProtectionService getProtectionService() {

        PassportConfigEndpointMode mode = passportService.loadConfigurationFromLdap()
                .getPassportConfiguration().getConf().getConfigEndpointMode();
        log.debug("Passport protection mode is: {}", mode);
        
        if (mode != null) {
            switch (mode) {
                case UMA: return passportUmaProtectionService;
                case OAUTH: return oauthProtectionService;
                case BYPASS: return noProtectionService;
            }
        }
        return null;
    }
    
}
