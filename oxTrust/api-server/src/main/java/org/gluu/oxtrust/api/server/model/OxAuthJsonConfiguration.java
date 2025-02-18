package org.gluu.oxtrust.api.server.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gluu.oxauth.model.common.GrantType;
import org.gluu.oxauth.model.common.ResponseMode;
import org.gluu.oxauth.model.common.ResponseType;
import org.gluu.oxauth.model.common.WebKeyStorage;
import org.gluu.oxauth.model.configuration.AuthenticationFilter;
import org.gluu.oxauth.model.configuration.AuthenticationProtectionConfiguration;
import org.gluu.oxauth.model.configuration.CIBAEndUserNotificationConfig;
import org.gluu.oxauth.model.configuration.ClientAuthenticationFilter;
import org.gluu.oxauth.model.configuration.CorsConfigurationFilter;
import org.gluu.oxauth.model.error.ErrorHandlingMethod;
import org.gluu.oxauth.model.jwk.KeySelectionStrategy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
	"issuer", "baseEndpoint", "authorizationEndpoint", "tokenEndpoint", "tokenRevocationEndpoint", "userInfoEndpoint", "clientInfoEndpoint",
	"checkSessionIFrame", "endSessionEndpoint", "jwksUri", "registrationEndpoint", "openIdDiscoveryEndpoint",
	"openIdConfigurationEndpoint", "idGenerationEndpoint", "introspectionEndpoint", "deviceAuthzEndpoint",
	"discoveryCacheLifetimeInMinutes", "sectorIdentifierCacheLifetimeInMinutes",
	"sessionAsJwt", "forceRopcInAuthorizationEndpoint",
	"umaConfigurationEndpoint", "umaRptAsJwt", "umaRptLifetime", "umaTicketLifetime", "umaPctLifetime", "umaResourceLifetime",
	"umaAddScopesAutomatically", "umaValidateClaimToken", "umaGrantAccessIfNoPolicies", "umaRestrictResourceToAssociatedClient",
	"statEnabled", "statAuthorizationScope", "statTimerIntervalInSeconds", "statWebServiceIntervalLimitInSeconds",
	"authorizationChallengeDefaultAcr", "authorizationChallengeShouldGenerateSession", "authorizationChallengeSessionLifetimeInSeconds",
	"allowSpontaneousScopes", "spontaneousScopeLifetime", "openidSubAttribute", "responseTypesSupported", "responseModesSupported",
	"grantTypesSupported", "subjectTypesSupported", "defaultSubjectType","userInfoSigningAlgValuesSupported", "userInfoEncryptionAlgValuesSupported",
	"userInfoEncryptionEncValuesSupported", "idTokenSigningAlgValuesSupported", "idTokenEncryptionAlgValuesSupported", "idTokenEncryptionEncValuesSupported",
	"requestObjectSigningAlgValuesSupported", "requestObjectEncryptionAlgValuesSupported", "requestObjectEncryptionEncValuesSupported", "tokenEndpointAuthMethodsSupported",
	"tokenEndpointAuthSigningAlgValuesSupported", "dynamicRegistrationCustomAttributes", "displayValuesSupported", "claimTypesSupported", "jwksAlgorithmsSupported",
	"serviceDocumentation", "claimsLocalesSupported", "idTokenTokenBindingCnfValuesSupported", "uiLocalesSupported", "claimsParameterSupported",
	"requestParameterSupported", "requestUriParameterSupported", "requestUriHashVerificationEnabled", "requireRequestUriRegistration", "requestUriBlockList",
	"opPolicyUri", "opTosUri", "authorizationCodeLifetime", "refreshTokenLifetime", "idTokenLifetime", "accessTokenLifetime",
	"cleanServiceInterval", "cleanServiceBatchChunkSize",
	"keyRegenerationEnabled", "keyRegenerationInterval", "defaultSignatureAlgorithm", "oxOpenIdConnectVersion", "oxId", "dynamicRegistrationEnabled",
	"dynamicRegistrationExpirationTime", "dynamicRegistrationPersistClientAuthorizations", "trustedClientEnabled", "skipAuthorizationForOpenIdScopeAndPairwiseId",
	"dynamicRegistrationScopesParamEnabled", "dynamicRegistrationDisableFallbackScopesAssigning", "dynamicRegistrationPasswordGrantTypeEnabled",
	"dynamicRegistrationAllowedPasswordGrantScopes", "dynamicRegistrationCustomObjectClass", "personCustomObjectClassList",
	"persistIdTokenInLdap", "persistRefreshTokenInLdap", "allowPostLogoutRedirectWithoutValidation", "invalidateSessionCookiesAfterAuthorizationFlow",
	"returnClientSecretOnRead", "rejectJwtWithNoneAlg", "expirationNotificatorEnabled", "useNestedJwtDuringEncryption", "expirationNotificatorMapSizeLimit",
	"expirationNotificatorIntervalInSeconds",
	"useCacheForAllImplicitFlowObjects",
	"authenticationFiltersEnabled", "clientAuthenticationFiltersEnabled", "clientRegDefaultToCodeFlowWithRefresh", "grantTypesAndResponseTypesAutofixEnabled",
	"authenticationFilters", "clientAuthenticationFilters", "corsConfigurationFilters",
	"sessionIdUnusedLifetime", "sessionIdUnauthenticatedUnusedLifetime", "sessionIdPersistOnPromptNone", "sessionIdRequestParameterEnabled",
	"changeSessionIdOnAuthentication", "sessionIdPersistInCache",
	"sessionIdLifetime", "serverSessionIdLifetime", "configurationUpdateInterval",
	"logNotFoundEntityAsError", "enableClientGrantTypeUpdate", 
	"dynamicGrantTypeDefault", 
	"cssLocation", "jsLocation", "imgLocation", "metricReporterInterval", "metricReporterKeepDataDays", "metricReporterEnabled", "pairwiseIdType",
	"pairwiseCalculationKey", "pairwiseCalculationSalt", "shareSubjectIdBetweenClientsWithSameSectorId", "subjectIdentifierBasedOnWholeUriBackwardCompatibility",
	"webKeysStorage", "dnName",
	"keyStoreFile", "keyStoreSecret", "keySelectionStrategy", "keyAlgsAllowedForGeneration",
	"oxElevenTestModeToken", "oxElevenGenerateKeyEndpoint", "oxElevenSignEndpoint", "oxElevenVerifySignatureEndpoint", "oxElevenDeleteKeyEndpoint",
	"introspectionAccessTokenMustHaveUmaProtectionScope", "introspectionSkipAuthorization", "introspectionRestrictBasicAuthnToOwnTokens",
	"endSessionWithAccessToken", "cookieDomain", "enabledOAuthAuditLogging", "jmsBrokerURISet", "jmsUserName", "jmsPassword", "allowWildcardRedirectUri",
	"clientWhiteList", "clientBlackList", "legacyIdTokenClaims", "customHeadersWithAuthorizationResponse", "frontChannelLogoutSessionSupported",
	"loggingLevel", "loggingLayout", "updateUserLastLogonTime", "updateClientAccessTime", "logClientIdOnClientAuthentication", "logClientNameOnClientAuthentication",
	"disableJdkLogger", "authorizationRequestCustomAllowedParameters", "legacyDynamicRegistrationScopeParam", "openidScopeBackwardCompatibility","disableU2fEndpoint",
	"useLocalCache", "fapiCompatibility", "forceIdTokenHintPrecense", "rejectEndSessionIfIdTokenExpired", "allowEndSessionWithUnmatchedSid",
	"forceOfflineAccessScopeToEnableRefreshToken", "errorReasonEnabled", "removeRefreshTokensForClientOnLogout", "skipRefreshTokenDuringRefreshing",
	"refreshTokenExtendLifetimeOnRotation", "checkUserPresenceOnRefreshToken", "consentGatheringScriptBackwardCompatibility", "introspectionScriptBackwardCompatibility",
	"introspectionResponseScopesBackwardCompatibility","clientAuthorizationBackwardCompatibility",
	"softwareStatementValidationType", "softwareStatementValidationClaimName",
	"authenticationProtectionConfiguration",
	"errorHandlingMethod",
	"keepAuthenticatorAttributesOnAcrChange", "disableAuthnForMaxAgeZero", "deviceAuthzRequestExpiresIn", "deviceAuthzTokenPollInterval", "deviceAuthzResponseTypeToProcessAuthz",
	"backchannelClientId", "backchannelRedirectUri", "backchannelAuthenticationEndpoint", "backchannelDeviceRegistrationEndpoint", "backchannelTokenDeliveryModesSupported",
	"backchannelAuthenticationRequestSigningAlgValuesSupported", "backchannelUserCodeParameterSupported", "backchannelBindingMessagePattern",
	"backchannelAuthenticationResponseExpiresIn", "backchannelAuthenticationResponseInterval", "backchannelLoginHintClaims", "cibaEndUserNotificationConfig",
	"backchannelRequestsProcessorJobIntervalSec", "backchannelRequestsProcessorJobChunkSize", "cibaGrantLifeExtraTimeSec", "cibaMaxExpirationTimeAllowedSec", "cibaEnabled",
	"return200OnClientRegistration", "dateFormatterPatterns",
	"allowBlankValuesInDiscoveryResponse",
	"skipAuthenticationFilterOptionsMethod",
})
public class OxAuthJsonConfiguration {
    private String issuer;
    private String baseEndpoint;
    private String authorizationEndpoint;
    private String tokenEndpoint;
    private String tokenRevocationEndpoint;
    private String userInfoEndpoint;
    private String clientInfoEndpoint;
    private String checkSessionIFrame;
    private String endSessionEndpoint;
    private String jwksUri;
    private String registrationEndpoint;
    private String openIdDiscoveryEndpoint;
    private String openIdConfigurationEndpoint;
    private String idGenerationEndpoint;
    private String introspectionEndpoint;
    private String deviceAuthzEndpoint;

    private int discoveryCacheLifetimeInMinutes;
    private int sectorIdentifierCacheLifetimeInMinutes;

    private Boolean sessionAsJwt;
    private Boolean forceRopcInAuthorizationEndpoint;
    
    private String umaConfigurationEndpoint;
    private Boolean umaRptAsJwt;
    private int umaRptLifetime;
    private int umaTicketLifetime;
    private int umaPctLifetime;
    private int umaResourceLifetime;
    private Boolean umaAddScopesAutomatically;
    private Boolean umaValidateClaimToken;
    private Boolean umaGrantAccessIfNoPolicies;
    private Boolean umaRestrictResourceToAssociatedClient;

