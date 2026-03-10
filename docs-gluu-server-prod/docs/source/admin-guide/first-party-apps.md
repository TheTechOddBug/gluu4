# OAuth 2.0 for First-Party Applications


Authorization Challenge Endpoint allows first-party applications obtain authorization code which later can be exchanged on access token.
This can provide an entirely browserless OAuth 2.0 experience suited for native applications.

This endpoint conforms to [OAuth 2.0 for First-Party Native Applications](https://www.ietf.org/archive/id/draft-parecki-oauth-first-party-native-apps-02.html) specifications.

URL to access authorization challenge endpoint on Authorization Server is listed in the response of well-known
[discovery endpoint](./openid-connect.md) given below.

```text
https://server.host/.well-known/openid-configuration
```

`authorization_challenge_endpoint` claim in the response specifies the URL for authorization challenge endpoint. By default, authorization 
challenge endpoint looks like below:

```
https://server.host/oxauth/restv1/authorize-challenge
```

More information about request and response of the authorization challenge endpoint can be found in the OpenAPI specification 
of [oxauth server module](https://gluu.org/swagger-ui/?url=https://raw.githubusercontent.com/GluuFederation/gluu4/4.5/docs/oxAuthSwagger.yaml#/Authorization_Challenge).

Sample request
```
POST /authorize-challenge HTTP/1.1
Host: server.example.com
Content-Type: application/x-www-form-urlencoded

login_hint=%2B1-310-123-4567&scope=profile
&client_id=bb16c14c73415
```

Sample successful response with `authorization_code`.
```
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Cache-Control: no-store

{
  "authorization_code": "uY29tL2F1dGhlbnRpY"
}
```

Sample error response
```
HTTP/1.1 401 Unauthorized
Content-Type: application/json
Cache-Control: no-store

{
  "error": "username_required"
}
```


## Configuration Properties

Authorization Challenge Endpoint AS configuration:

- **authorizationChallengeDefaultAcr** - Authorization Challenge Endpoint Default ACR if no value is specified in acr_values request parameter. Default value is `default_challenge`.
- **authorizationChallengeShouldGenerateSession** - Boolean value specifying whether to generate session_id (AS object and cookie) during authorization at Authorization Challenge Endpoint. Default value is `false`.
- **authorizationChallengeSessionLifetimeInSeconds** - Boolean value specifying whether to generate session_id (AS object and cookie) during authorization at Authorization Challenge Endpoint. Default value is `false`.

## Custom script  

AS provides `AuthorizationChallengeType` custom script which must be used to control Authorization Challenge Endpoint behaviour.

If request does not have `acr_values` specified and script name falls back to `default_challenge` which is available and enabled during installation.
Default script name can be changed via `authorizationChallengeDefaultAcr` configuration property. 

Main method returns true/false which indicates to server whether to issue `authorization_code` in response or not.

If parameters is not present then error has to be created and `false` returned.
If all is good script has to return `true` and it's strongly recommended to set user `context.getExecutionContext().setUser(user);` so AS can keep tracking what exactly user is authenticated.

Please see following snippet below:

```python
    def authorize(self, context):
        # 1. As first step we get username
        username = self.getParameterOrCreateError(context, "username")
        if StringUtils.isBlank(username):
            return False

        # 2. OTP validation
        otp = self.getParameterOrCreateError(context, "otp")
        if StringUtils.isBlank(otp):
            return False

        print "All required parameters are present"

        # Main authorization logic
        userService = CdiUtil.bean(UserService)
        entryManager = CdiUtil.bean(PersistenceEntryManager)

        user = userService.getUser(username)
        if user is None:
            print "User is not found"
            self.createError(context, "username_invalid")
            return False

        isUserActive = StringUtils.equalsIgnoreCase(user.getStatus(), "ACTIVE")
        if not isUserActive:
            print "User is not active"
            self.createError(context, "username_inactive")
            return False

        ok = entryManager.authenticate(user.getDn(), User, otp)
        if ok:
            context.getExecutionContext().setUser(user)
            print "User is authenticated successfully."
            return True

        # Error case
        print "Failed to authenticate user. Please check username and OTP."
        self.createError(context, "username_or_otp_invalid")
        return False
```

More details in [Custom Script Page](./custom-script.md).

Full sample script can be found [here](./authorization_challenge.py)

## Auth session

Auth session is optional. AS does not return it by default. 
It's possible to pass in request `use_auth_session=true` which makes AS return it in error response.

## Full successful Authorization Challenge Flow sample

```
#######################################################
TEST: OpenID Connect Discovery
#######################################################
-------------------------------------------------------
REQUEST:
-------------------------------------------------------
GET /.well-known/webfinger HTTP/1.1?resource=https%3A%2F%2Fyuriyz-shining-squirrel.gluu.info&rel=http%3A%2F%2Fopenid.net%2Fspecs%2Fconnect%2F1.0%2Fissuer HTTP/1.1
Host: yuriyz-shining-squirrel.gluu.info

-------------------------------------------------------
RESPONSE:
-------------------------------------------------------
HTTP/1.1 200
Connection: Keep-Alive
Content-Length: 208
Content-Type: application/jrd+json;charset=iso-8859-1
Date: Fri, 18 Oct 2024 14:45:46 GMT
Keep-Alive: timeout=5, max=100
Server: Apache/2.4.52 (Ubuntu)
Strict-Transport-Security: max-age=31536000; includeSubDomains
X-Content-Type-Options: nosniff
X-Xss-Protection: 1; mode=block

{
    "subject": "https://yuriyz-shining-squirrel.gluu.info",
    "links": [{
        "rel": "http://openid.net/specs/connect/1.0/issuer",
        "href": "https://yuriyz-shining-squirrel.gluu.info"
    }]
}


OpenID Connect Configuration
-------------------------------------------------------
REQUEST:
-------------------------------------------------------
GET /.well-known/openid-configuration HTTP/1.1 HTTP/1.1
Host: yuriyz-shining-squirrel.gluu.info

-------------------------------------------------------
RESPONSE:
-------------------------------------------------------
HTTP/1.1 200
Connection: Keep-Alive
Content-Length: 14690
Content-Type: application/json
Date: Fri, 18 Oct 2024 14:45:46 GMT
Keep-Alive: timeout=5, max=100
Server: Apache/2.4.52 (Ubuntu)
Strict-Transport-Security: max-age=31536000; includeSubDomains
X-Content-Type-Options: nosniff
X-Xss-Protection: 1; mode=block

{
  "request_parameter_supported" : true,
  "token_revocation_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/revoke",
  "introspection_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/introspection",
  "claims_parameter_supported" : false,
  "issuer" : "https://yuriyz-shining-squirrel.gluu.info",
  "userinfo_encryption_enc_values_supported" : [ "RSA1_5", "RSA-OAEP", "A128KW", "A256KW" ],
  "id_token_encryption_enc_values_supported" : [ "A128CBC+HS256", "A256CBC+HS512", "A128GCM", "A256GCM" ],
  "authorization_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/authorize",
  "service_documentation" : "http://gluu.org/docs",
  "id_generation_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/id",
  "claims_supported" : [ "street_address", "country", "zoneinfo", "birthdate", "role", "gender", "formatted", "user_name", "phone_mobile_number", "preferred_username", "locale", "inum", "updated_at", "post_office_box", "nickname", "preferred_language", "email", "website", "email_verified", "profile", "locality", "phone_number_verified", "room_number", "given_name", "middle_name", "picture", "name", "phone_number", "postal_code", "region", "family_name" ],
  "scope_to_claims_mapping" : [ {
    "https://gluu.org/auth/oxtrust.scope.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.smtpconfiguration.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.umaresource.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.oxtrustconfiguration.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.rptConfig.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.sectoridentifier.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.gluuradiusclient.read" : [ ]
  }, {
    "profile" : [ "name", "family_name", "given_name", "middle_name", "nickname", "preferred_username", "profile", "picture", "website", "gender", "birthdate", "zoneinfo", "locale", "updated_at", "preferred_language" ]
  }, {
    "https://gluu.org/auth/oxtrust.certificates.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.systemconfig.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.umascope.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.metricconfig.read" : [ ]
  }, {
    "openid" : [ ]
  }, {
    "permission" : [ "role" ]
  }, {
    "https://gluu.org/auth/oxtrust.scimconfig.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.captchaconfig.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.group.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.people.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.serverstatus.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.attribute.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.smtpconfiguration.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.apiconfig.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.OxauthjsonSetting.read" : [ ]
  }, {
    "super_gluu_ro_session" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.passportconfig.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.gluuradiusclient.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.oxtrustconfiguration.write" : [ ]
  }, {
    "phone" : [ "phone_number_verified", "phone_number" ]
  }, {
    "https://gluu.org/auth/oxtrust.trustedidp.read" : [ ]
  }, {
    "revoke_session" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.apiconfig.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.sectoridentifier.read" : [ ]
  }, {
    "address" : [ "formatted", "postal_code", "street_address", "locality", "country", "room_number", "region", "post_office_box" ]
  }, {
    "https://gluu.org/auth/oxtrust.authenticationmethod.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.passportbasicconfig.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.idpconfig.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.oxtrustjsonSetting.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.passportbasicconfig.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.passportprovider.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.ldapauthentication.read" : [ ]
  }, {
    "clientinfo" : [ "name", "inum" ]
  }, {
    "https://gluu.org/auth/oxtrust.configuration.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.oxauthjsonSetting.write" : [ ]
  }, {
    "mobile_phone" : [ "phone_mobile_number" ]
  }, {
    "https://gluu.org/auth/oxtrust.customscript.write" : [ ]
  }, {
    "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/uma/scopes/config_api_access" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.attribute.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.captchaconfig.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.customscript.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.oxtrustsetting.read" : [ ]
  }, {
    "email" : [ "email_verified", "email" ]
  }, {
    "user_name" : [ "user_name" ]
  }, {
    "https://gluu.org/auth/oxtrust.passportconfig.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.umascope.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.oxauthconfiguration.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.casprotocol.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.gluuradiusconfig.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.client.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.passportprovider.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.authenticationmethod.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.scimconfig.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.scope.read" : [ ]
  }, {
    "oxd" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.metricconfig.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.ldapauthentication.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.oxauthconfiguration.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.systemconfig.write" : [ ]
  }, {
    "uma_protection" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.saml.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.serverstatus.read" : [ ]
  }, {
    "offline_access" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.group.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.saml.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.oxtrustsetting.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.certificates.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.people.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.gluuradiusconfig.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.trustedidp.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.rptConfig.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.umaresource.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.configuration.read" : [ ]
  }, {
    "authorization_challenge" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.casprotocol.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.client.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.idpconfig.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.oxtrustjsonSetting.read" : [ ]
  } ],
  "token_endpoint_auth_methods_supported" : [ "client_secret_basic", "client_secret_post", "client_secret_jwt", "private_key_jwt", "tls_client_auth", "self_signed_tls_client_auth" ],
  "tls_client_certificate_bound_access_tokens" : true,
  "response_modes_supported" : [ "form_post", "query", "fragment" ],
  "backchannel_logout_session_supported" : true,
  "token_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/token",
  "response_types_supported" : [ "token", "token code", "token code id_token", "code", "id_token", "code id_token", "token id_token" ],
  "request_uri_parameter_supported" : true,
  "backchannel_user_code_parameter_supported" : false,
  "grant_types_supported" : [ "password", "implicit", "urn:ietf:params:oauth:grant-type:device_code", "authorization_code", "urn:ietf:params:oauth:grant-type:uma-ticket", "client_credentials", "refresh_token" ],
  "ui_locales_supported" : [ "en", "bg", "de", "es", "fr", "it", "ru", "tr" ],
  "userinfo_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/userinfo",
  "authorization_challenge_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/authorize-challenge",
  "auth_level_mapping" : {
    "-1" : [ "simple_password_auth" ]
  },
  "require_request_uri_registration" : false,
  "id_token_encryption_alg_values_supported" : [ "RSA1_5", "RSA-OAEP", "A128KW", "A256KW" ],
  "frontchannel_logout_session_supported" : true,
  "claims_locales_supported" : [ "en" ],
  "clientinfo_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/clientinfo",
  "request_object_signing_alg_values_supported" : [ "none", "HS256", "HS384", "HS512", "RS256", "RS384", "RS512", "ES256", "ES384", "ES512", "PS256", "PS384", "PS512" ],
  "request_object_encryption_alg_values_supported" : [ "RSA1_5", "RSA-OAEP", "A128KW", "A256KW" ],
  "session_revocation_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/revoke_session",
  "check_session_iframe" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/opiframe.htm",
  "scopes_supported" : [ "https://gluu.org/auth/oxtrust.group.read", "https://gluu.org/auth/oxtrust.group.write", "https://gluu.org/auth/oxtrust.gluuradiusconfig.write", "https://gluu.org/auth/oxtrust.trustedidp.write", "https://gluu.org/auth/oxtrust.saml.write", "https://gluu.org/auth/oxtrust.client.read", "https://gluu.org/auth/oxtrust.ldapauthentication.write", "https://gluu.org/auth/oxtrust.oxtrustconfiguration.write", "https://gluu.org/auth/oxtrust.scope.write", "https://gluu.org/auth/oxtrust.apiconfig.read", "https://gluu.org/auth/oxtrust.serverstatus.write", "https://gluu.org/auth/oxtrust.attribute.read", "https://gluu.org/auth/oxtrust.gluuradiusclient.write", "authorization_challenge", "super_gluu_ro_session", "https://gluu.org/auth/oxtrust.trustedidp.read", "https://gluu.org/auth/oxtrust.ldapauthentication.read", "https://gluu.org/auth/oxtrust.sectoridentifier.write", "https://gluu.org/auth/oxtrust.gluuradiusclient.read", "https://gluu.org/auth/oxtrust.authenticationmethod.read", "openid", "profile", "https://gluu.org/auth/oxtrust.configuration.read", "https://gluu.org/auth/oxtrust.systemconfig.write", "https://gluu.org/auth/oxtrust.oxtrustsetting.read", "https://gluu.org/auth/oxtrust.metricconfig.write", "https://gluu.org/auth/oxtrust.umaresource.write", "https://gluu.org/auth/oxtrust.customscript.read", "https://gluu.org/auth/oxtrust.customscript.write", "phone", "https://gluu.org/auth/oxtrust.passportbasicconfig.read", "https://gluu.org/auth/oxtrust.rptConfig.write", "https://gluu.org/auth/oxtrust.passportprovider.write", "https://gluu.org/auth/oxtrust.umascope.read", "https://gluu.org/auth/oxtrust.scope.read", "https://gluu.org/auth/oxtrust.apiconfig.write", "https://gluu.org/auth/oxtrust.saml.read", "https://gluu.org/auth/oxtrust.rptConfig.read", "https://gluu.org/auth/oxtrust.serverstatus.read", "https://gluu.org/auth/oxtrust.smtpconfiguration.write", "https://gluu.org/auth/oxtrust.oxauthjsonSetting.write", "https://gluu.org/auth/oxtrust.configuration.write", "https://gluu.org/auth/oxtrust.people.read", "https://gluu.org/auth/oxtrust.attribute.write", "clientinfo", "user_name", "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/uma/scopes/config_api_access", "https://gluu.org/auth/oxtrust.passportconfig.write", "revoke_session", "https://gluu.org/auth/oxtrust.metricconfig.read", "https://gluu.org/auth/oxtrust.systemconfig.read", "mobile_phone", "https://gluu.org/auth/oxtrust.casprotocol.write", "offline_access", "https://gluu.org/auth/oxtrust.oxtrustconfiguration.read", "https://gluu.org/auth/oxtrust.idpconfig.write", "https://gluu.org/auth/oxtrust.scimconfig.write", "https://gluu.org/auth/oxtrust.certificates.read", "https://gluu.org/auth/oxtrust.certificates.write", "email", "https://gluu.org/auth/oxtrust.captchaconfig.write", "https://gluu.org/auth/oxtrust.oxtrustjsonSetting.write", "https://gluu.org/auth/oxtrust.authenticationmethod.write", "https://gluu.org/auth/oxtrust.oxauthconfiguration.write", "https://gluu.org/auth/oxtrust.gluuradiusconfig.read", "https://gluu.org/auth/oxtrust.idpconfig.read", "address", "https://gluu.org/auth/oxtrust.oxauthconfiguration.read", "https://gluu.org/auth/oxtrust.scimconfig.read", "uma_protection", "https://gluu.org/auth/oxtrust.OxauthjsonSetting.read", "https://gluu.org/auth/oxtrust.sectoridentifier.read", "permission", "https://gluu.org/auth/oxtrust.umascope.write", "https://gluu.org/auth/oxtrust.captchaconfig.read", "https://gluu.org/auth/oxtrust.client.write", "https://gluu.org/auth/oxtrust.oxtrustjsonSetting.read", "https://gluu.org/auth/oxtrust.oxtrustsetting.write", "https://gluu.org/auth/oxtrust.casprotocol.read", "https://gluu.org/auth/oxtrust.passportbasicconfig.write", "https://gluu.org/auth/oxtrust.passportconfig.read", "https://gluu.org/auth/oxtrust.smtpconfiguration.read", "https://gluu.org/auth/oxtrust.passportprovider.read", "https://gluu.org/auth/oxtrust.people.write", "https://gluu.org/auth/oxtrust.umaresource.read" ],
  "backchannel_logout_supported" : true,
  "acr_values_supported" : [ "simple_password_auth" ],
  "request_object_encryption_enc_values_supported" : [ "A128CBC+HS256", "A256CBC+HS512", "A128GCM", "A256GCM" ],
  "device_authorization_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/device_authorization",
  "display_values_supported" : [ "page", "popup" ],
  "userinfo_signing_alg_values_supported" : [ "HS256", "HS384", "HS512", "RS256", "RS384", "RS512", "ES256", "ES384", "ES512", "PS256", "PS384", "PS512" ],
  "claim_types_supported" : [ "normal" ],
  "userinfo_encryption_alg_values_supported" : [ "RSA1_5", "RSA-OAEP", "A128KW", "A256KW" ],
  "end_session_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/end_session",
  "revocation_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/revoke",
  "backchannel_authentication_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/bc-authorize",
  "token_endpoint_auth_signing_alg_values_supported" : [ "HS256", "HS384", "HS512", "RS256", "RS384", "RS512", "ES256", "ES384", "ES512", "PS256", "PS384", "PS512" ],
  "frontchannel_logout_supported" : true,
  "jwks_uri" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/jwks",
  "subject_types_supported" : [ "public", "pairwise" ],
  "id_token_signing_alg_values_supported" : [ "none", "HS256", "HS384", "HS512", "RS256", "RS384", "RS512", "ES256", "ES384", "ES512", "PS256", "PS384", "PS512" ],
  "registration_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/register",
  "id_token_token_binding_cnf_values_supported" : [ "tbh" ]
}


#######################################################
TEST: authorizationChallengeFlow
#######################################################
-------------------------------------------------------
REQUEST:
-------------------------------------------------------
POST /oxauth/restv1/register HTTP/1.1
Content-Type: application/json
Accept: application/json
Host: yuriyz-shining-squirrel.gluu.info

{
  "grant_types" : [ "authorization_code", "refresh_token" ],
  "subject_type" : "public",
  "application_type" : "web",
  "scope" : "openid profile address email phone user_name authorization_challenge",
  "redirect_uris" : [ "https://example.com/oxauth-rp/home.htm" ],
  "client_name" : "test app",
  "additional_audience" : [ ],
  "response_types" : [ "code", "id_token" ]
}

-------------------------------------------------------
RESPONSE:
-------------------------------------------------------
HTTP/1.1 200
Cache-Control: no-store
Connection: Keep-Alive
Content-Length: 1458
Content-Type: application/json
Date: Fri, 18 Oct 2024 14:45:47 GMT
Keep-Alive: timeout=5, max=100
Pragma: no-cache
Server: Apache/2.4.52 (Ubuntu)
Strict-Transport-Security: max-age=31536000; includeSubDomains
X-Content-Type-Options: nosniff
X-Xss-Protection: 1; mode=block

{
    "allow_spontaneous_scopes": false,
    "application_type": "web",
    "rpt_as_jwt": false,
    "registration_client_uri": "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/register?client_id=5774bd42-f6e4-441e-8b9f-9078fc2a5a9a",
    "tls_client_auth_subject_dn": "",
    "registration_access_token": "2e43a738-06fd-420b-9e5d-3a41b31361e7",
    "client_id": "5774bd42-f6e4-441e-8b9f-9078fc2a5a9a",
    "token_endpoint_auth_method": "client_secret_basic",
    "scope": "authorization_challenge openid",
    "run_introspection_script_before_access_token_as_jwt_creation_and_include_claims": false,
    "client_secret": "1ce373fb-3610-41c3-9210-8d7ee9ebe701",
    "client_id_issued_at": 1729262747,
    "backchannel_logout_uri": [],
    "backchannel_logout_session_required": false,
    "client_name": "test app",
    "spontaneous_scopes": [],
    "id_token_signed_response_alg": "RS256",
    "access_token_as_jwt": false,
    "grant_types": [
        "authorization_code",
        "refresh_token"
    ],
    "subject_type": "public",
    "keep_client_authorization_after_expiration": false,
    "redirect_uris": ["https://example.com/oxauth-rp/home.htm"],
    "additional_audience": [],
    "frontchannel_logout_uri": [],
    "frontchannel_logout_session_required": false,
    "client_secret_expires_at": 0,
    "require_auth_time": false,
    "access_token_signing_alg": "RS256",
    "response_types": [
        "code",
        "id_token"
    ]
}

-------------------------------------------------------
REQUEST:
-------------------------------------------------------
POST /oxauth/restv1/authorize-challenge HTTP/1.1
Host: yuriyz-shining-squirrel.gluu.info

&client_id=5774bd42-f6e4-441e-8b9f-9078fc2a5a9a&scope=openid+profile+address+email+phone+user_name+authorization_challenge&state=ec48ac7e-e5ad-40ab-b865-726017fafe14&nonce=e2fb8888-64d1-4327-9902-0fd7bfa14e55&otp=secret&username=admin

-------------------------------------------------------
RESPONSE:
-------------------------------------------------------
HTTP/1.1 200
Cache-Control: no-transform, no-store
Connection: Keep-Alive
Content-Length: 61
Content-Type: application/json
Date: Fri, 18 Oct 2024 14:45:47 GMT
Keep-Alive: timeout=5, max=100
Server: Apache/2.4.52 (Ubuntu)
Strict-Transport-Security: max-age=31536000; includeSubDomains
X-Content-Type-Options: nosniff
X-Xss-Protection: 1; mode=block

{"authorization_code":"7a1b0294-eba4-4a75-81f7-18ca666f3dd2"}

Successfully obtained authorization code 7a1b0294-eba4-4a75-81f7-18ca666f3dd2 at Authorization Challenge Endpoint
-------------------------------------------------------
REQUEST:
-------------------------------------------------------
POST /oxauth/restv1/token HTTP/1.1
Content-Type: application/x-www-form-urlencoded
Host: yuriyz-shining-squirrel.gluu.info
Authorization: Basic NTc3NGJkNDItZjZlNC00NDFlLThiOWYtOTA3OGZjMmE1YTlhOjFjZTM3M2ZiLTM2MTAtNDFjMy05MjEwLThkN2VlOWViZTcwMQ==

grant_type=authorization_code&code=7a1b0294-eba4-4a75-81f7-18ca666f3dd2&redirect_uri=https%3A%2F%2Fexample.com%2Foxauth-rp%2Fhome.htm

-------------------------------------------------------
RESPONSE:
-------------------------------------------------------
HTTP/1.1 200
Cache-Control: no-store
Connection: Keep-Alive
Content-Length: 1188
Content-Type: application/json
Date: Fri, 18 Oct 2024 14:45:48 GMT
Keep-Alive: timeout=5, max=100
Pragma: no-cache
Server: Apache/2.4.52 (Ubuntu)
Strict-Transport-Security: max-age=31536000; includeSubDomains
X-Content-Type-Options: nosniff
X-Xss-Protection: 1; mode=block

{"access_token":"6fdfcb8e-c90a-486d-b547-05352127876b","id_token":"eyJraWQiOiI2ZTAxYjczMS05ZDgwLTRhN2MtOTg1OS0wMWUwZWFjN2JkNGNfc2lnX3JzMjU2IiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJhdF9oYXNoIjoiMnZwNHF6MHJMVE1YWk03b1o3RkxfZyIsInN1YiI6ImIwNTJjNTQ3LWRmMmItNGExYS1iZDc1LTE4M2IyMWU5MWVkOCIsImNvZGUiOiI3NTcxNDZjYS02NTdmLTRiNWItODYxMS0zY2IxOTBlMmExMGYiLCJhbXIiOltdLCJpc3MiOiJodHRwczovL3l1cml5ei1zaGluaW5nLXNxdWlycmVsLmdsdXUuaW5mbyIsIm5vbmNlIjoiZTJmYjg4ODgtNjRkMS00MzI3LTk5MDItMGZkN2JmYTE0ZTU1Iiwib3hPcGVuSURDb25uZWN0VmVyc2lvbiI6Im9wZW5pZGNvbm5lY3QtMS4wIiwiYXVkIjoiNTc3NGJkNDItZjZlNC00NDFlLThiOWYtOTA3OGZjMmE1YTlhIiwiYWNyIjoiZGVmYXVsdF9jaGFsbGVuZ2UiLCJjX2hhc2giOiJ1SEY4Yzl4VDQyVHZxYVU0TE5BWDR3IiwiYXV0aF90aW1lIjoxNzI5MjYyNzQ3LCJleHAiOjE3MjkyNjYzNDgsImdyYW50IjoiYXV0aG9yaXphdGlvbl9jb2RlIiwiaWF0IjoxNzI5MjYyNzQ4fQ.mYTQsLbWPR-q5rDYhSBlQOPNixzlLyBUv43tCmx142z8K9lf-m_PAyEhhuENcfJlkc4J8M5Jjt-rLixvDhGC4BXCwHFGPtihuu2iQFiMHPO-XkwwOqBMLeZ_QDdmvsUQ3XCZZk0sh2YPflTChGh21PwBNOjkGxAZ5MlH4W6zYfe2a9T7zEo7qtTxQnigcygZk6HNK0MnfTmW7ZksmVpF_2HNuNx0B9nlvWp16Zj4dhaYA-5Ur6BLqfHi-CHfJtTBenQynMUZRQkiJj2202YtmQ474PovydstPQZP_OU9sLwFrYFEVv8-NBQp3Gwbh2kxl0-ttL42yQtrYhSI0tgDzQ","token_type":"bearer","expires_in":299}

-------------------------------------------------------
REQUEST:
-------------------------------------------------------
GET /oxauth/restv1/userinfo HTTP/1.1 HTTP/1.1
Host: yuriyz-shining-squirrel.gluu.info
Authorization: Bearer 6fdfcb8e-c90a-486d-b547-05352127876b

-------------------------------------------------------
RESPONSE:
-------------------------------------------------------
HTTP/1.1 200
Cache-Control: no-store, private
Connection: Keep-Alive
Content-Length: 46
Content-Type: application/json;charset=utf-8
Date: Fri, 18 Oct 2024 14:45:48 GMT
Keep-Alive: timeout=5, max=100
Pragma: no-cache
Server: Apache/2.4.52 (Ubuntu)
Strict-Transport-Security: max-age=31536000; includeSubDomains
X-Content-Type-Options: nosniff
X-Xss-Protection: 1; mode=block

{"sub":"b052c547-df2b-4a1a-bd75-183b21e91ed8"}

```

## Authorization Challenge Flow sample with invalid user

```
#######################################################
TEST: OpenID Connect Discovery
#######################################################
-------------------------------------------------------
REQUEST:
-------------------------------------------------------
GET /.well-known/webfinger HTTP/1.1?resource=https%3A%2F%2Fyuriyz-shining-squirrel.gluu.info&rel=http%3A%2F%2Fopenid.net%2Fspecs%2Fconnect%2F1.0%2Fissuer HTTP/1.1
Host: yuriyz-shining-squirrel.gluu.info

-------------------------------------------------------
RESPONSE:
-------------------------------------------------------
HTTP/1.1 200
Connection: Keep-Alive
Content-Length: 208
Content-Type: application/jrd+json;charset=iso-8859-1
Date: Fri, 18 Oct 2024 14:59:59 GMT
Keep-Alive: timeout=5, max=100
Server: Apache/2.4.52 (Ubuntu)
Strict-Transport-Security: max-age=31536000; includeSubDomains
X-Content-Type-Options: nosniff
X-Xss-Protection: 1; mode=block

{
    "subject": "https://yuriyz-shining-squirrel.gluu.info",
    "links": [{
        "rel": "http://openid.net/specs/connect/1.0/issuer",
        "href": "https://yuriyz-shining-squirrel.gluu.info"
    }]
}


OpenID Connect Configuration
-------------------------------------------------------
REQUEST:
-------------------------------------------------------
GET /.well-known/openid-configuration HTTP/1.1 HTTP/1.1
Host: yuriyz-shining-squirrel.gluu.info

-------------------------------------------------------
RESPONSE:
-------------------------------------------------------
HTTP/1.1 200
Connection: Keep-Alive
Content-Length: 14690
Content-Type: application/json
Date: Fri, 18 Oct 2024 15:00:00 GMT
Keep-Alive: timeout=5, max=100
Server: Apache/2.4.52 (Ubuntu)
Strict-Transport-Security: max-age=31536000; includeSubDomains
X-Content-Type-Options: nosniff
X-Xss-Protection: 1; mode=block

{
  "request_parameter_supported" : true,
  "token_revocation_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/revoke",
  "introspection_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/introspection",
  "claims_parameter_supported" : false,
  "issuer" : "https://yuriyz-shining-squirrel.gluu.info",
  "userinfo_encryption_enc_values_supported" : [ "RSA1_5", "RSA-OAEP", "A128KW", "A256KW" ],
  "id_token_encryption_enc_values_supported" : [ "A128CBC+HS256", "A256CBC+HS512", "A128GCM", "A256GCM" ],
  "authorization_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/authorize",
  "service_documentation" : "http://gluu.org/docs",
  "id_generation_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/id",
  "claims_supported" : [ "street_address", "country", "zoneinfo", "birthdate", "role", "gender", "formatted", "user_name", "phone_mobile_number", "preferred_username", "locale", "inum", "updated_at", "post_office_box", "nickname", "preferred_language", "email", "website", "email_verified", "profile", "locality", "phone_number_verified", "room_number", "given_name", "middle_name", "picture", "name", "phone_number", "postal_code", "region", "family_name" ],
  "scope_to_claims_mapping" : [ {
    "https://gluu.org/auth/oxtrust.scope.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.smtpconfiguration.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.umaresource.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.oxtrustconfiguration.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.rptConfig.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.sectoridentifier.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.gluuradiusclient.read" : [ ]
  }, {
    "profile" : [ "name", "family_name", "given_name", "middle_name", "nickname", "preferred_username", "profile", "picture", "website", "gender", "birthdate", "zoneinfo", "locale", "updated_at", "preferred_language" ]
  }, {
    "https://gluu.org/auth/oxtrust.certificates.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.systemconfig.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.umascope.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.metricconfig.read" : [ ]
  }, {
    "openid" : [ ]
  }, {
    "permission" : [ "role" ]
  }, {
    "https://gluu.org/auth/oxtrust.scimconfig.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.captchaconfig.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.group.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.people.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.serverstatus.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.attribute.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.smtpconfiguration.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.apiconfig.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.OxauthjsonSetting.read" : [ ]
  }, {
    "super_gluu_ro_session" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.passportconfig.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.gluuradiusclient.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.oxtrustconfiguration.write" : [ ]
  }, {
    "phone" : [ "phone_number_verified", "phone_number" ]
  }, {
    "https://gluu.org/auth/oxtrust.trustedidp.read" : [ ]
  }, {
    "revoke_session" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.apiconfig.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.sectoridentifier.read" : [ ]
  }, {
    "address" : [ "formatted", "postal_code", "street_address", "locality", "country", "room_number", "region", "post_office_box" ]
  }, {
    "https://gluu.org/auth/oxtrust.authenticationmethod.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.passportbasicconfig.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.idpconfig.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.oxtrustjsonSetting.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.passportbasicconfig.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.passportprovider.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.ldapauthentication.read" : [ ]
  }, {
    "clientinfo" : [ "name", "inum" ]
  }, {
    "https://gluu.org/auth/oxtrust.configuration.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.oxauthjsonSetting.write" : [ ]
  }, {
    "mobile_phone" : [ "phone_mobile_number" ]
  }, {
    "https://gluu.org/auth/oxtrust.customscript.write" : [ ]
  }, {
    "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/uma/scopes/config_api_access" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.attribute.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.captchaconfig.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.customscript.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.oxtrustsetting.read" : [ ]
  }, {
    "email" : [ "email_verified", "email" ]
  }, {
    "user_name" : [ "user_name" ]
  }, {
    "https://gluu.org/auth/oxtrust.passportconfig.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.umascope.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.oxauthconfiguration.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.casprotocol.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.gluuradiusconfig.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.client.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.passportprovider.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.authenticationmethod.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.scimconfig.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.scope.read" : [ ]
  }, {
    "oxd" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.metricconfig.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.ldapauthentication.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.oxauthconfiguration.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.systemconfig.write" : [ ]
  }, {
    "uma_protection" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.saml.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.serverstatus.read" : [ ]
  }, {
    "offline_access" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.group.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.saml.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.oxtrustsetting.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.certificates.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.people.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.gluuradiusconfig.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.trustedidp.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.rptConfig.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.umaresource.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.configuration.read" : [ ]
  }, {
    "authorization_challenge" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.casprotocol.write" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.client.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.idpconfig.read" : [ ]
  }, {
    "https://gluu.org/auth/oxtrust.oxtrustjsonSetting.read" : [ ]
  } ],
  "token_endpoint_auth_methods_supported" : [ "client_secret_basic", "client_secret_post", "client_secret_jwt", "private_key_jwt", "tls_client_auth", "self_signed_tls_client_auth" ],
  "tls_client_certificate_bound_access_tokens" : true,
  "response_modes_supported" : [ "form_post", "query", "fragment" ],
  "backchannel_logout_session_supported" : true,
  "token_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/token",
  "response_types_supported" : [ "token", "token code", "token code id_token", "code", "id_token", "code id_token", "token id_token" ],
  "request_uri_parameter_supported" : true,
  "backchannel_user_code_parameter_supported" : false,
  "grant_types_supported" : [ "password", "implicit", "urn:ietf:params:oauth:grant-type:device_code", "authorization_code", "urn:ietf:params:oauth:grant-type:uma-ticket", "client_credentials", "refresh_token" ],
  "ui_locales_supported" : [ "en", "bg", "de", "es", "fr", "it", "ru", "tr" ],
  "userinfo_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/userinfo",
  "authorization_challenge_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/authorize-challenge",
  "auth_level_mapping" : {
    "-1" : [ "simple_password_auth" ]
  },
  "require_request_uri_registration" : false,
  "id_token_encryption_alg_values_supported" : [ "RSA1_5", "RSA-OAEP", "A128KW", "A256KW" ],
  "frontchannel_logout_session_supported" : true,
  "claims_locales_supported" : [ "en" ],
  "clientinfo_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/clientinfo",
  "request_object_signing_alg_values_supported" : [ "none", "HS256", "HS384", "HS512", "RS256", "RS384", "RS512", "ES256", "ES384", "ES512", "PS256", "PS384", "PS512" ],
  "request_object_encryption_alg_values_supported" : [ "RSA1_5", "RSA-OAEP", "A128KW", "A256KW" ],
  "session_revocation_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/revoke_session",
  "check_session_iframe" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/opiframe.htm",
  "scopes_supported" : [ "https://gluu.org/auth/oxtrust.group.read", "https://gluu.org/auth/oxtrust.group.write", "https://gluu.org/auth/oxtrust.gluuradiusconfig.write", "https://gluu.org/auth/oxtrust.trustedidp.write", "https://gluu.org/auth/oxtrust.saml.write", "https://gluu.org/auth/oxtrust.client.read", "https://gluu.org/auth/oxtrust.ldapauthentication.write", "https://gluu.org/auth/oxtrust.oxtrustconfiguration.write", "https://gluu.org/auth/oxtrust.scope.write", "https://gluu.org/auth/oxtrust.apiconfig.read", "https://gluu.org/auth/oxtrust.serverstatus.write", "https://gluu.org/auth/oxtrust.attribute.read", "https://gluu.org/auth/oxtrust.gluuradiusclient.write", "authorization_challenge", "super_gluu_ro_session", "https://gluu.org/auth/oxtrust.trustedidp.read", "https://gluu.org/auth/oxtrust.ldapauthentication.read", "https://gluu.org/auth/oxtrust.sectoridentifier.write", "https://gluu.org/auth/oxtrust.gluuradiusclient.read", "https://gluu.org/auth/oxtrust.authenticationmethod.read", "openid", "profile", "https://gluu.org/auth/oxtrust.configuration.read", "https://gluu.org/auth/oxtrust.systemconfig.write", "https://gluu.org/auth/oxtrust.oxtrustsetting.read", "https://gluu.org/auth/oxtrust.metricconfig.write", "https://gluu.org/auth/oxtrust.umaresource.write", "https://gluu.org/auth/oxtrust.customscript.read", "https://gluu.org/auth/oxtrust.customscript.write", "phone", "https://gluu.org/auth/oxtrust.passportbasicconfig.read", "https://gluu.org/auth/oxtrust.rptConfig.write", "https://gluu.org/auth/oxtrust.passportprovider.write", "https://gluu.org/auth/oxtrust.umascope.read", "https://gluu.org/auth/oxtrust.scope.read", "https://gluu.org/auth/oxtrust.apiconfig.write", "https://gluu.org/auth/oxtrust.saml.read", "https://gluu.org/auth/oxtrust.rptConfig.read", "https://gluu.org/auth/oxtrust.serverstatus.read", "https://gluu.org/auth/oxtrust.smtpconfiguration.write", "https://gluu.org/auth/oxtrust.oxauthjsonSetting.write", "https://gluu.org/auth/oxtrust.configuration.write", "https://gluu.org/auth/oxtrust.people.read", "https://gluu.org/auth/oxtrust.attribute.write", "clientinfo", "user_name", "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/uma/scopes/config_api_access", "https://gluu.org/auth/oxtrust.passportconfig.write", "revoke_session", "https://gluu.org/auth/oxtrust.metricconfig.read", "https://gluu.org/auth/oxtrust.systemconfig.read", "mobile_phone", "https://gluu.org/auth/oxtrust.casprotocol.write", "offline_access", "https://gluu.org/auth/oxtrust.oxtrustconfiguration.read", "https://gluu.org/auth/oxtrust.idpconfig.write", "https://gluu.org/auth/oxtrust.scimconfig.write", "https://gluu.org/auth/oxtrust.certificates.read", "https://gluu.org/auth/oxtrust.certificates.write", "email", "https://gluu.org/auth/oxtrust.captchaconfig.write", "https://gluu.org/auth/oxtrust.oxtrustjsonSetting.write", "https://gluu.org/auth/oxtrust.authenticationmethod.write", "https://gluu.org/auth/oxtrust.oxauthconfiguration.write", "https://gluu.org/auth/oxtrust.gluuradiusconfig.read", "https://gluu.org/auth/oxtrust.idpconfig.read", "address", "https://gluu.org/auth/oxtrust.oxauthconfiguration.read", "https://gluu.org/auth/oxtrust.scimconfig.read", "uma_protection", "https://gluu.org/auth/oxtrust.OxauthjsonSetting.read", "https://gluu.org/auth/oxtrust.sectoridentifier.read", "permission", "https://gluu.org/auth/oxtrust.umascope.write", "https://gluu.org/auth/oxtrust.captchaconfig.read", "https://gluu.org/auth/oxtrust.client.write", "https://gluu.org/auth/oxtrust.oxtrustjsonSetting.read", "https://gluu.org/auth/oxtrust.oxtrustsetting.write", "https://gluu.org/auth/oxtrust.casprotocol.read", "https://gluu.org/auth/oxtrust.passportbasicconfig.write", "https://gluu.org/auth/oxtrust.passportconfig.read", "https://gluu.org/auth/oxtrust.smtpconfiguration.read", "https://gluu.org/auth/oxtrust.passportprovider.read", "https://gluu.org/auth/oxtrust.people.write", "https://gluu.org/auth/oxtrust.umaresource.read" ],
  "backchannel_logout_supported" : true,
  "acr_values_supported" : [ "simple_password_auth" ],
  "request_object_encryption_enc_values_supported" : [ "A128CBC+HS256", "A256CBC+HS512", "A128GCM", "A256GCM" ],
  "device_authorization_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/device_authorization",
  "display_values_supported" : [ "page", "popup" ],
  "userinfo_signing_alg_values_supported" : [ "HS256", "HS384", "HS512", "RS256", "RS384", "RS512", "ES256", "ES384", "ES512", "PS256", "PS384", "PS512" ],
  "claim_types_supported" : [ "normal" ],
  "userinfo_encryption_alg_values_supported" : [ "RSA1_5", "RSA-OAEP", "A128KW", "A256KW" ],
  "end_session_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/end_session",
  "revocation_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/revoke",
  "backchannel_authentication_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/bc-authorize",
  "token_endpoint_auth_signing_alg_values_supported" : [ "HS256", "HS384", "HS512", "RS256", "RS384", "RS512", "ES256", "ES384", "ES512", "PS256", "PS384", "PS512" ],
  "frontchannel_logout_supported" : true,
  "jwks_uri" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/jwks",
  "subject_types_supported" : [ "public", "pairwise" ],
  "id_token_signing_alg_values_supported" : [ "none", "HS256", "HS384", "HS512", "RS256", "RS384", "RS512", "ES256", "ES384", "ES512", "PS256", "PS384", "PS512" ],
  "registration_endpoint" : "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/register",
  "id_token_token_binding_cnf_values_supported" : [ "tbh" ]
}


#######################################################
TEST: authorizationChallengeFlow
#######################################################
-------------------------------------------------------
REQUEST:
-------------------------------------------------------
POST /oxauth/restv1/register HTTP/1.1
Content-Type: application/json
Accept: application/json
Host: yuriyz-shining-squirrel.gluu.info

{
  "grant_types" : [ "authorization_code", "refresh_token" ],
  "subject_type" : "public",
  "application_type" : "web",
  "scope" : "openid profile address email phone user_name authorization_challenge",
  "redirect_uris" : [ "https://example.com/oxauth-rp/home.htm" ],
  "client_name" : "test app",
  "additional_audience" : [ ],
  "response_types" : [ "code", "id_token" ]
}

-------------------------------------------------------
RESPONSE:
-------------------------------------------------------
HTTP/1.1 200
Cache-Control: no-store
Connection: Keep-Alive
Content-Length: 1458
Content-Type: application/json
Date: Fri, 18 Oct 2024 15:00:01 GMT
Keep-Alive: timeout=5, max=100
Pragma: no-cache
Server: Apache/2.4.52 (Ubuntu)
Strict-Transport-Security: max-age=31536000; includeSubDomains
X-Content-Type-Options: nosniff
X-Xss-Protection: 1; mode=block

{
    "allow_spontaneous_scopes": false,
    "application_type": "web",
    "rpt_as_jwt": false,
    "registration_client_uri": "https://yuriyz-shining-squirrel.gluu.info/oxauth/restv1/register?client_id=2d38e8ee-0cf5-4543-b407-28d9958602a6",
    "tls_client_auth_subject_dn": "",
    "registration_access_token": "6f21a096-f030-4097-871b-a9b58556045b",
    "client_id": "2d38e8ee-0cf5-4543-b407-28d9958602a6",
    "token_endpoint_auth_method": "client_secret_basic",
    "scope": "authorization_challenge openid",
    "run_introspection_script_before_access_token_as_jwt_creation_and_include_claims": false,
    "client_secret": "1f0a10f9-0227-4d5a-8589-02f96fa98364",
    "client_id_issued_at": 1729263601,
    "backchannel_logout_uri": [],
    "backchannel_logout_session_required": false,
    "client_name": "test app",
    "spontaneous_scopes": [],
    "id_token_signed_response_alg": "RS256",
    "access_token_as_jwt": false,
    "grant_types": [
        "authorization_code",
        "refresh_token"
    ],
    "subject_type": "public",
    "keep_client_authorization_after_expiration": false,
    "redirect_uris": ["https://example.com/oxauth-rp/home.htm"],
    "additional_audience": [],
    "frontchannel_logout_uri": [],
    "frontchannel_logout_session_required": false,
    "client_secret_expires_at": 0,
    "require_auth_time": false,
    "access_token_signing_alg": "RS256",
    "response_types": [
        "code",
        "id_token"
    ]
}

-------------------------------------------------------
REQUEST:
-------------------------------------------------------
POST /oxauth/restv1/authorize-challenge HTTP/1.1
Host: yuriyz-shining-squirrel.gluu.info

&client_id=2d38e8ee-0cf5-4543-b407-28d9958602a6&scope=openid+profile+address+email+phone+user_name+authorization_challenge&state=2f3d87bc-ca4f-4895-a925-11a1e14ec046&nonce=1db86666-8967-4611-b3f5-07c963553a95&otp=secret&username=admin1

-------------------------------------------------------
RESPONSE:
-------------------------------------------------------
HTTP/1.1 401
Cache-Control: no-transform, no-store
Connection: Keep-Alive
Content-Length: 29
Content-Type: application/json
Date: Fri, 18 Oct 2024 15:00:01 GMT
Keep-Alive: timeout=5, max=100
Server: Apache/2.4.52 (Ubuntu)
Strict-Transport-Security: max-age=31536000; includeSubDomains
X-Content-Type-Options: nosniff
X-Xss-Protection: 1; mode=block

{"error": "username_invalid"}

``` 