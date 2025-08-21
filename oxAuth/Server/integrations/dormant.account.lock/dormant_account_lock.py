# oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
# Copyright (c) 2016, Gluu
#
# -*- coding: utf-8 -*-
# Auto-deactivate users who haven't logged in within the last 1 hour.
# Sets gluuStatus="inactive" and denies login only when (now - oxLastLogonTime) >= 60 minutes.


from org.gluu.model.custom.script.type.auth import PersonAuthenticationType

try:
    from org.gluu.service.cdi.util import CdiUtil
except ImportError:
    from org.xdi.service.cdi.util import CdiUtil

try:
    from org.gluu.oxauth.service import AuthenticationService, UserService
    from org.gluu.oxauth.security import Identity
except ImportError:
    from org.xdi.oxauth.service import AuthenticationService, UserService
    from org.xdi.oxauth.security import Identity

from java.time import LocalDateTime, ZonedDateTime, Instant, ZoneId, Duration
from java.time.format import DateTimeFormatter
from java.util import Date, Locale

INACTIVITY_LIMIT_MINUTES = 60  # 1 hour

_FMT_MAIN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
_FMT_FALLBACK = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
_FMT_JAVA_TOSTR = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)

class PersonAuthentication(PersonAuthenticationType):

    def __init__(self, currentTimeMillis=None, *args, **kwargs):
        self.currentTimeMillis = currentTimeMillis
        self.zone = ZoneId.systemDefault()

    def init(self, *args, **kwargs):
        print("inactive-deactivation (60 min): init OK")
        return True

    def destroy(self, *args, **kwargs):
        print("inactive-deactivation: destroy")
        return True

    def getApiVersion(self):
        return 11

    # flow
    def getCountAuthenticationSteps(self, configurationAttributes):
        return 1

    def getPageForStep(self, configurationAttributes, step):
        return ""  

    def getNextStep(self, *args, **kwargs):
        return -1

    def prepareForStep(self, configurationAttributes, requestParameters, step):
        return True

    def getExtraParametersForStep(self, configurationAttributes, step):
        return None

    def getAlternativeAuthenticationMethod(self, usageType, configurationAttributes):
        return None

    def isValidAuthenticationMethod(self, usageType, configurationAttributes):
        return True

    def getAuthenticationMethodClaims(self, requestParameters):
        return None

    def logout(self, configurationAttributes, requestParameters):
        return True

    def authenticate(self, configurationAttributes, requestParameters, step):
        identity = CdiUtil.bean(Identity)
        authSvc = CdiUtil.bean(AuthenticationService)
        userSvc = CdiUtil.bean(UserService)

        creds = identity.getCredentials()
        username = creds.getUsername()
        password = creds.getPassword()

        if not username:
            return True

        user = userSvc.getUser(username, "oxLastLogonTime", "gluuStatus", "uid")
        if user is None:
            return False

        status = (user.getAttribute("gluuStatus") or "").lower()
        if status == "inactive":
            print("inactive-deactivation: user already inactive: %s" % username)
            return False

        lastLogonVal = user.getAttribute("oxLastLogonTime")

        ok, minutes = self._within_window_minutes(lastLogonVal)
        print("inactive-deactivation: user=%s oxLastLogonTime='%s' minutes_since=%s within_window=%s"
              % (username, str(lastLogonVal), str(minutes), str(ok)))

        if not ok:
            self._deactivate(userSvc, user)
            return False

        if not authSvc.authenticate(username, password):
            return False

        return True  

    def _within_window_minutes(self, value):
        """
        value may be:
          - java.util.Date
          - 'yyyy-MM-dd HH:mm:ss.SSS'
          - 'yyyy-MM-dd HH:mm:ss'
          - 'EEE MMM dd HH:mm:ss z yyyy' (e.g., 'Thu Aug 21 13:54:29 GMT 2025')
        Returns (allow_bool, minutes_since) where allow_bool=True means allow login.
        """
        if value is None:
            print("inactive-deactivation: oxLastLogonTime missing -> allow")
            return (True, None)

        ldt = None

        if isinstance(value, Date):
            try:
                inst = Instant.ofEpochMilli(value.getTime())
                ldt = LocalDateTime.ofInstant(inst, self.zone)
            except:
                ldt = None
        else:
            s = str(value)
            if ldt is None:
                try:
                    ldt = LocalDateTime.parse(s, _FMT_MAIN)
                except:
                    ldt = None
            if ldt is None:
                try:
                    ldt = LocalDateTime.parse(s, _FMT_FALLBACK)
                except:
                    ldt = None
            if ldt is None:
                try:
                    zdt = ZonedDateTime.parse(s, _FMT_JAVA_TOSTR)
                    ldt = zdt.withZoneSameInstant(self.zone).toLocalDateTime()
                except:
                    ldt = None

        if ldt is None:
            print("inactive-deactivation: unparsable oxLastLogonTime='%s' -> allow" % str(value))
            return (True, None)

        now = LocalDateTime.now(self.zone)
        minutes = Duration.between(ldt, now).toMinutes()
        if minutes < 0:
            minutes = 0

        return (minutes < INACTIVITY_LIMIT_MINUTES, minutes)

    def _deactivate(self, userSvc, user):
        user.setAttribute("gluuStatus", "inactive")
        userSvc.updateUser(user)
        print("inactive-deactivation: gluuStatus set to 'inactive' for user=%s" % user.getUserId())