    private Boolean statEnabled;
    private String statAuthorizationScope;
    private int statTimerIntervalInSeconds;
    private int statWebServiceIntervalLimitInSeconds;

    private String authorizationChallengeDefaultAcr;
    private Boolean authorizationChallengeShouldGenerateSession;
    private Integer authorizationChallengeSessionLifetimeInSeconds;

    private Boolean allowSpontaneousScopes;
    private int spontaneousScopeLifetime;
    private String openidSubAttribute;
    private Set<Set<ResponseType>> responseTypesSupported = null;
    private Set<ResponseMode> responseModesSupported = null;
    private Set<GrantType> grantTypesSupported = null;
    private List<String> subjectTypesSupported = null;
    private String defaultSubjectType;
    private List<String> userInfoSigningAlgValuesSupported = null;
    private List<String> userInfoEncryptionAlgValuesSupported = null;
    private List<String> userInfoEncryptionEncValuesSupported = null;
    private List<String> idTokenSigningAlgValuesSupported = null;
    private List<String> idTokenEncryptionAlgValuesSupported = null;
    private List<String> idTokenEncryptionEncValuesSupported = null;
    private List<String> requestObjectSigningAlgValuesSupported = null;
    private List<String> requestObjectEncryptionAlgValuesSupported = null;
    private List<String> requestObjectEncryptionEncValuesSupported = null;
    private List<String> tokenEndpointAuthMethodsSupported = null;
    private List<String> tokenEndpointAuthSigningAlgValuesSupported = null;
    private List<String> dynamicRegistrationCustomAttributes = null;
    private List<String> displayValuesSupported = null;
    private List<String> claimTypesSupported = null;
    private List<String> jwksAlgorithmsSupported = null;
    private String serviceDocumentation;
    private List<String> claimsLocalesSupported = null;
    private List<String> idTokenTokenBindingCnfValuesSupported = null;
    private List<String> uiLocalesSupported = null;
    private Boolean claimsParameterSupported;
    private Boolean requestParameterSupported;
    private Boolean requestUriParameterSupported;
    private Boolean requestUriHashVerificationEnabled;
    private Boolean requireRequestUriRegistration;
    private List<String> requestUriBlockList = null;
    private String opPolicyUri;
    private String opTosUri;
    private int authorizationCodeLifetime;
    private int refreshTokenLifetime;
    private int idTokenLifetime;
    private int accessTokenLifetime;

    private int cleanServiceInterval;
    private int cleanServiceBatchChunkSize;

    private Boolean keyRegenerationEnabled;
    private int keyRegenerationInterval;
    private String defaultSignatureAlgorithm;
    private String oxOpenIdConnectVersion;
    private String oxId;
    private Boolean dynamicRegistrationEnabled;
    private int dynamicRegistrationExpirationTime;
    private Boolean dynamicRegistrationPersistClientAuthorizations;
    private Boolean trustedClientEnabled;
    private Boolean skipAuthorizationForOpenIdScopeAndPairwiseId;
    private Boolean dynamicRegistrationScopesParamEnabled;
    private Boolean dynamicRegistrationDisableFallbackScopesAssigning;
    private Boolean dynamicRegistrationPasswordGrantTypeEnabled;
    private List<String> dynamicRegistrationAllowedPasswordGrantScopes = null;
    private String dynamicRegistrationCustomObjectClass;
    private List<String> personCustomObjectClassList = null;

    private Boolean persistIdTokenInLdap;
    private Boolean persistRefreshTokenInLdap;
    private Boolean allowPostLogoutRedirectWithoutValidation;
    private Boolean invalidateSessionCookiesAfterAuthorizationFlow;
    private Boolean returnClientSecretOnRead;
    private Boolean rejectJwtWithNoneAlg;
    private Boolean expirationNotificatorEnabled;
    private Boolean useNestedJwtDuringEncryption;
    private int expirationNotificatorMapSizeLimit;
    private int expirationNotificatorIntervalInSeconds;

    private Boolean useCacheForAllImplicitFlowObjects;

    private Boolean authenticationFiltersEnabled;
    private Boolean clientAuthenticationFiltersEnabled;
    private Boolean clientRegDefaultToCodeFlowWithRefresh;
    private Boolean grantTypesAndResponseTypesAutofixEnabled;
    private List<AuthenticationFilter> authenticationFilters = null;
    private List<ClientAuthenticationFilter> clientAuthenticationFilters = null;
    private List<CorsConfigurationFilter> corsConfigurationFilters = null;

    private int sessionIdUnusedLifetime;
    private int sessionIdUnauthenticatedUnusedLifetime;
    private Boolean sessionIdPersistOnPromptNone;
    private Boolean sessionIdRequestParameterEnabled;
    private Boolean changeSessionIdOnAuthentication;
    private Boolean sessionIdPersistInCache;

    private Integer sessionIdLifetime;
    private Integer serverSessionIdLifetime;
    private int configurationUpdateInterval;

    private Boolean logNotFoundEntityAsError;
    private Boolean enableClientGrantTypeUpdate;
    private Set<GrantType> dynamicGrantTypeDefault;

    private String cssLocation;
    private String jsLocation;
    private String imgLocation;
    private int metricReporterInterval;
    private int metricReporterKeepDataDays;
    private Boolean metricReporterEnabled;
    private String pairwiseIdType; // persistent, algorithmic
    private String pairwiseCalculationKey;
    private String pairwiseCalculationSalt;
    private Boolean shareSubjectIdBetweenClientsWithSameSectorId;
    private Boolean subjectIdentifierBasedOnWholeUriBackwardCompatibility; // todo remove in 5.0

    private WebKeyStorage webKeysStorage;
    private String dnName;
    // oxAuth KeyStore
    private String keyStoreFile;
    private String keyStoreSecret;
    private KeySelectionStrategy keySelectionStrategy;
    private List<String> keyAlgsAllowedForGeneration = null;

    //oxEleven
    private String oxElevenTestModeToken;
    private String oxElevenGenerateKeyEndpoint;
    private String oxElevenSignEndpoint;
    private String oxElevenVerifySignatureEndpoint;
    private String oxElevenDeleteKeyEndpoint;

    private Boolean introspectionAccessTokenMustHaveUmaProtectionScope;
    private Boolean introspectionSkipAuthorization;
    private Boolean introspectionRestrictBasicAuthnToOwnTokens;

    private Boolean endSessionWithAccessToken;
    private String cookieDomain;
    private Boolean enabledOAuthAuditLogging;
    private Set<String> jmsBrokerURISet = null;
    private String jmsUserName;
    private String jmsPassword;
    private Boolean allowWildcardRedirectUri;
    private List<String> clientWhiteList = null;
    private List<String> clientBlackList = null;
    private Boolean legacyIdTokenClaims;
    private Boolean customHeadersWithAuthorizationResponse;
    private Boolean frontChannelLogoutSessionSupported;
    private String loggingLevel;
    private String loggingLayout;
    private Boolean updateUserLastLogonTime;
    private Boolean updateClientAccessTime;
    private Boolean logClientIdOnClientAuthentication;
    private Boolean logClientNameOnClientAuthentication;
    private Boolean disableJdkLogger;
    private Set<String> authorizationRequestCustomAllowedParameters = null;
    private Boolean legacyDynamicRegistrationScopeParam;
    private Boolean openidScopeBackwardCompatibility;
    private Boolean disableU2fEndpoint;

    private Boolean useLocalCache;
    private Boolean fapiCompatibility;
    private Boolean forceIdTokenHintPrecense;
    private Boolean rejectEndSessionIfIdTokenExpired;
    private Boolean allowEndSessionWithUnmatchedSid;
    private Boolean forceOfflineAccessScopeToEnableRefreshToken;
    private Boolean errorReasonEnabled;
    private Boolean removeRefreshTokensForClientOnLogout;
    private Boolean skipRefreshTokenDuringRefreshing;
    private Boolean refreshTokenExtendLifetimeOnRotation;
    private Boolean checkUserPresenceOnRefreshToken;
    private Boolean consentGatheringScriptBackwardCompatibility; // means ignore client configuration (as defined in 4.2) and determine it globally (as in 4.1 and earlier)
    private Boolean introspectionScriptBackwardCompatibility; // means ignore client configuration (as defined in 4.2) and determine it globally (as in 4.1 and earlier)
    private Boolean introspectionResponseScopesBackwardCompatibility; // See #1499
    private Boolean clientAuthorizationBackwardCompatibility; // search client authorization by filter (instead of key)

    private String softwareStatementValidationType;
    private String softwareStatementValidationClaimName;

    private AuthenticationProtectionConfiguration authenticationProtectionConfiguration;

    private ErrorHandlingMethod errorHandlingMethod;

    private Boolean keepAuthenticatorAttributesOnAcrChange;
    private Boolean disableAuthnForMaxAgeZero;
    private int deviceAuthzRequestExpiresIn;
    private int deviceAuthzTokenPollInterval;
    private String deviceAuthzResponseTypeToProcessAuthz;

    // CIBA
    private String backchannelClientId;
    private String backchannelRedirectUri;
    private String backchannelAuthenticationEndpoint;
    private String backchannelDeviceRegistrationEndpoint;
    private List<String> backchannelTokenDeliveryModesSupported = null;
    private List<String> backchannelAuthenticationRequestSigningAlgValuesSupported = null;
    private Boolean backchannelUserCodeParameterSupported;
    private String backchannelBindingMessagePattern;
    private int backchannelAuthenticationResponseExpiresIn;
    private int backchannelAuthenticationResponseInterval;
    private List<String> backchannelLoginHintClaims = null;
    private CIBAEndUserNotificationConfig cibaEndUserNotificationConfig;
    private int backchannelRequestsProcessorJobIntervalSec;
    private int backchannelRequestsProcessorJobChunkSize;
    private int cibaGrantLifeExtraTimeSec;
    private int cibaMaxExpirationTimeAllowedSec;
    private Boolean cibaEnabled;

    private Boolean return200OnClientRegistration;
    private Map<String, String> dateFormatterPatterns;

    private Boolean allowBlankValuesInDiscoveryResponse;

