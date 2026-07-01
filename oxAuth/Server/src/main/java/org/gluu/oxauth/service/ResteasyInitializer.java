package org.gluu.oxauth.service;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.gluu.oxauth.authorize.ws.rs.AuthorizationChallengeEndpoint;
import org.gluu.oxauth.authorize.ws.rs.AuthorizeRestWebServiceImpl;
import org.gluu.oxauth.authorize.ws.rs.DeviceAuthorizationRestWebServiceImpl;
import org.gluu.oxauth.bcauthorize.ws.rs.BackchannelAuthorizeRestWebServiceImpl;
import org.gluu.oxauth.bcauthorize.ws.rs.BackchannelDeviceRegistrationRestWebServiceImpl;
import org.gluu.oxauth.clientinfo.ws.rs.ClientInfoRestWebServiceImpl;
import org.gluu.oxauth.gluu.ws.rs.GluuConfigurationWS;
import org.gluu.oxauth.introspection.ws.rs.IntrospectionWebService;
import org.gluu.oxauth.jwk.ws.rs.JwkRestWebServiceImpl;
import org.gluu.oxauth.model.fido.u2f.U2fConfiguration;
import org.gluu.oxauth.register.ws.rs.RegisterRestWebServiceImpl;
import org.gluu.oxauth.revoke.RevokeRestWebServiceImpl;
import org.gluu.oxauth.session.ws.rs.CheckSessionStatusRestWebServiceImpl;
import org.gluu.oxauth.session.ws.rs.EndSessionRestWebServiceImpl;
import org.gluu.oxauth.token.ws.rs.TokenRestWebServiceImpl;
import org.gluu.oxauth.uma.ws.rs.UmaGatheringWS;
import org.gluu.oxauth.uma.ws.rs.UmaMetadataWS;
import org.gluu.oxauth.uma.ws.rs.UmaPermissionRegistrationWS;
import org.gluu.oxauth.uma.ws.rs.UmaResourceRegistrationWS;
import org.gluu.oxauth.uma.ws.rs.UmaRptIntrospectionWS;
import org.gluu.oxauth.uma.ws.rs.UmaScopeIconWS;
import org.gluu.oxauth.uma.ws.rs.UmaScopeWS;
import org.gluu.oxauth.userinfo.ws.rs.UserInfoRestWebServiceImpl;
import org.gluu.oxauth.ws.rs.controller.HealthCheckControllerOld;
import org.gluu.oxauth.ws.rs.fido.u2f.U2fAuthenticationWS;
import org.gluu.oxauth.ws.rs.fido.u2f.U2fRegistrationWS;
import org.gluu.oxauth.ws.rs.stat.StatWS;

/**
 * Integration with Resteasy
 * 
 * @author Yuriy Movchan
 * @version 0.1, 03/21/2017
 */
@ApplicationPath("/restv1")
public class ResteasyInitializer extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> classes = new HashSet<>();
        classes.add(GluuConfigurationWS.class);

        classes.add(AuthorizeRestWebServiceImpl.class);
        classes.add(AuthorizationChallengeEndpoint.class);
//        classes.add(AccessEvaluationRestWebServiceImplV1.class);
//        classes.add(AccessEvaluationDiscoveryWS.class);
//        classes.add(AccessEvaluationSearchWS.class);
        classes.add(RegisterRestWebServiceImpl.class);
        classes.add(ClientInfoRestWebServiceImpl.class);
        classes.add(RevokeRestWebServiceImpl.class);
//        classes.add(GlobalTokenRevocationRestWebService.class);
//        classes.add(StatusListRestWebService.class);
//        classes.add(StatusListAggregationRestWebService.class);
        classes.add(JwkRestWebServiceImpl.class);
//        classes.add(ArchivedJwksWebServiceImpl.class);
        classes.add(IntrospectionWebService.class);
//        classes.add(ParRestWebService.class);
//        classes.add(SessionRestWebService.class);

        classes.add(TokenRestWebServiceImpl.class);
        classes.add(UserInfoRestWebServiceImpl.class);
        classes.add(EndSessionRestWebServiceImpl.class);

        classes.add(UmaMetadataWS.class);
        classes.add(UmaGatheringWS.class);
        classes.add(UmaPermissionRegistrationWS.class);
        classes.add(UmaResourceRegistrationWS.class);
        classes.add(UmaRptIntrospectionWS.class);
        classes.add(UmaScopeIconWS.class);
        classes.add(UmaScopeWS.class);

        classes.add(CheckSessionStatusRestWebServiceImpl.class);

        classes.add(DeviceAuthorizationRestWebServiceImpl.class);
        classes.add(BackchannelAuthorizeRestWebServiceImpl.class);
        classes.add(BackchannelDeviceRegistrationRestWebServiceImpl.class);

        classes.add(StatWS.class);

//        classes.add(SsaRestWebServiceImpl.class);
        
        classes.add(U2fConfiguration.class);
        classes.add(U2fAuthenticationWS.class);
        classes.add(U2fRegistrationWS.class);

        classes.add(HealthCheckControllerOld.class);

        return classes;
    }

}