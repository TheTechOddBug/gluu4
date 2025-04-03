package org.gluu.idp.externalauth;

public class ReuseAuthnResultContext {
    
    private String usedAcr;
    private String requestedAcr;

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
}