    private Boolean skipAuthenticationFilterOptionsMethod;

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getBaseEndpoint() {
		return baseEndpoint;
	}

	public void setBaseEndpoint(String baseEndpoint) {
		this.baseEndpoint = baseEndpoint;
	}

	public String getAuthorizationEndpoint() {
		return authorizationEndpoint;
	}

	public void setAuthorizationEndpoint(String authorizationEndpoint) {
		this.authorizationEndpoint = authorizationEndpoint;
	}

	public String getTokenEndpoint() {
		return tokenEndpoint;
	}

	public void setTokenEndpoint(String tokenEndpoint) {
		this.tokenEndpoint = tokenEndpoint;
	}

	public String getTokenRevocationEndpoint() {
		return tokenRevocationEndpoint;
	}

	public void setTokenRevocationEndpoint(String tokenRevocationEndpoint) {
		this.tokenRevocationEndpoint = tokenRevocationEndpoint;
	}

	public String getUserInfoEndpoint() {
		return userInfoEndpoint;
	}

	public void setUserInfoEndpoint(String userInfoEndpoint) {
		this.userInfoEndpoint = userInfoEndpoint;
	}

	public String getClientInfoEndpoint() {
		return clientInfoEndpoint;
	}

	public void setClientInfoEndpoint(String clientInfoEndpoint) {
		this.clientInfoEndpoint = clientInfoEndpoint;
	}

	public String getCheckSessionIFrame() {
		return checkSessionIFrame;
	}

	public void setCheckSessionIFrame(String checkSessionIFrame) {
		this.checkSessionIFrame = checkSessionIFrame;
	}

	public String getEndSessionEndpoint() {
		return endSessionEndpoint;
	}

	public void setEndSessionEndpoint(String endSessionEndpoint) {
		this.endSessionEndpoint = endSessionEndpoint;
	}

	public String getJwksUri() {
		return jwksUri;
	}

	public void setJwksUri(String jwksUri) {
		this.jwksUri = jwksUri;
	}

	public String getRegistrationEndpoint() {
		return registrationEndpoint;
	}

	public void setRegistrationEndpoint(String registrationEndpoint) {
		this.registrationEndpoint = registrationEndpoint;
	}

	public String getOpenIdDiscoveryEndpoint() {
		return openIdDiscoveryEndpoint;
	}

	public void setOpenIdDiscoveryEndpoint(String openIdDiscoveryEndpoint) {
		this.openIdDiscoveryEndpoint = openIdDiscoveryEndpoint;
	}

	public String getOpenIdConfigurationEndpoint() {
		return openIdConfigurationEndpoint;
	}

	public void setOpenIdConfigurationEndpoint(String openIdConfigurationEndpoint) {
		this.openIdConfigurationEndpoint = openIdConfigurationEndpoint;
	}

	public String getIdGenerationEndpoint() {
		return idGenerationEndpoint;
	}

	public void setIdGenerationEndpoint(String idGenerationEndpoint) {
		this.idGenerationEndpoint = idGenerationEndpoint;
	}

	public String getIntrospectionEndpoint() {
		return introspectionEndpoint;
	}

	public void setIntrospectionEndpoint(String introspectionEndpoint) {
		this.introspectionEndpoint = introspectionEndpoint;
	}

	public String getDeviceAuthzEndpoint() {
		return deviceAuthzEndpoint;
	}

	public void setDeviceAuthzEndpoint(String deviceAuthzEndpoint) {
		this.deviceAuthzEndpoint = deviceAuthzEndpoint;
	}

	public int getDiscoveryCacheLifetimeInMinutes() {
		return discoveryCacheLifetimeInMinutes;
	}

	public void setDiscoveryCacheLifetimeInMinutes(int discoveryCacheLifetimeInMinutes) {
		this.discoveryCacheLifetimeInMinutes = discoveryCacheLifetimeInMinutes;
	}

	public int getSectorIdentifierCacheLifetimeInMinutes() {
		return sectorIdentifierCacheLifetimeInMinutes;
	}

	public void setSectorIdentifierCacheLifetimeInMinutes(int sectorIdentifierCacheLifetimeInMinutes) {
		this.sectorIdentifierCacheLifetimeInMinutes = sectorIdentifierCacheLifetimeInMinutes;
	}

	public Boolean getSessionAsJwt() {
		return sessionAsJwt;
	}

	public void setSessionAsJwt(Boolean sessionAsJwt) {
		this.sessionAsJwt = sessionAsJwt;
	}

	public Boolean getForceRopcInAuthorizationEndpoint() {
		return forceRopcInAuthorizationEndpoint;
	}

	public void setForceRopcInAuthorizationEndpoint(Boolean forceRopcInAuthorizationEndpoint) {
		this.forceRopcInAuthorizationEndpoint = forceRopcInAuthorizationEndpoint;
	}

	public String getUmaConfigurationEndpoint() {
		return umaConfigurationEndpoint;
	}

	public void setUmaConfigurationEndpoint(String umaConfigurationEndpoint) {
		this.umaConfigurationEndpoint = umaConfigurationEndpoint;
	}

	public Boolean getUmaRptAsJwt() {
		return umaRptAsJwt;
	}

	public void setUmaRptAsJwt(Boolean umaRptAsJwt) {
		this.umaRptAsJwt = umaRptAsJwt;
	}

	public int getUmaRptLifetime() {
		return umaRptLifetime;
	}

	public void setUmaRptLifetime(int umaRptLifetime) {
		this.umaRptLifetime = umaRptLifetime;
	}

	public int getUmaTicketLifetime() {
		return umaTicketLifetime;
	}

	public void setUmaTicketLifetime(int umaTicketLifetime) {
		this.umaTicketLifetime = umaTicketLifetime;
	}

	public int getUmaPctLifetime() {
		return umaPctLifetime;
	}

	public void setUmaPctLifetime(int umaPctLifetime) {
		this.umaPctLifetime = umaPctLifetime;
	}

	public int getUmaResourceLifetime() {
		return umaResourceLifetime;
	}

	public void setUmaResourceLifetime(int umaResourceLifetime) {
		this.umaResourceLifetime = umaResourceLifetime;
	}

	public Boolean getUmaAddScopesAutomatically() {
		return umaAddScopesAutomatically;
	}

	public void setUmaAddScopesAutomatically(Boolean umaAddScopesAutomatically) {
		this.umaAddScopesAutomatically = umaAddScopesAutomatically;
	}

	public Boolean getUmaValidateClaimToken() {
		return umaValidateClaimToken;
	}

	public void setUmaValidateClaimToken(Boolean umaValidateClaimToken) {
		this.umaValidateClaimToken = umaValidateClaimToken;
	}

	public Boolean getUmaGrantAccessIfNoPolicies() {
		return umaGrantAccessIfNoPolicies;
	}

	public void setUmaGrantAccessIfNoPolicies(Boolean umaGrantAccessIfNoPolicies) {
		this.umaGrantAccessIfNoPolicies = umaGrantAccessIfNoPolicies;
	}

	public Boolean getUmaRestrictResourceToAssociatedClient() {
		return umaRestrictResourceToAssociatedClient;
	}

	public void setUmaRestrictResourceToAssociatedClient(Boolean umaRestrictResourceToAssociatedClient) {
		this.umaRestrictResourceToAssociatedClient = umaRestrictResourceToAssociatedClient;
	}

	public Boolean getStatEnabled() {
		return statEnabled;
	}

	public void setStatEnabled(Boolean statEnabled) {
		this.statEnabled = statEnabled;
	}

	public String getStatAuthorizationScope() {
		return statAuthorizationScope;
	}

	public void setStatAuthorizationScope(String statAuthorizationScope) {
		this.statAuthorizationScope = statAuthorizationScope;
	}

	public int getStatTimerIntervalInSeconds() {
		return statTimerIntervalInSeconds;
	}

	public void setStatTimerIntervalInSeconds(int statTimerIntervalInSeconds) {
		this.statTimerIntervalInSeconds = statTimerIntervalInSeconds;
	}

	public int getStatWebServiceIntervalLimitInSeconds() {
		return statWebServiceIntervalLimitInSeconds;
	}

	public void setStatWebServiceIntervalLimitInSeconds(int statWebServiceIntervalLimitInSeconds) {
		this.statWebServiceIntervalLimitInSeconds = statWebServiceIntervalLimitInSeconds;
	}

	public String getAuthorizationChallengeDefaultAcr() {
		return authorizationChallengeDefaultAcr;
	}

	public void setAuthorizationChallengeDefaultAcr(String authorizationChallengeDefaultAcr) {
		this.authorizationChallengeDefaultAcr = authorizationChallengeDefaultAcr;
	}

	public Boolean getAuthorizationChallengeShouldGenerateSession() {
		return authorizationChallengeShouldGenerateSession;
	}

	public void setAuthorizationChallengeShouldGenerateSession(Boolean authorizationChallengeShouldGenerateSession) {
		this.authorizationChallengeShouldGenerateSession = authorizationChallengeShouldGenerateSession;
	}

	public Integer getAuthorizationChallengeSessionLifetimeInSeconds() {
		return authorizationChallengeSessionLifetimeInSeconds;
	}

	public void setAuthorizationChallengeSessionLifetimeInSeconds(Integer authorizationChallengeSessionLifetimeInSeconds) {
		this.authorizationChallengeSessionLifetimeInSeconds = authorizationChallengeSessionLifetimeInSeconds;
	}

	public Boolean getAllowSpontaneousScopes() {
		return allowSpontaneousScopes;
	}

	public void setAllowSpontaneousScopes(Boolean allowSpontaneousScopes) {
		this.allowSpontaneousScopes = allowSpontaneousScopes;
	}

	public int getSpontaneousScopeLifetime() {
		return spontaneousScopeLifetime;
	}

	public void setSpontaneousScopeLifetime(int spontaneousScopeLifetime) {
		this.spontaneousScopeLifetime = spontaneousScopeLifetime;
	}

	public String getOpenidSubAttribute() {
		return openidSubAttribute;
	}

	public void setOpenidSubAttribute(String openidSubAttribute) {
		this.openidSubAttribute = openidSubAttribute;
	}

	public Set<Set<ResponseType>> getResponseTypesSupported() {
		return responseTypesSupported;
	}

	public void setResponseTypesSupported(Set<Set<ResponseType>> responseTypesSupported) {
		this.responseTypesSupported = responseTypesSupported;
	}

