# Dormant Account Lock

!!! info "What it is"
    **Dormant Account Lock** is a Gluu **Person Authentication** script that enforces a strict inactivity policy using `oxLastLogonTime`. If a user has not logged in within the last **60 minutes** ( this can be changed accordingly ), the script **blocks the login attempt** and **automatically sets `gluuStatus` to `inactive`**. This helps prevent access with stale credentials and supports security/compliance controls for short-lived access windows.

## Why use it?
- Enforces **fresh-login-only** access for sensitive environments  
- Automatically **disables dormant accounts** (`gluuStatus=inactive`)  
- Reduces risk from **stale sessions** or unattended accounts

## How it works (at a glance)
1. On each login attempt, the script retrieves the user’s `oxLastLogonTime` **before** password verification (so oxAuth can’t refresh the timestamp first).
2. If `now - oxLastLogonTime ≥ 60 minutes`, the script:
   - Sets `gluuStatus` → `inactive`
   - Denies the login
3. If the user is within the 60-minute window, normal authentication proceeds and oxAuth updates `oxLastLogonTime` on success.

!!! tip "Status values"
    The script uses `gluuStatus` values **`active`** / **`inactive`**.

## Key features
- **One-hour inactivity window** (changeable via `INACTIVITY_LIMIT_MINUTES`)
- **Robust timestamp parsing**, including:
  - `yyyy-MM-dd HH:mm:ss.SSS` (default in many deployments)
  - `yyyy-MM-dd HH:mm:ss`
  - Java `Date.toString()` style (e.g., `Thu Aug 21 13:54:29 GMT 2025`)
  - Direct `java.util.Date` values from LDAP
- **Single-step ACR**; integrates with the default login page
- Defensive defaults: **missing or unparsable** `oxLastLogonTime` → **allow** (no surprise deactivation)

## Compatibility
- Designed for **Gluu 4.5.x**
- Works with both `org.gluu.*` and legacy `org.xdi.*` package paths
- Jython-based custom script (PersonAuthenticationType, API v11)

## Quick start
1. Upload the script in **Admin UI → Configuration → Scripts → Person Authentication**.
2. Enable it and set as your **default ACR** or assign to specific clients.
3. (Optional) Adjust `INACTIVITY_LIMIT_MINUTES` to fit your policy.
4. Test with a user whose `oxLastLogonTime` is older/newer than the threshold and observe behavior.


## Logging
Look for log lines with the prefix `inactive-deactivation`, for example:
```text
2025-08-21 15:32:33,548 DEBUG [qtp512549200-26] [org.gluu.oxauth.service.external.ExternalAuthenticationService] (ExternalAuthenticationService.java:161) - Executing python 'isValidAuthenticationMethod' authenticator method
2025-08-21 15:32:33,548 DEBUG [qtp512549200-26] [org.gluu.oxauth.service.external.ExternalAuthenticationService] (ExternalAuthenticationService.java:165) - Executed python 'isValidAuthenticationMethod' authenticator method, result: true
2025-08-21 15:32:39,827 INFO  [qtp512549200-30] [org.gluu.service.PythonService$PythonLoggerOutputStream] (PythonService.java:243) - inactive-deactivation: user=zico oxLastLogonTime='Thu Aug 21 14:17:36 GMT 2025' minutes_since=75 within_window=False
2025-08-21 15:32:39,835 INFO  [qtp512549200-30] [org.gluu.service.PythonService$PythonLoggerOutputStream] (PythonService.java:243) - inactive-deactivation: gluuStatus set to 'inactive' for user=zico
```
