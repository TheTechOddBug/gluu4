import base64
import json
import time
import requests
import jwt
import sys

from urllib.parse import urljoin, urlparse

from setup_app.config import Config
from setup_app.utils.base import logIt

Config.product_code = 'gluu4'


class LicenseError(Exception):
    """Exception raised if there is an issue with SSA."""
    pass

LICENSER = 'https://cloud-dev.gluu.cloud'
SSA_VALIDATION_URL = 'https://account-dev.gluu.cloud/jans-auth/restv1/jwks'

class LicenseActivator:

    def __init__(self):
        self.session = requests.session()

    def set_ssa(self, ssa):

        self.ssa = ssa
        self.decoded_payload = self.decode_jwt_payload()

        exp = self.decoded_payload.get('exp')
        if exp is None:
            raise LicenseError("SSA is missing exp claim.")


        if exp < time.time() + 60 * 10:
            raise LicenseError("SSA expired. Please get new one.")

        Config.ssa = ssa

        if not (self.decoded_payload.get('software_id') and self.decoded_payload.get('org_id') and self.decoded_payload.get('fqdn') and self.ssa_issuer):
            raise LicenseError("At least one of the fields software_id, org_id, fqdn or iss is missing in SSA payload")

        self.ssa_issuer = self.ssa_issuer.rstrip('/')



    def decode_jwt_payload(self, token=None):

        logIt("Decoding SSA")

        if not token:
            token = self.ssa

        parts = token.split('.')
        if len(parts) != 3:
            raise LicenseError("Invalid JWT format")

        payload_b64 = parts[1]

        missing_padding = len(payload_b64) % 4
        if missing_padding:
            payload_b64 += '=' * (4 - missing_padding)

        try:
            decoded_payload = base64.urlsafe_b64decode(payload_b64).decode('utf-8')
        except Exception as e:
            raise LicenseError("Error while decoding JWT.") from e

        try:
            decoded_data = json.loads(decoded_payload)
        except json.JSONDecodeError as e:
            raise LicenseError("JWT payload is not valid json.") from e

        return decoded_data


    def validate_ssa(self, ssa=None):

        logIt(f"Validating SSA from {SSA_VALIDATION_URL}")

        if not ssa:
            ssa = self.ssa

        jwks_client = jwt.PyJWKClient(SSA_VALIDATION_URL)
        parsed_uri = urlparse(SSA_VALIDATION_URL)

        try:
            signing_key = jwks_client.get_signing_key_from_jwt(ssa)
            decoded_jwt = jwt.decode(
                        ssa,
                        signing_key.key,
                        algorithms=["RS256"],
                        options={"verify_aud": False},
                        issuer=f'{parsed_uri.scheme}://{parsed_uri.netloc}'
                    )

        except jwt.exceptions.PyJWTError as e:
            raise LicenseError(f"SSA validation failed: {e}") from e


        Config.software_id = decoded_jwt['software_id']
        Config.org_id = decoded_jwt['org_id']
        Config.hostname = decoded_jwt['fqdn']
        self.ssa_issuer = self.decoded_payload.get('iss')

    def get_http_auth_header_with_scope(self, scope):
        url = urljoin(self.ssa_issuer, 'jans-auth/restv1/token')
        logIt(f"Retreiving access token for scope {scope} from {url}")
        data = {
            'grant_type': 'client_credentials',
            'scope': scope
        }

        try:
            response = self.session.post(
                    url=url,
                    auth=(self.client_id, self.client_secret),
                    data=data,
                    timeout=30,
                    )
        except Exception as e:
            raise LicenseError(f"Error while getting auth token from client created with SSA: {e}") from e

        if response.status_code not in (200, 201):
            raise LicenseError(f"Server {url} returned error wile getting access token for scope {scope}. Status code: {response.status_code}. Response text: {response.text}")

        try:
            result = response.json()
        except json.JSONDecodeError as e:
            raise LicenseError(f"Server {url} did not return valid json data: {e}") from e

        access_token = result.get('access_token')

        if not access_token:
            raise LicenseError("auth_token is not in server response")

        auth_header = {
            'Content-Type': 'application/json',
            'Authorization': f'Bearer {access_token}'
            }

        return auth_header


    def register_client(self):
        url = urljoin(self.ssa_issuer, 'jans-auth/restv1/register')
        logIt(f"Registering client at {url}")
        data = {
              "client_name": "mbclient",
              "redirect_uris": [self.ssa_issuer],
              "software_statement": self.ssa,
              "grant_types": ["client_credentials"],
              "lifetime": 86400
            }

        try:
            response = self.session.post(
                        url=url,
                        data=json.dumps(data),
                        headers={'Content-Type': 'application/json'},
                        timeout=30,
                    )
        except Exception as e:
            raise LicenseError(f"Error while creating client from SSA: {e}") from e

        if response.status_code not in (200, 201):
            raise LicenseError(f"Server {url} returned error while creating client with SSA. Status code: {response.status_code}. Response text: {response.text}")

        try:
            result = response.json()
        except json.JSONDecodeError as e:
            raise LicenseError(f"Server {url} did not return valid json data: {e}") from e

        self.client_id = result.get('client_id')
        self.client_secret = result.get('client_secret')

        if not (self.client_id  and self.client_secret):
            raise LicenseError("client_id or client_secret is not in server response")


    def fetch_license(self):
        scope = 'https://jans.io/oauth/jans-auth-server/config/license'
        url = urljoin(LICENSER, 'v1/license/fetch')
        logIt(f"Fetcing license from {url}")
        params = {
            'org_id': Config.org_id,
            'productCode': Config.product_code,
            'fqdn': Config.hostname
            }
        headers = self.get_http_auth_header_with_scope(scope)

        try:
            response = self.session.get(
                    url=url,
                    params=params,
                    headers=headers,
                    timeout=30
                    )
        except Exception as e:
            raise LicenseError(f"Error while getting fetching license from {url}: {e}") from e
        if response.status_code not in (200, 201):
            raise LicenseError(f"Server {url} returned error wile fetching license. Status code: {response.status_code}. Response text: {response.text}")

        try:
            result = response.json()
        except json.JSONDecodeError as e:
            raise LicenseError(f"Server {url} did not return valid json data: {e}") from e

        license_key = result.get('licenseKey')

        if not license_key:
            raise LicenseError("auth_token is not in server response")

        return license_key


    def check_license(self, license_key):
        scope = 'https://jans.io/oauth/jans-auth-server/config/license'
        url = urljoin(LICENSER, 'v1/license/check')
        logIt(f"Checking license from {url}")
        data = {
                "licenseKey": license_key,
                "hardwareId": Config.hostname,
                "productCode": Config.product_code
            }
        headers = self.get_http_auth_header_with_scope(scope)

        try:
            response = self.session.post(
                    url=url,
                    data=json.dumps(data),
                    headers=headers,
                    timeout=30
                    )
        except Exception as e:
            raise LicenseError(f"Error while checking license from {url}: {e}") from e
        if response.status_code not in (200, 201):
            raise LicenseError(f"Server {url} returned error wile checking license. Status code: {response.status_code}. Response text: {response.text}")

        try:
            result = response.json()
        except json.JSONDecodeError as e:
            raise LicenseError(f"Server {url} did not return valid json data: {e}") from e


        return result


    def activate_license(self, license_key):
        scope = 'https://jans.io/oauth/jans-auth-server/config/license'
        url = urljoin(LICENSER, 'v1/license/activation')
        logIt(f"Activating license from {url}")
        data = {
                "licenseKey": license_key,
                "hardwareId": Config.hostname,
                "productCode": Config.product_code
            }
        headers = self.get_http_auth_header_with_scope(scope)

        try:
            response = self.session.post(
                    url=url,
                    data=json.dumps(data),
                    headers=headers,
                    timeout=30
                    )
        except Exception as e:
            raise LicenseError(f"Error while activating license from {url}: {e}") from e
        if response.status_code not in (200, 201):
            raise LicenseError(f"Server {url} returned error wile activating license. Status code: {response.status_code}. Response text: {response.text}")

        try:
            result = response.json()
        except json.JSONDecodeError as e:
            raise LicenseError(f"Server {url} did not return valid json data: {e}") from e

        for key, value in result.items():
            setattr(Config, key, value)

        return result