	public Set<ResponseMode> getResponseModesSupported() {
		return responseModesSupported;
	}

	public void setResponseModesSupported(Set<ResponseMode> responseModesSupported) {
		this.responseModesSupported = responseModesSupported;
	}

	public Set<GrantType> getGrantTypesSupported() {
		return grantTypesSupported;
	}

	public void setGrantTypesSupported(Set<GrantType> grantTypesSupported) {
		this.grantTypesSupported = grantTypesSupported;
	}

	public List<String> getSubjectTypesSupported() {
		return subjectTypesSupported;
	}

	public void setSubjectTypesSupported(List<String> subjectTypesSupported) {
		this.subjectTypesSupported = subjectTypesSupported;
	}

	public String getDefaultSubjectType() {
		return defaultSubjectType;
	}

	public void setDefaultSubjectType(String defaultSubjectType) {
		this.defaultSubjectType = defaultSubjectType;
	}

	public List<String> getUserInfoSigningAlgValuesSupported() {
		return userInfoSigningAlgValuesSupported;
	}

	public void setUserInfoSigningAlgValuesSupported(List<String> userInfoSigningAlgValuesSupported) {
		this.userInfoSigningAlgValuesSupported = userInfoSigningAlgValuesSupported;
	}

	public List<String> getUserInfoEncryptionAlgValuesSupported() {
		return userInfoEncryptionAlgValuesSupported;
	}

	public void setUserInfoEncryptionAlgValuesSupported(List<String> userInfoEncryptionAlgValuesSupported) {
		this.userInfoEncryptionAlgValuesSupported = userInfoEncryptionAlgValuesSupported;
	}

	public List<String> getUserInfoEncryptionEncValuesSupported() {
		return userInfoEncryptionEncValuesSupported;
	}

	public void setUserInfoEncryptionEncValuesSupported(List<String> userInfoEncryptionEncValuesSupported) {
		this.userInfoEncryptionEncValuesSupported = userInfoEncryptionEncValuesSupported;
	}

	public List<String> getIdTokenSigningAlgValuesSupported() {
		return idTokenSigningAlgValuesSupported;
	}

	public void setIdTokenSigningAlgValuesSupported(List<String> idTokenSigningAlgValuesSupported) {
		this.idTokenSigningAlgValuesSupported = idTokenSigningAlgValuesSupported;
	}

	public List<String> getIdTokenEncryptionAlgValuesSupported() {
		return idTokenEncryptionAlgValuesSupported;
	}

	public void setIdTokenEncryptionAlgValuesSupported(List<String> idTokenEncryptionAlgValuesSupported) {
		this.idTokenEncryptionAlgValuesSupported = idTokenEncryptionAlgValuesSupported;
	}

	public List<String> getIdTokenEncryptionEncValuesSupported() {
		return idTokenEncryptionEncValuesSupported;
	}

	public void setIdTokenEncryptionEncValuesSupported(List<String> idTokenEncryptionEncValuesSupported) {
		this.idTokenEncryptionEncValuesSupported = idTokenEncryptionEncValuesSupported;
	}

	public List<String> getRequestObjectSigningAlgValuesSupported() {
		return requestObjectSigningAlgValuesSupported;
	}

	public void setRequestObjectSigningAlgValuesSupported(List<String> requestObjectSigningAlgValuesSupported) {
		this.requestObjectSigningAlgValuesSupported = requestObjectSigningAlgValuesSupported;
	}

	public List<String> getRequestObjectEncryptionAlgValuesSupported() {
		return requestObjectEncryptionAlgValuesSupported;
	}

	public void setRequestObjectEncryptionAlgValuesSupported(List<String> requestObjectEncryptionAlgValuesSupported) {
		this.requestObjectEncryptionAlgValuesSupported = requestObjectEncryptionAlgValuesSupported;
	}

	public List<String> getRequestObjectEncryptionEncValuesSupported() {
		return requestObjectEncryptionEncValuesSupported;
	}

	public void setRequestObjectEncryptionEncValuesSupported(List<String> requestObjectEncryptionEncValuesSupported) {
		this.requestObjectEncryptionEncValuesSupported = requestObjectEncryptionEncValuesSupported;
	}

	public List<String> getTokenEndpointAuthMethodsSupported() {
		return tokenEndpointAuthMethodsSupported;
	}

	public void setTokenEndpointAuthMethodsSupported(List<String> tokenEndpointAuthMethodsSupported) {
		this.tokenEndpointAuthMethodsSupported = tokenEndpointAuthMethodsSupported;
	}

	public List<String> getTokenEndpointAuthSigningAlgValuesSupported() {
		return tokenEndpointAuthSigningAlgValuesSupported;
	}

	public void setTokenEndpointAuthSigningAlgValuesSupported(List<String> tokenEndpointAuthSigningAlgValuesSupported) {
		this.tokenEndpointAuthSigningAlgValuesSupported = tokenEndpointAuthSigningAlgValuesSupported;
	}

	public List<String> getDynamicRegistrationCustomAttributes() {
		return dynamicRegistrationCustomAttributes;
	}

	public void setDynamicRegistrationCustomAttributes(List<String> dynamicRegistrationCustomAttributes) {
		this.dynamicRegistrationCustomAttributes = dynamicRegistrationCustomAttributes;
	}

	public List<String> getDisplayValuesSupported() {
		return displayValuesSupported;
	}

	public void setDisplayValuesSupported(List<String> displayValuesSupported) {
		this.displayValuesSupported = displayValuesSupported;
	}

	public List<String> getClaimTypesSupported() {
		return claimTypesSupported;
	}

	public void setClaimTypesSupported(List<String> claimTypesSupported) {
		this.claimTypesSupported = claimTypesSupported;
	}

	public List<String> getJwksAlgorithmsSupported() {
		return jwksAlgorithmsSupported;
	}

	public void setJwksAlgorithmsSupported(List<String> jwksAlgorithmsSupported) {
		this.jwksAlgorithmsSupported = jwksAlgorithmsSupported;
	}

	public String getServiceDocumentation() {
		return serviceDocumentation;
	}

	public void setServiceDocumentation(String serviceDocumentation) {
		this.serviceDocumentation = serviceDocumentation;
	}

	public List<String> getClaimsLocalesSupported() {
		return claimsLocalesSupported;
	}

	public void setClaimsLocalesSupported(List<String> claimsLocalesSupported) {
		this.claimsLocalesSupported = claimsLocalesSupported;
	}

	public List<String> getIdTokenTokenBindingCnfValuesSupported() {
		return idTokenTokenBindingCnfValuesSupported;
	}

	public void setIdTokenTokenBindingCnfValuesSupported(List<String> idTokenTokenBindingCnfValuesSupported) {
		this.idTokenTokenBindingCnfValuesSupported = idTokenTokenBindingCnfValuesSupported;
	}

	public List<String> getUiLocalesSupported() {
		return uiLocalesSupported;
	}

	public void setUiLocalesSupported(List<String> uiLocalesSupported) {
		this.uiLocalesSupported = uiLocalesSupported;
	}

	public Boolean getClaimsParameterSupported() {
		return claimsParameterSupported;
	}

	public void setClaimsParameterSupported(Boolean claimsParameterSupported) {
		this.claimsParameterSupported = claimsParameterSupported;
	}

	public Boolean getRequestParameterSupported() {
		return requestParameterSupported;
	}

	public void setRequestParameterSupported(Boolean requestParameterSupported) {
		this.requestParameterSupported = requestParameterSupported;
	}

	public Boolean getRequestUriParameterSupported() {
		return requestUriParameterSupported;
	}

	public void setRequestUriParameterSupported(Boolean requestUriParameterSupported) {
		this.requestUriParameterSupported = requestUriParameterSupported;
	}

	public Boolean getRequestUriHashVerificationEnabled() {
		return requestUriHashVerificationEnabled;
	}

	public void setRequestUriHashVerificationEnabled(Boolean requestUriHashVerificationEnabled) {
		this.requestUriHashVerificationEnabled = requestUriHashVerificationEnabled;
	}

	public Boolean getRequireRequestUriRegistration() {
		return requireRequestUriRegistration;
	}

	public void setRequireRequestUriRegistration(Boolean requireRequestUriRegistration) {
		this.requireRequestUriRegistration = requireRequestUriRegistration;
	}

	public List<String> getRequestUriBlockList() {
		return requestUriBlockList;
	}

	public void setRequestUriBlockList(List<String> requestUriBlockList) {
		this.requestUriBlockList = requestUriBlockList;
	}

	public String getOpPolicyUri() {
		return opPolicyUri;
	}

	public void setOpPolicyUri(String opPolicyUri) {
		this.opPolicyUri = opPolicyUri;
	}

	public String getOpTosUri() {
		return opTosUri;
	}

	public void setOpTosUri(String opTosUri) {
		this.opTosUri = opTosUri;
	}

	public int getAuthorizationCodeLifetime() {
		return authorizationCodeLifetime;
	}

	public void setAuthorizationCodeLifetime(int authorizationCodeLifetime) {
		this.authorizationCodeLifetime = authorizationCodeLifetime;
	}

	public int getRefreshTokenLifetime() {
		return refreshTokenLifetime;
	}

	public void setRefreshTokenLifetime(int refreshTokenLifetime) {
		this.refreshTokenLifetime = refreshTokenLifetime;
	}

	public int getIdTokenLifetime() {
		return idTokenLifetime;
	}

	public void setIdTokenLifetime(int idTokenLifetime) {
		this.idTokenLifetime = idTokenLifetime;
	}

	public int getAccessTokenLifetime() {
		return accessTokenLifetime;
	}

	public void setAccessTokenLifetime(int accessTokenLifetime) {
		this.accessTokenLifetime = accessTokenLifetime;
	}

	public int getCleanServiceInterval() {
		return cleanServiceInterval;
	}

	public void setCleanServiceInterval(int cleanServiceInterval) {
		this.cleanServiceInterval = cleanServiceInterval;
	}

	public int getCleanServiceBatchChunkSize() {
		return cleanServiceBatchChunkSize;
	}

	public void setCleanServiceBatchChunkSize(int cleanServiceBatchChunkSize) {
		this.cleanServiceBatchChunkSize = cleanServiceBatchChunkSize;
	}

	public Boolean getKeyRegenerationEnabled() {
		return keyRegenerationEnabled;
	}

