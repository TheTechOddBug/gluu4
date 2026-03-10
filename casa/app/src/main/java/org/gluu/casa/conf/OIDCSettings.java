package org.gluu.casa.conf;

import com.fasterxml.jackson.annotation.*;

import java.util.List;

/**
 * @author jgomer
 */
public class OIDCSettings {

    @JsonProperty("authz_redirect_uri")
    private String redirectUri;

    @JsonProperty("post_logout_uri")
    private String postLogoutUri;

    @JsonProperty("frontchannel_logout_uri")
    private String frontLogoutUri;

    @JsonProperty("client_id")
    private String clientId;
    
    @JsonProperty("client_secret")
    private String clientSecret;

    private List<String> scopes;

    @JsonIgnore
    private List<String> acrValues;

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getPostLogoutUri() {
        return postLogoutUri;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public List<String> getAcrValues() {
        return acrValues;
    }

    public String getFrontLogoutUri() {
        return frontLogoutUri;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public void setPostLogoutUri(String postLogoutUri) {
        this.postLogoutUri = postLogoutUri;
    }

    public void setAcrValues(List<String> acrValues) {
        this.acrValues = acrValues;
    }

    public void setFrontLogoutUri(String frontLogoutUri) {
        this.frontLogoutUri = frontLogoutUri;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

}
