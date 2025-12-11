# Per-SP ACR Routing for SAML → oxAuth (Shibboleth + Gluu 4.x)

## Overview

This document describes how to route SAML logins from different Service Providers (SPs) to different oxAuth Authentication Context Class References (ACRs) in Gluu 4.x, using a custom **SAML→OIDC ACR router** script.

The solution uses:

- **Shibboleth IdP** (Gluu Server IdP) as the SAML identity provider
- **oxAuth** as the OpenID Provider
- A custom **Person Authentication** script in oxAuth that:
  - Reads a JSON mapping file: **SAML SP `entityID` → oxAuth ACR**
  - Uses `issuerId` / `entityId` values stored in the oxAuth session
  - Returns the correct ACR for the login request

### Sample scenario

In the lab we used two SAML SPs:

1. `https://testappsaml.gluu.org`  
   - `entityID = https://testappsaml.gluu.org/shibboleth`  
   - Should use **`basic`** login (username/password)

2. `https://testappsaml2.gluu.org`  
   - `entityID = https://testappsaml2.gluu.org/shibboleth`  
   - Should use **`OTP`** login (OTP 2FA script)

After deploying this solution:

- Requests from `testappsaml.gluu.org` go through **`basic`** ACR.
- Requests from `testappsaml2.gluu.org` go through **`OTP`** ACR.

---

## Assumptions

This guide assumes:

- Gluu Server **4.x CE** with Shibboleth IdP.
- Shibboleth is already configured to delegate authentication to oxAuth via the `Authn/oxAuth` flow.
- Each SAML SP has its own **unique `entityID`** in metadata.
- You already have:
  - A working **`basic`** ACR script (username/password).
  - A working **`OTP`** ACR script (OTP 2FA).

---

## High-Level Architecture

1. SAML SP sends `AuthnRequest` to Shibboleth IdP.
2. Shibboleth IdP invokes the **oxAuth** authentication flow (`Authn/oxAuth`) and calls:

   ```
   GET /oxauth/restv1/authorize?...&issuerId=<SP entityID>&entityId=<SP entityID>
   ```

3. oxAuth is configured with:

   ```json
   "authorizationRequestCustomAllowedParameters": [
     "issuerId",
     "entityId"
   ]
   ```

4. oxAuth’s **default ACR** is set to the **router script**.

5. Router script evaluates the SP entityID and returns correct ACR.

---

## Step 1 – Create JSON mapping file

Create `/etc/certs/saml2oidc_acr_mappings.json`:

```json
{
  "mappings": {
    "https://testappsaml2.gluu.org/shibboleth": "OTP"
  },
  "default": "basic"
}
```

---

## Step 2 – Deploy router script

Create a Person Authentication script named `saml2_oidc_acr_router` and paste the full Python script [ check another script ] .

Add property:

```
entityid_oidc_acr_map_file = /etc/certs/saml2oidc_acr_mappings.json
```

---

## Step 3 – Allow issuerId & entityId in oxAuth

Edit config:

 - Log into Gluu Server oxTrust
 - JSON Configuration > oxAuth Configuration > Search for "authorizationRequestCustomAllowedParameters"
 - Add `issuerId` as Item_1 and "entityId" as Item_2
 - Update property

```json
"authorizationRequestCustomAllowedParameters": [
  "issuerId",
  "entityId"
]
```

Restart oxAuth.

---

## Step 4 – Set router as default ACR

In oxTrust → Manage Authentication:

```
Default Authentication Method = saml2_oidc_acr_router
```

---

## Results

- **testappsaml.gluu.org** → basic  
- **testappsaml2.gluu.org** → OTP

---

## Testing Checklist

- Verify JSON mapping loads.
- Verify `issuerId` and `entityId` appear in `/authorize` calls.
- Validate routing behavior in `oxauth_script.log`.