	public void setKeyRegenerationEnabled(Boolean keyRegenerationEnabled) {
		this.keyRegenerationEnabled = keyRegenerationEnabled;
	}

	public int getKeyRegenerationInterval() {
		return keyRegenerationInterval;
	}

	public void setKeyRegenerationInterval(int keyRegenerationInterval) {
		this.keyRegenerationInterval = keyRegenerationInterval;
	}

	public String getDefaultSignatureAlgorithm() {
		return defaultSignatureAlgorithm;
	}

	public void setDefaultSignatureAlgorithm(String defaultSignatureAlgorithm) {
		this.defaultSignatureAlgorithm = defaultSignatureAlgorithm;
	}

	public String getOxOpenIdConnectVersion() {
		return oxOpenIdConnectVersion;
	}

	public void setOxOpenIdConnectVersion(String oxOpenIdConnectVersion) {
		this.oxOpenIdConnectVersion = oxOpenIdConnectVersion;
	}

	public String getOxId() {
		return oxId;
	}

	public void setOxId(String oxId) {
		this.oxId = oxId;
	}

	public Boolean getDynamicRegistrationEnabled() {
		return dynamicRegistrationEnabled;
	}

	public void setDynamicRegistrationEnabled(Boolean dynamicRegistrationEnabled) {
		this.dynamicRegistrationEnabled = dynamicRegistrationEnabled;
	}

	public int getDynamicRegistrationExpirationTime() {
		return dynamicRegistrationExpirationTime;
	}

	public void setDynamicRegistrationExpirationTime(int dynamicRegistrationExpirationTime) {
		this.dynamicRegistrationExpirationTime = dynamicRegistrationExpirationTime;
	}

	public Boolean getDynamicRegistrationPersistClientAuthorizations() {
		return dynamicRegistrationPersistClientAuthorizations;
	}

	public void setDynamicRegistrationPersistClientAuthorizations(Boolean dynamicRegistrationPersistClientAuthorizations) {
		this.dynamicRegistrationPersistClientAuthorizations = dynamicRegistrationPersistClientAuthorizations;
	}

	public Boolean getTrustedClientEnabled() {
		return trustedClientEnabled;
	}

	public void setTrustedClientEnabled(Boolean trustedClientEnabled) {
		this.trustedClientEnabled = trustedClientEnabled;
	}

	public Boolean getSkipAuthorizationForOpenIdScopeAndPairwiseId() {
		return skipAuthorizationForOpenIdScopeAndPairwiseId;
	}

	public void setSkipAuthorizationForOpenIdScopeAndPairwiseId(Boolean skipAuthorizationForOpenIdScopeAndPairwiseId) {
		this.skipAuthorizationForOpenIdScopeAndPairwiseId = skipAuthorizationForOpenIdScopeAndPairwiseId;
	}

	public Boolean getDynamicRegistrationScopesParamEnabled() {
		return dynamicRegistrationScopesParamEnabled;
	}

	public void setDynamicRegistrationScopesParamEnabled(Boolean dynamicRegistrationScopesParamEnabled) {
		this.dynamicRegistrationScopesParamEnabled = dynamicRegistrationScopesParamEnabled;
	}

	public Boolean getDynamicRegistrationDisableFallbackScopesAssigning() {
		return dynamicRegistrationDisableFallbackScopesAssigning;
	}

	public void setDynamicRegistrationDisableFallbackScopesAssigning(
			Boolean dynamicRegistrationDisableFallbackScopesAssigning) {
		this.dynamicRegistrationDisableFallbackScopesAssigning = dynamicRegistrationDisableFallbackScopesAssigning;
	}

	public Boolean getDynamicRegistrationPasswordGrantTypeEnabled() {
		return dynamicRegistrationPasswordGrantTypeEnabled;
	}

	public void setDynamicRegistrationPasswordGrantTypeEnabled(Boolean dynamicRegistrationPasswordGrantTypeEnabled) {
		this.dynamicRegistrationPasswordGrantTypeEnabled = dynamicRegistrationPasswordGrantTypeEnabled;
	}

	public List<String> getDynamicRegistrationAllowedPasswordGrantScopes() {
		return dynamicRegistrationAllowedPasswordGrantScopes;
	}

	public void setDynamicRegistrationAllowedPasswordGrantScopes(
			List<String> dynamicRegistrationAllowedPasswordGrantScopes) {
		this.dynamicRegistrationAllowedPasswordGrantScopes = dynamicRegistrationAllowedPasswordGrantScopes;
	}

	public String getDynamicRegistrationCustomObjectClass() {
		return dynamicRegistrationCustomObjectClass;
	}

	public void setDynamicRegistrationCustomObjectClass(String dynamicRegistrationCustomObjectClass) {
		this.dynamicRegistrationCustomObjectClass = dynamicRegistrationCustomObjectClass;
	}

	public List<String> getPersonCustomObjectClassList() {
		return personCustomObjectClassList;
	}

	public void setPersonCustomObjectClassList(List<String> personCustomObjectClassList) {
		this.personCustomObjectClassList = personCustomObjectClassList;
	}

	public Boolean getPersistIdTokenInLdap() {
		return persistIdTokenInLdap;
	}

	public void setPersistIdTokenInLdap(Boolean persistIdTokenInLdap) {
		this.persistIdTokenInLdap = persistIdTokenInLdap;
	}

	public Boolean getPersistRefreshTokenInLdap() {
		return persistRefreshTokenInLdap;
	}

	public void setPersistRefreshTokenInLdap(Boolean persistRefreshTokenInLdap) {
		this.persistRefreshTokenInLdap = persistRefreshTokenInLdap;
	}

	public Boolean getAllowPostLogoutRedirectWithoutValidation() {
		return allowPostLogoutRedirectWithoutValidation;
	}

	public void setAllowPostLogoutRedirectWithoutValidation(Boolean allowPostLogoutRedirectWithoutValidation) {
		this.allowPostLogoutRedirectWithoutValidation = allowPostLogoutRedirectWithoutValidation;
	}

	public Boolean getInvalidateSessionCookiesAfterAuthorizationFlow() {
		return invalidateSessionCookiesAfterAuthorizationFlow;
	}

	public void setInvalidateSessionCookiesAfterAuthorizationFlow(Boolean invalidateSessionCookiesAfterAuthorizationFlow) {
		this.invalidateSessionCookiesAfterAuthorizationFlow = invalidateSessionCookiesAfterAuthorizationFlow;
	}

	public Boolean getReturnClientSecretOnRead() {
		return returnClientSecretOnRead;
	}

	public void setReturnClientSecretOnRead(Boolean returnClientSecretOnRead) {
		this.returnClientSecretOnRead = returnClientSecretOnRead;
	}

	public Boolean getRejectJwtWithNoneAlg() {
		return rejectJwtWithNoneAlg;
	}

	public void setRejectJwtWithNoneAlg(Boolean rejectJwtWithNoneAlg) {
		this.rejectJwtWithNoneAlg = rejectJwtWithNoneAlg;
	}

	public Boolean getExpirationNotificatorEnabled() {
		return expirationNotificatorEnabled;
	}

	public void setExpirationNotificatorEnabled(Boolean expirationNotificatorEnabled) {
		this.expirationNotificatorEnabled = expirationNotificatorEnabled;
	}

	public Boolean getUseNestedJwtDuringEncryption() {
		return useNestedJwtDuringEncryption;
	}

	public void setUseNestedJwtDuringEncryption(Boolean useNestedJwtDuringEncryption) {
		this.useNestedJwtDuringEncryption = useNestedJwtDuringEncryption;
	}

	public int getExpirationNotificatorMapSizeLimit() {
		return expirationNotificatorMapSizeLimit;
	}

	public void setExpirationNotificatorMapSizeLimit(int expirationNotificatorMapSizeLimit) {
		this.expirationNotificatorMapSizeLimit = expirationNotificatorMapSizeLimit;
	}

	public int getExpirationNotificatorIntervalInSeconds() {
		return expirationNotificatorIntervalInSeconds;
	}

	public void setExpirationNotificatorIntervalInSeconds(int expirationNotificatorIntervalInSeconds) {
		this.expirationNotificatorIntervalInSeconds = expirationNotificatorIntervalInSeconds;
	}

	public Boolean getUseCacheForAllImplicitFlowObjects() {
		return useCacheForAllImplicitFlowObjects;
	}

	public void setUseCacheForAllImplicitFlowObjects(Boolean useCacheForAllImplicitFlowObjects) {
		this.useCacheForAllImplicitFlowObjects = useCacheForAllImplicitFlowObjects;
	}

	public Boolean getAuthenticationFiltersEnabled() {
		return authenticationFiltersEnabled;
	}

	public void setAuthenticationFiltersEnabled(Boolean authenticationFiltersEnabled) {
		this.authenticationFiltersEnabled = authenticationFiltersEnabled;
	}

	public Boolean getClientAuthenticationFiltersEnabled() {
		return clientAuthenticationFiltersEnabled;
	}

	public void setClientAuthenticationFiltersEnabled(Boolean clientAuthenticationFiltersEnabled) {
		this.clientAuthenticationFiltersEnabled = clientAuthenticationFiltersEnabled;
	}

	public Boolean getClientRegDefaultToCodeFlowWithRefresh() {
		return clientRegDefaultToCodeFlowWithRefresh;
	}

	public void setClientRegDefaultToCodeFlowWithRefresh(Boolean clientRegDefaultToCodeFlowWithRefresh) {
		this.clientRegDefaultToCodeFlowWithRefresh = clientRegDefaultToCodeFlowWithRefresh;
	}

	public Boolean getGrantTypesAndResponseTypesAutofixEnabled() {
		return grantTypesAndResponseTypesAutofixEnabled;
	}

	public void setGrantTypesAndResponseTypesAutofixEnabled(Boolean grantTypesAndResponseTypesAutofixEnabled) {
		this.grantTypesAndResponseTypesAutofixEnabled = grantTypesAndResponseTypesAutofixEnabled;
	}

	public List<AuthenticationFilter> getAuthenticationFilters() {
		return authenticationFilters;
	}

	public void setAuthenticationFilters(List<AuthenticationFilter> authenticationFilters) {
		this.authenticationFilters = authenticationFilters;
	}

	public List<ClientAuthenticationFilter> getClientAuthenticationFilters() {
		return clientAuthenticationFilters;
	}

