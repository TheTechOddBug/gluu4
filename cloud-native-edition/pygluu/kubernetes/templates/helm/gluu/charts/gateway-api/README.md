# gateway-api

![Version: 1.8.50](https://img.shields.io/badge/Version-1.8.50-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square) ![AppVersion: 4.5.15](https://img.shields.io/badge/AppVersion-4.5.15-informational?style=flat-square)

Gateway API definitions chart

**Homepage:** <https://gluu.org/docs/gluu-server>

## Maintainers

| Name | Email | Url |
| ---- | ------ | --- |
| Mohammad Abudayyeh | <support@gluu.org> | <https://github.com/moabu> |

## Source Code

* <https://gateway-api.sigs.k8s.io/>
* <https://github.com/GluuFederation/gluu4/tree/4.5/cloud-native-edition>

## Requirements

Kubernetes: `>=v1.22.0-0`

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| additionalConfig | object | `{"airlock":{"createLbService":false},"cilium":{"ipPoolBlocks":[]},"envoy":{"createGatewayClass":false},"istio":{"sessionCookieLifetime":"0s"},"kgateway":{"sessionCookieLifetime":"0s"},"nginx":{"ipHashLbEnabled":false},"traefik":{}}` | Additional configuration for Specific Gateway API implementation |
| additionalConfig.airlock | object | `{"createLbService":false}` | Configuration for Airlock Microgateway |
| additionalConfig.airlock.createLbService | bool | `false` | Create LoadBalancer service using GatewayParameters (by default airlock-microgateway doesn't create the service). See https://docs.airlock.com/microgateway/latest/index/api/crds/gateway-parameters/v1alpha1/ for details. The GatewayParameters will be attached to gateway.infrastructure.parametersRef only if it's empty. |
| additionalConfig.cilium | object | `{"ipPoolBlocks":[]}` | Configuration for Cilium. |
| additionalConfig.cilium.ipPoolBlocks | list | `[]` | Create Cilium IP pool with the specified blocks. See https://docs.cilium.io/en/stable/network/lb-ipam/ for details. |
| additionalConfig.envoy | object | `{"createGatewayClass":false}` | Configuration for Envoy. |
| additionalConfig.envoy.createGatewayClass | bool | `false` | Create GatewayClass named `envoy` (by default Envoy doesn't create gatewayclass). The `envoy` name can be set as value of `gateway.className` attribute. |
| additionalConfig.istio | object | `{"sessionCookieLifetime":"0s"}` | Configuration for Istio. |
| additionalConfig.istio.sessionCookieLifetime | string | `"0s"` | TTL of cookie for session affinity. |
| additionalConfig.kgateway | object | `{"sessionCookieLifetime":"0s"}` | Configuration for kgateway. |
| additionalConfig.kgateway.sessionCookieLifetime | string | `"0s"` | TTL of cookie for session affinity. |
| additionalConfig.nginx | object | `{"ipHashLbEnabled":false}` | Configuration for NGINX Fabric. |
| additionalConfig.nginx.ipHashLbEnabled | bool | `false` | Enable nginx ip_hash loadbalancing for supported service e.g. oxshibboleth (enable this if using nginx fabric OSS version). See https://docs.nginx.com/nginx-gateway-fabric/traffic-management/session-persistence/ for details. |
| additionalConfig.traefik | object | `{}` | Configuration for Traefik. |
| fullnameOverride | string | `""` |  |
| gateway | object | `{"annotations":{},"attachLbIp":false,"className":"nginx","enabled":true,"httpPort":80,"httpsPort":443,"infrastructure":{"annotations":{},"labels":{},"parametersRef":{}},"labels":{},"name":"gluu-gateway","tlsSecretName":"tls-certificate"}` | Configuration for Gateway resource |
| gateway.annotations | object | `{}` | Specific annotations for the Gateway resource |
| gateway.attachLbIp | bool | `false` | Attach global.lbIp to Gateway spec.addresses with IPAddress type (enable this if loadbalancer doesn't assign IP address to Gateway automatically) |
| gateway.className | string | `"nginx"` | Set the gatewayClassName corresponding to your installed controller. |
| gateway.enabled | bool | `true` | Enable Gateway API and create Gateway resource (if disabled, you can create and manage the Gateway resource externally). |
| gateway.httpPort | int | `80` | Gateway http port number |
| gateway.httpsPort | int | `443` | Gateway https port number |
| gateway.infrastructure | object | `{"annotations":{},"labels":{},"parametersRef":{}}` | Gateway spec.infrastructure |
| gateway.infrastructure.annotations | object | `{}` | Specific annotations for the infrastructure |
| gateway.infrastructure.labels | object | `{}` | Specific labels for the infrastructure |
| gateway.infrastructure.parametersRef | object | `{}` | Specific parametersRef for the infrastructure Some gateway implementation like `airlock-microgateway` may need to attach GatewayParameters to create Loadbalancer service automatically. |
| gateway.labels | object | `{}` | Specific labels for the Gateway resource |
| gateway.name | string | `"gluu-gateway"` | The name of the Gateway resource to be created |
| gateway.tlsSecretName | string | `"tls-certificate"` | Secret containing the TLS certificate for the Gateway |
| nameOverride | string | `""` |  |
| routes | object | `{"adminUiEnabled":true,"annotations":{},"authServerEnabled":true,"casaEnabled":false,"deviceCodeEnabled":true,"fido2ConfigEnabled":false,"fido2Enabled":false,"firebaseMessagingEnabled":true,"gatewayNamespace":"","httpSectionName":"http","httpsSectionName":"https","labels":{},"openidConfigEnabled":true,"passportEnabled":false,"rootPath":"/","scimConfigEnabled":false,"scimEnabled":false,"shibEnabled":false,"u2fConfigEnabled":true,"uma2ConfigEnabled":true,"webdiscoveryEnabled":true,"webfingerEnabled":true}` | Configuration for HTTPRoute and its related resources |
| routes.adminUiEnabled | bool | `true` | Enable Admin UI endpoints /identity |
| routes.annotations | object | `{}` | Specific annotations for the HTTPRoute resource |
| routes.authServerEnabled | bool | `true` | Enable Auth server endpoints /oxauth |
| routes.casaEnabled | bool | `false` | Enable casa endpoints /casa |
| routes.deviceCodeEnabled | bool | `true` | Enable endpoint /device-code |
| routes.fido2ConfigEnabled | bool | `false` | Enable endpoint /.well-known/fido2-configuration |
| routes.fido2Enabled | bool | `false` | Enable all fido2 endpoints |
| routes.firebaseMessagingEnabled | bool | `true` | Enable endpoint /firebase-messaging-sw.js |
| routes.gatewayNamespace | string | `""` | Namespace the Gateway resource is deployed in (if different from the release namespace, make sure to create the namespace beforehand and set up necessary RBAC permissions for the controller to manage resources in that namespace). Typically, the HTTPRoute resource will be created in the same namespace as the Gateway resource, but if the gateway is externally managed and deployed in a different namespace, you can set the namespace for HTTPRoute resource here. |
| routes.httpSectionName | string | `"http"` | Set the httpSectionName and httpsSectionName according to your installed controller if it doesn't work with default values (e.g. some controller may require the listener name to be `default`). |
| routes.labels | object | `{}` | Specific labels for the HTTPRoute resource |
| routes.openidConfigEnabled | bool | `true` | Enable endpoint /.well-known/openid-configuration |
| routes.passportEnabled | bool | `false` | Enable passport endpoints /passport |
| routes.rootPath | string | `"/"` | Base endpoint |
| routes.scimConfigEnabled | bool | `false` | Enable endpoint /.well-known/scim-configuration |
| routes.scimEnabled | bool | `false` | Enable SCIM endpoints /scim |
| routes.shibEnabled | bool | `false` | Enable shibboleth endpoints /idp |
| routes.u2fConfigEnabled | bool | `true` | Enable endpoint /.well-known/fido-configuration |
| routes.uma2ConfigEnabled | bool | `true` | Enable endpoint /.well-known/uma2-configuration |
| routes.webdiscoveryEnabled | bool | `true` | Enable endpoint /.well-known/simple-web-discovery |
| routes.webfingerEnabled | bool | `true` | Enable endpoint /.well-known/webfinger |