	public void setClientAuthenticationFilters(List<ClientAuthenticationFilter> clientAuthenticationFilters) {
		this.clientAuthenticationFilters = clientAuthenticationFilters;
	}

	public List<CorsConfigurationFilter> getCorsConfigurationFilters() {
		return corsConfigurationFilters;
	}

	public void setCorsConfigurationFilters(List<CorsConfigurationFilter> corsConfigurationFilters) {
		this.corsConfigurationFilters = corsConfigurationFilters;
	}

	public int getSessionIdUnusedLifetime() {
		return sessionIdUnusedLifetime;
	}

	public void setSessionIdUnusedLifetime(int sessionIdUnusedLifetime) {
		this.sessionIdUnusedLifetime = sessionIdUnusedLifetime;
	}

	public int getSessionIdUnauthenticatedUnusedLifetime() {
		return sessionIdUnauthenticatedUnusedLifetime;
	}

	public void setSessionIdUnauthenticatedUnusedLifetime(int sessionIdUnauthenticatedUnusedLifetime) {
		this.sessionIdUnauthenticatedUnusedLifetime = sessionIdUnauthenticatedUnusedLifetime;
	}

	public Boolean getSessionIdPersistOnPromptNone() {
		return sessionIdPersistOnPromptNone;
	}

	public void setSessionIdPersistOnPromptNone(Boolean sessionIdPersistOnPromptNone) {
		this.sessionIdPersistOnPromptNone = sessionIdPersistOnPromptNone;
	}

	public Boolean getSessionIdRequestParameterEnabled() {
		return sessionIdRequestParameterEnabled;
	}

	public void setSessionIdRequestParameterEnabled(Boolean sessionIdRequestParameterEnabled) {
		this.sessionIdRequestParameterEnabled = sessionIdRequestParameterEnabled;
	}

	public Boolean getChangeSessionIdOnAuthentication() {
		return changeSessionIdOnAuthentication;
	}

	public void setChangeSessionIdOnAuthentication(Boolean changeSessionIdOnAuthentication) {
		this.changeSessionIdOnAuthentication = changeSessionIdOnAuthentication;
	}

	public Boolean getSessionIdPersistInCache() {
		return sessionIdPersistInCache;
	}

	public void setSessionIdPersistInCache(Boolean sessionIdPersistInCache) {
		this.sessionIdPersistInCache = sessionIdPersistInCache;
	}

	public Integer getSessionIdLifetime() {
		return sessionIdLifetime;
	}

	public void setSessionIdLifetime(Integer sessionIdLifetime) {
		this.sessionIdLifetime = sessionIdLifetime;
	}

	public Integer getServerSessionIdLifetime() {
		return serverSessionIdLifetime;
	}

	public void setServerSessionIdLifetime(Integer serverSessionIdLifetime) {
		this.serverSessionIdLifetime = serverSessionIdLifetime;
	}

	public int getConfigurationUpdateInterval() {
		return configurationUpdateInterval;
	}

	public void setConfigurationUpdateInterval(int configurationUpdateInterval) {
		this.configurationUpdateInterval = configurationUpdateInterval;
	}

	public Boolean getLogNotFoundEntityAsError() {
		return logNotFoundEntityAsError;
	}

	public void setLogNotFoundEntityAsError(Boolean logNotFoundEntityAsError) {
		this.logNotFoundEntityAsError = logNotFoundEntityAsError;
	}

	public Boolean getEnableClientGrantTypeUpdate() {
		return enableClientGrantTypeUpdate;
	}

	public void setEnableClientGrantTypeUpdate(Boolean enableClientGrantTypeUpdate) {
		this.enableClientGrantTypeUpdate = enableClientGrantTypeUpdate;
	}

	public Set<GrantType> getDynamicGrantTypeDefault() {
		return dynamicGrantTypeDefault;
	}

	public void setDynamicGrantTypeDefault(Set<GrantType> dynamicGrantTypeDefault) {
		this.dynamicGrantTypeDefault = dynamicGrantTypeDefault;
	}

	public String getCssLocation() {
		return cssLocation;
	}

	public void setCssLocation(String cssLocation) {
		this.cssLocation = cssLocation;
	}

	public String getJsLocation() {
		return jsLocation;
	}

	public void setJsLocation(String jsLocation) {
		this.jsLocation = jsLocation;
	}

	public String getImgLocation() {
		return imgLocation;
	}

	public void setImgLocation(String imgLocation) {
		this.imgLocation = imgLocation;
	}

	public int getMetricReporterInterval() {
		return metricReporterInterval;
	}

	public void setMetricReporterInterval(int metricReporterInterval) {
		this.metricReporterInterval = metricReporterInterval;
	}

	public int getMetricReporterKeepDataDays() {
		return metricReporterKeepDataDays;
	}

	public void setMetricReporterKeepDataDays(int metricReporterKeepDataDays) {
		this.metricReporterKeepDataDays = metricReporterKeepDataDays;
	}

	public Boolean getMetricReporterEnabled() {
		return metricReporterEnabled;
	}

	public void setMetricReporterEnabled(Boolean metricReporterEnabled) {
		this.metricReporterEnabled = metricReporterEnabled;
	}

	public String getPairwiseIdType() {
		return pairwiseIdType;
	}

	public void setPairwiseIdType(String pairwiseIdType) {
		this.pairwiseIdType = pairwiseIdType;
	}

	public String getPairwiseCalculationKey() {
		return pairwiseCalculationKey;
	}

	public void setPairwiseCalculationKey(String pairwiseCalculationKey) {
		this.pairwiseCalculationKey = pairwiseCalculationKey;
	}

	public String getPairwiseCalculationSalt() {
		return pairwiseCalculationSalt;
	}

	public void setPairwiseCalculationSalt(String pairwiseCalculationSalt) {
		this.pairwiseCalculationSalt = pairwiseCalculationSalt;
	}

	public Boolean getShareSubjectIdBetweenClientsWithSameSectorId() {
		return shareSubjectIdBetweenClientsWithSameSectorId;
	}

	public void setShareSubjectIdBetweenClientsWithSameSectorId(Boolean shareSubjectIdBetweenClientsWithSameSectorId) {
		this.shareSubjectIdBetweenClientsWithSameSectorId = shareSubjectIdBetweenClientsWithSameSectorId;
	}

	public Boolean getSubjectIdentifierBasedOnWholeUriBackwardCompatibility() {
		return subjectIdentifierBasedOnWholeUriBackwardCompatibility;
	}

	public void setSubjectIdentifierBasedOnWholeUriBackwardCompatibility(
			Boolean subjectIdentifierBasedOnWholeUriBackwardCompatibility) {
		this.subjectIdentifierBasedOnWholeUriBackwardCompatibility = subjectIdentifierBasedOnWholeUriBackwardCompatibility;
	}

	public WebKeyStorage getWebKeysStorage() {
		return webKeysStorage;
	}

	public void setWebKeysStorage(WebKeyStorage webKeysStorage) {
		this.webKeysStorage = webKeysStorage;
	}

	public String getDnName() {
		return dnName;
	}

	public void setDnName(String dnName) {
		this.dnName = dnName;
	}

	public String getKeyStoreFile() {
		return keyStoreFile;
	}

	public void setKeyStoreFile(String keyStoreFile) {
		this.keyStoreFile = keyStoreFile;
	}

	public String getKeyStoreSecret() {
		return keyStoreSecret;
	}

	public void setKeyStoreSecret(String keyStoreSecret) {
		this.keyStoreSecret = keyStoreSecret;
	}

	public KeySelectionStrategy getKeySelectionStrategy() {
		return keySelectionStrategy;
	}

	public void setKeySelectionStrategy(KeySelectionStrategy keySelectionStrategy) {
		this.keySelectionStrategy = keySelectionStrategy;
	}

	public List<String> getKeyAlgsAllowedForGeneration() {
		return keyAlgsAllowedForGeneration;
	}

	public void setKeyAlgsAllowedForGeneration(List<String> keyAlgsAllowedForGeneration) {
		this.keyAlgsAllowedForGeneration = keyAlgsAllowedForGeneration;
	}

	public String getOxElevenTestModeToken() {
		return oxElevenTestModeToken;
	}

	public void setOxElevenTestModeToken(String oxElevenTestModeToken) {
		this.oxElevenTestModeToken = oxElevenTestModeToken;
	}

	public String getOxElevenGenerateKeyEndpoint() {
		return oxElevenGenerateKeyEndpoint;
	}

	public void setOxElevenGenerateKeyEndpoint(String oxElevenGenerateKeyEndpoint) {
		this.oxElevenGenerateKeyEndpoint = oxElevenGenerateKeyEndpoint;
	}

	public String getOxElevenSignEndpoint() {
		return oxElevenSignEndpoint;
	}

	public void setOxElevenSignEndpoint(String oxElevenSignEndpoint) {
		this.oxElevenSignEndpoint = oxElevenSignEndpoint;
	}

	public String getOxElevenVerifySignatureEndpoint() {
		return oxElevenVerifySignatureEndpoint;
	}

	public void setOxElevenVerifySignatureEndpoint(String oxElevenVerifySignatureEndpoint) {
		this.oxElevenVerifySignatureEndpoint = oxElevenVerifySignatureEndpoint;
	}

	public String getOxElevenDeleteKeyEndpoint() {
		return oxElevenDeleteKeyEndpoint;
	}

	public void setOxElevenDeleteKeyEndpoint(String oxElevenDeleteKeyEndpoint) {
		this.oxElevenDeleteKeyEndpoint = oxElevenDeleteKeyEndpoint;
	}

	public Boolean getIntrospectionAccessTokenMustHaveUmaProtectionScope() {
		return introspectionAccessTokenMustHaveUmaProtectionScope;
	}

	public void setIntrospectionAccessTokenMustHaveUmaProtectionScope(
			Boolean introspectionAccessTokenMustHaveUmaProtectionScope) {
		this.introspectionAccessTokenMustHaveUmaProtectionScope = introspectionAccessTokenMustHaveUmaProtectionScope;
	}

	public Boolean getIntrospectionSkipAuthorization() {
		return introspectionSkipAuthorization;
	}

	public void setIntrospectionSkipAuthorization(Boolean introspectionSkipAuthorization) {
		this.introspectionSkipAuthorization = introspectionSkipAuthorization;
	}

	public Boolean getIntrospectionRestrictBasicAuthnToOwnTokens() {
		return introspectionRestrictBasicAuthnToOwnTokens;
	}

	public void setIntrospectionRestrictBasicAuthnToOwnTokens(Boolean introspectionRestrictBasicAuthnToOwnTokens) {
		this.introspectionRestrictBasicAuthnToOwnTokens = introspectionRestrictBasicAuthnToOwnTokens;
	}

	public Boolean getEndSessionWithAccessToken() {
		return endSessionWithAccessToken;
	}

	public void setEndSessionWithAccessToken(Boolean endSessionWithAccessToken) {
		this.endSessionWithAccessToken = endSessionWithAccessToken;
	}

	public String getCookieDomain() {
		return cookieDomain;
	}

	public void setCookieDomain(String cookieDomain) {
		this.cookieDomain = cookieDomain;
	}

	public Boolean getEnabledOAuthAuditLogging() {
		return enabledOAuthAuditLogging;
	}

	public void setEnabledOAuthAuditLogging(Boolean enabledOAuthAuditLogging) {
		this.enabledOAuthAuditLogging = enabledOAuthAuditLogging;
	}

	public Set<String> getJmsBrokerURISet() {
		return jmsBrokerURISet;
	}

	public void setJmsBrokerURISet(Set<String> jmsBrokerURISet) {
		this.jmsBrokerURISet = jmsBrokerURISet;
	}

	public String getJmsUserName() {
		return jmsUserName;
	}

	public void setJmsUserName(String jmsUserName) {
		this.jmsUserName = jmsUserName;
	}

	public String getJmsPassword() {
		return jmsPassword;
	}

	public void setJmsPassword(String jmsPassword) {
		this.jmsPassword = jmsPassword;
	}

	public Boolean getAllowWildcardRedirectUri() {
		return allowWildcardRedirectUri;
	}

	public void setAllowWildcardRedirectUri(Boolean allowWildcardRedirectUri) {
		this.allowWildcardRedirectUri = allowWildcardRedirectUri;
	}

	public List<String> getClientWhiteList() {
		return clientWhiteList;
	}

	public void setClientWhiteList(List<String> clientWhiteList) {
		this.clientWhiteList = clientWhiteList;
	}

	public List<String> getClientBlackList() {
		return clientBlackList;
	}

	public void setClientBlackList(List<String> clientBlackList) {
		this.clientBlackList = clientBlackList;
	}

	public Boolean getLegacyIdTokenClaims() {
		return legacyIdTokenClaims;
	}

	public void setLegacyIdTokenClaims(Boolean legacyIdTokenClaims) {
		this.legacyIdTokenClaims = legacyIdTokenClaims;
	}

	public Boolean getCustomHeadersWithAuthorizationResponse() {
		return customHeadersWithAuthorizationResponse;
	}

	public void setCustomHeadersWithAuthorizationResponse(Boolean customHeadersWithAuthorizationResponse) {
		this.customHeadersWithAuthorizationResponse = customHeadersWithAuthorizationResponse;
	}

	public Boolean getFrontChannelLogoutSessionSupported() {
		return frontChannelLogoutSessionSupported;
	}

	public void setFrontChannelLogoutSessionSupported(Boolean frontChannelLogoutSessionSupported) {
		this.frontChannelLogoutSessionSupported = frontChannelLogoutSessionSupported;
	}

	public String getLoggingLevel() {
		return loggingLevel;
	}

	public void setLoggingLevel(String loggingLevel) {
		this.loggingLevel = loggingLevel;
	}

	public String getLoggingLayout() {
		return loggingLayout;
	}

	public void setLoggingLayout(String loggingLayout) {
		this.loggingLayout = loggingLayout;
	}

	public Boolean getUpdateUserLastLogonTime() {
		return updateUserLastLogonTime;
	}

	public void setUpdateUserLastLogonTime(Boolean updateUserLastLogonTime) {
		this.updateUserLastLogonTime = updateUserLastLogonTime;
	}

	public Boolean getUpdateClientAccessTime() {
		return updateClientAccessTime;
	}

	public void setUpdateClientAccessTime(Boolean updateClientAccessTime) {
		this.updateClientAccessTime = updateClientAccessTime;
	}

	public Boolean getLogClientIdOnClientAuthentication() {
		return logClientIdOnClientAuthentication;
	}

	public void setLogClientIdOnClientAuthentication(Boolean logClientIdOnClientAuthentication) {
		this.logClientIdOnClientAuthentication = logClientIdOnClientAuthentication;
	}

	public Boolean getLogClientNameOnClientAuthentication() {
		return logClientNameOnClientAuthentication;
	}

	public void setLogClientNameOnClientAuthentication(Boolean logClientNameOnClientAuthentication) {
		this.logClientNameOnClientAuthentication = logClientNameOnClientAuthentication;
	}

	public Boolean getDisableJdkLogger() {
		return disableJdkLogger;
	}

	public void setDisableJdkLogger(Boolean disableJdkLogger) {
		this.disableJdkLogger = disableJdkLogger;
	}

	public Set<String> getAuthorizationRequestCustomAllowedParameters() {
		return authorizationRequestCustomAllowedParameters;
	}

	public void setAuthorizationRequestCustomAllowedParameters(Set<String> authorizationRequestCustomAllowedParameters) {
		this.authorizationRequestCustomAllowedParameters = authorizationRequestCustomAllowedParameters;
	}

	public Boolean getLegacyDynamicRegistrationScopeParam() {
		return legacyDynamicRegistrationScopeParam;
	}

	public void setLegacyDynamicRegistrationScopeParam(Boolean legacyDynamicRegistrationScopeParam) {
		this.legacyDynamicRegistrationScopeParam = legacyDynamicRegistrationScopeParam;
	}

	public Boolean getOpenidScopeBackwardCompatibility() {
		return openidScopeBackwardCompatibility;
	}

	public void setOpenidScopeBackwardCompatibility(Boolean openidScopeBackwardCompatibility) {
		this.openidScopeBackwardCompatibility = openidScopeBackwardCompatibility;
	}

	public Boolean getDisableU2fEndpoint() {
		return disableU2fEndpoint;
	}

	public void setDisableU2fEndpoint(Boolean disableU2fEndpoint) {
		this.disableU2fEndpoint = disableU2fEndpoint;
	}

	public Boolean getUseLocalCache() {
		return useLocalCache;
	}

	public void setUseLocalCache(Boolean useLocalCache) {
		this.useLocalCache = useLocalCache;
	}

	public Boolean getFapiCompatibility() {
		return fapiCompatibility;
	}

	public void setFapiCompatibility(Boolean fapiCompatibility) {
		this.fapiCompatibility = fapiCompatibility;
	}

	public Boolean getForceIdTokenHintPrecense() {
		return forceIdTokenHintPrecense;
	}

	public void setForceIdTokenHintPrecense(Boolean forceIdTokenHintPrecense) {
		this.forceIdTokenHintPrecense = forceIdTokenHintPrecense;
	}

	public Boolean getRejectEndSessionIfIdTokenExpired() {
		return rejectEndSessionIfIdTokenExpired;
	}

	public void setRejectEndSessionIfIdTokenExpired(Boolean rejectEndSessionIfIdTokenExpired) {
		this.rejectEndSessionIfIdTokenExpired = rejectEndSessionIfIdTokenExpired;
	}

	public Boolean getAllowEndSessionWithUnmatchedSid() {
		return allowEndSessionWithUnmatchedSid;
	}

	public void setAllowEndSessionWithUnmatchedSid(Boolean allowEndSessionWithUnmatchedSid) {
		this.allowEndSessionWithUnmatchedSid = allowEndSessionWithUnmatchedSid;
	}

	public Boolean getForceOfflineAccessScopeToEnableRefreshToken() {
		return forceOfflineAccessScopeToEnableRefreshToken;
	}

	public void setForceOfflineAccessScopeToEnableRefreshToken(Boolean forceOfflineAccessScopeToEnableRefreshToken) {
		this.forceOfflineAccessScopeToEnableRefreshToken = forceOfflineAccessScopeToEnableRefreshToken;
	}

	public Boolean getErrorReasonEnabled() {
		return errorReasonEnabled;
	}

	public void setErrorReasonEnabled(Boolean errorReasonEnabled) {
		this.errorReasonEnabled = errorReasonEnabled;
	}

	public Boolean getRemoveRefreshTokensForClientOnLogout() {
		return removeRefreshTokensForClientOnLogout;
	}

	public void setRemoveRefreshTokensForClientOnLogout(Boolean removeRefreshTokensForClientOnLogout) {
		this.removeRefreshTokensForClientOnLogout = removeRefreshTokensForClientOnLogout;
	}

	public Boolean getSkipRefreshTokenDuringRefreshing() {
		return skipRefreshTokenDuringRefreshing;
	}

	public void setSkipRefreshTokenDuringRefreshing(Boolean skipRefreshTokenDuringRefreshing) {
		this.skipRefreshTokenDuringRefreshing = skipRefreshTokenDuringRefreshing;
	}

	public Boolean getRefreshTokenExtendLifetimeOnRotation() {
		return refreshTokenExtendLifetimeOnRotation;
	}

	public void setRefreshTokenExtendLifetimeOnRotation(Boolean refreshTokenExtendLifetimeOnRotation) {
		this.refreshTokenExtendLifetimeOnRotation = refreshTokenExtendLifetimeOnRotation;
	}

	public Boolean getCheckUserPresenceOnRefreshToken() {
		return checkUserPresenceOnRefreshToken;
	}

	public void setCheckUserPresenceOnRefreshToken(Boolean checkUserPresenceOnRefreshToken) {
		this.checkUserPresenceOnRefreshToken = checkUserPresenceOnRefreshToken;
	}

	public Boolean getConsentGatheringScriptBackwardCompatibility() {
		return consentGatheringScriptBackwardCompatibility;
	}

	public void setConsentGatheringScriptBackwardCompatibility(Boolean consentGatheringScriptBackwardCompatibility) {
		this.consentGatheringScriptBackwardCompatibility = consentGatheringScriptBackwardCompatibility;
	}

	public Boolean getIntrospectionScriptBackwardCompatibility() {
		return introspectionScriptBackwardCompatibility;
	}

	public void setIntrospectionScriptBackwardCompatibility(Boolean introspectionScriptBackwardCompatibility) {
		this.introspectionScriptBackwardCompatibility = introspectionScriptBackwardCompatibility;
	}

	public Boolean getIntrospectionResponseScopesBackwardCompatibility() {
		return introspectionResponseScopesBackwardCompatibility;
	}

	public void setIntrospectionResponseScopesBackwardCompatibility(
			Boolean introspectionResponseScopesBackwardCompatibility) {
		this.introspectionResponseScopesBackwardCompatibility = introspectionResponseScopesBackwardCompatibility;
	}

	public Boolean getClientAuthorizationBackwardCompatibility() {
		return clientAuthorizationBackwardCompatibility;
	}

	public void setClientAuthorizationBackwardCompatibility(Boolean clientAuthorizationBackwardCompatibility) {
		this.clientAuthorizationBackwardCompatibility = clientAuthorizationBackwardCompatibility;
	}

	public String getSoftwareStatementValidationType() {
		return softwareStatementValidationType;
	}

	public void setSoftwareStatementValidationType(String softwareStatementValidationType) {
		this.softwareStatementValidationType = softwareStatementValidationType;
	}

	public String getSoftwareStatementValidationClaimName() {
		return softwareStatementValidationClaimName;
	}

	public void setSoftwareStatementValidationClaimName(String softwareStatementValidationClaimName) {
		this.softwareStatementValidationClaimName = softwareStatementValidationClaimName;
	}

	public AuthenticationProtectionConfiguration getAuthenticationProtectionConfiguration() {
		return authenticationProtectionConfiguration;
	}

	public void setAuthenticationProtectionConfiguration(
			AuthenticationProtectionConfiguration authenticationProtectionConfiguration) {
		this.authenticationProtectionConfiguration = authenticationProtectionConfiguration;
	}

	public ErrorHandlingMethod getErrorHandlingMethod() {
		return errorHandlingMethod;
	}

	public void setErrorHandlingMethod(ErrorHandlingMethod errorHandlingMethod) {
		this.errorHandlingMethod = errorHandlingMethod;
	}

	public Boolean getKeepAuthenticatorAttributesOnAcrChange() {
		return keepAuthenticatorAttributesOnAcrChange;
	}

	public void setKeepAuthenticatorAttributesOnAcrChange(Boolean keepAuthenticatorAttributesOnAcrChange) {
		this.keepAuthenticatorAttributesOnAcrChange = keepAuthenticatorAttributesOnAcrChange;
	}

	public Boolean getDisableAuthnForMaxAgeZero() {
		return disableAuthnForMaxAgeZero;
	}

	public void setDisableAuthnForMaxAgeZero(Boolean disableAuthnForMaxAgeZero) {
		this.disableAuthnForMaxAgeZero = disableAuthnForMaxAgeZero;
	}

	public int getDeviceAuthzRequestExpiresIn() {
		return deviceAuthzRequestExpiresIn;
	}

	public void setDeviceAuthzRequestExpiresIn(int deviceAuthzRequestExpiresIn) {
		this.deviceAuthzRequestExpiresIn = deviceAuthzRequestExpiresIn;
	}

	public int getDeviceAuthzTokenPollInterval() {
		return deviceAuthzTokenPollInterval;
	}

	public void setDeviceAuthzTokenPollInterval(int deviceAuthzTokenPollInterval) {
		this.deviceAuthzTokenPollInterval = deviceAuthzTokenPollInterval;
	}

	public String getDeviceAuthzResponseTypeToProcessAuthz() {
		return deviceAuthzResponseTypeToProcessAuthz;
	}

	public void setDeviceAuthzResponseTypeToProcessAuthz(String deviceAuthzResponseTypeToProcessAuthz) {
		this.deviceAuthzResponseTypeToProcessAuthz = deviceAuthzResponseTypeToProcessAuthz;
	}

	public String getBackchannelClientId() {
		return backchannelClientId;
	}

	public void setBackchannelClientId(String backchannelClientId) {
		this.backchannelClientId = backchannelClientId;
	}

	public String getBackchannelRedirectUri() {
		return backchannelRedirectUri;
	}

	public void setBackchannelRedirectUri(String backchannelRedirectUri) {
		this.backchannelRedirectUri = backchannelRedirectUri;
	}

	public String getBackchannelAuthenticationEndpoint() {
		return backchannelAuthenticationEndpoint;
	}

	public void setBackchannelAuthenticationEndpoint(String backchannelAuthenticationEndpoint) {
		this.backchannelAuthenticationEndpoint = backchannelAuthenticationEndpoint;
	}

	public String getBackchannelDeviceRegistrationEndpoint() {
		return backchannelDeviceRegistrationEndpoint;
	}

	public void setBackchannelDeviceRegistrationEndpoint(String backchannelDeviceRegistrationEndpoint) {
		this.backchannelDeviceRegistrationEndpoint = backchannelDeviceRegistrationEndpoint;
	}

	public List<String> getBackchannelTokenDeliveryModesSupported() {
		return backchannelTokenDeliveryModesSupported;
	}

	public void setBackchannelTokenDeliveryModesSupported(List<String> backchannelTokenDeliveryModesSupported) {
		this.backchannelTokenDeliveryModesSupported = backchannelTokenDeliveryModesSupported;
	}

	public List<String> getBackchannelAuthenticationRequestSigningAlgValuesSupported() {
		return backchannelAuthenticationRequestSigningAlgValuesSupported;
	}

	public void setBackchannelAuthenticationRequestSigningAlgValuesSupported(
			List<String> backchannelAuthenticationRequestSigningAlgValuesSupported) {
		this.backchannelAuthenticationRequestSigningAlgValuesSupported = backchannelAuthenticationRequestSigningAlgValuesSupported;
	}

	public Boolean getBackchannelUserCodeParameterSupported() {
		return backchannelUserCodeParameterSupported;
	}

	public void setBackchannelUserCodeParameterSupported(Boolean backchannelUserCodeParameterSupported) {
		this.backchannelUserCodeParameterSupported = backchannelUserCodeParameterSupported;
	}

	public String getBackchannelBindingMessagePattern() {
		return backchannelBindingMessagePattern;
	}

	public void setBackchannelBindingMessagePattern(String backchannelBindingMessagePattern) {
		this.backchannelBindingMessagePattern = backchannelBindingMessagePattern;
	}

	public int getBackchannelAuthenticationResponseExpiresIn() {
		return backchannelAuthenticationResponseExpiresIn;
	}

	public void setBackchannelAuthenticationResponseExpiresIn(int backchannelAuthenticationResponseExpiresIn) {
		this.backchannelAuthenticationResponseExpiresIn = backchannelAuthenticationResponseExpiresIn;
	}

	public int getBackchannelAuthenticationResponseInterval() {
		return backchannelAuthenticationResponseInterval;
	}

	public void setBackchannelAuthenticationResponseInterval(int backchannelAuthenticationResponseInterval) {
		this.backchannelAuthenticationResponseInterval = backchannelAuthenticationResponseInterval;
	}

	public List<String> getBackchannelLoginHintClaims() {
		return backchannelLoginHintClaims;
	}

	public void setBackchannelLoginHintClaims(List<String> backchannelLoginHintClaims) {
		this.backchannelLoginHintClaims = backchannelLoginHintClaims;
	}

	public CIBAEndUserNotificationConfig getCibaEndUserNotificationConfig() {
		return cibaEndUserNotificationConfig;
	}

	public void setCibaEndUserNotificationConfig(CIBAEndUserNotificationConfig cibaEndUserNotificationConfig) {
		this.cibaEndUserNotificationConfig = cibaEndUserNotificationConfig;
	}

	public int getBackchannelRequestsProcessorJobIntervalSec() {
		return backchannelRequestsProcessorJobIntervalSec;
	}

	public void setBackchannelRequestsProcessorJobIntervalSec(int backchannelRequestsProcessorJobIntervalSec) {
		this.backchannelRequestsProcessorJobIntervalSec = backchannelRequestsProcessorJobIntervalSec;
	}

	public int getBackchannelRequestsProcessorJobChunkSize() {
		return backchannelRequestsProcessorJobChunkSize;
	}

	public void setBackchannelRequestsProcessorJobChunkSize(int backchannelRequestsProcessorJobChunkSize) {
		this.backchannelRequestsProcessorJobChunkSize = backchannelRequestsProcessorJobChunkSize;
	}

	public int getCibaGrantLifeExtraTimeSec() {
		return cibaGrantLifeExtraTimeSec;
	}

	public void setCibaGrantLifeExtraTimeSec(int cibaGrantLifeExtraTimeSec) {
		this.cibaGrantLifeExtraTimeSec = cibaGrantLifeExtraTimeSec;
	}

	public int getCibaMaxExpirationTimeAllowedSec() {
		return cibaMaxExpirationTimeAllowedSec;
	}

	public void setCibaMaxExpirationTimeAllowedSec(int cibaMaxExpirationTimeAllowedSec) {
		this.cibaMaxExpirationTimeAllowedSec = cibaMaxExpirationTimeAllowedSec;
	}

	public Boolean getCibaEnabled() {
		return cibaEnabled;
	}

	public void setCibaEnabled(Boolean cibaEnabled) {
		this.cibaEnabled = cibaEnabled;
	}

	public Boolean getReturn200OnClientRegistration() {
		return return200OnClientRegistration;
	}

	public void setReturn200OnClientRegistration(Boolean return200OnClientRegistration) {
		this.return200OnClientRegistration = return200OnClientRegistration;
	}

	public Map<String, String> getDateFormatterPatterns() {
		return dateFormatterPatterns;
	}

	public void setDateFormatterPatterns(Map<String, String> dateFormatterPatterns) {
		this.dateFormatterPatterns = dateFormatterPatterns;
	}

	public Boolean getAllowBlankValuesInDiscoveryResponse() {
		return allowBlankValuesInDiscoveryResponse;
	}

	public void setAllowBlankValuesInDiscoveryResponse(Boolean allowBlankValuesInDiscoveryResponse) {
		this.allowBlankValuesInDiscoveryResponse = allowBlankValuesInDiscoveryResponse;
	}

	public Boolean getSkipAuthenticationFilterOptionsMethod() {
		return skipAuthenticationFilterOptionsMethod;
	}

	public void setSkipAuthenticationFilterOptionsMethod(Boolean skipAuthenticationFilterOptionsMethod) {
		this.skipAuthenticationFilterOptionsMethod = skipAuthenticationFilterOptionsMethod;
	}


}