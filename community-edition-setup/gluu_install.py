#!/usr/bin/python3

import os
import sys
import json
import zipfile
import tarfile
import shutil
import site
import argparse
import csv
import locale
import re
import shlex
import subprocess
from pathlib import Path
from urllib import request
from urllib.parse import urlparse, urljoin
from urllib.error import HTTPError, URLError
from tempfile import TemporaryDirectory

sys.path.append('/usr/lib/python{}.{}/gluu-packaged'.format(sys.version_info.major, sys.version_info.minor))

sys.path.append('/usr/lib/python{}.{}/gluu-packaged'.format(sys.version_info.major, sys.version_info.minor))

parser = argparse.ArgumentParser(description="This script downloads Gluu Server components and fires setup")
parser.add_argument('-a', help=argparse.SUPPRESS, action='store_true')
parser.add_argument('-u', help="Use downloaded components", action='store_true')
parser.add_argument('-upgrade', help="Upgrade Gluu war and jar files", action='store_true')
parser.add_argument('-uninstall', help="Uninstall Gluu server and removes all files", action='store_true')
parser.add_argument('--args', help="Arguments to be passed to setup.py")
parser.add_argument('--keep-downloads', help="Keep downloaded files", action='store_true')

if '-a' in sys.argv:
    parser.add_argument('--jetty-version', help="Jetty verison. For example 11.0.6")
    parser.add_argument('-k', help="Don't validate the server's certificate", action='store_true')

if '-uninstall' not in sys.argv:
    parser.add_argument('-maven-user', help="Maven username", required=True)
    parser.add_argument('-maven-password', help="Maven password", required=True)

parser.add_argument('-n', help="No prompt", action='store_true')
parser.add_argument('--no-setup', help="Do not launch setup", action='store_true')
parser.add_argument('--dist-server-base', help="Download server", default='https://maven.gluu.org/maven4')
parser.add_argument('-profile', help="Setup profile", choices=['CE', 'DISA-STIG'], default='CE')
parser.add_argument('--setup-branch', help="Gluu CE setup github branch", default="4.5")
parser.add_argument('--gluu-version', help="Gluu CE maven artifacts download version")
parser.add_argument(
    '--gluu-git-version',
    help="Gluu CE git version suffix",
    choices=['Final', 'SNAPSHOT'],
)
parser.add_argument('--passport-version', help="Gluu CE Passport version")
parser.add_argument('-c', help="Don't download files that exists on disk", action='store_true')
parser.add_argument('-app-info', help="Use specified app info file instead of downloading form github")


argsp = parser.parse_args()

if '-a' in sys.argv and argsp.k:
    import ssl
    ssl._create_default_https_context = ssl._create_unverified_context

maven_base = argsp.dist_server_base
maven_o = urlparse(maven_base)
maven_root = maven_o._replace(path='').geturl()
setup_ref_kind = None

if argsp.app_info:
    if not os.path.isfile(argsp.app_info):
        print(f"File {argsp.app_info} not found. Please check file location", file=sys.stderr)
        sys.exit(1)
    try:
        with open(argsp.app_info) as f:
            app_versions_str = f.read()
    except OSError as exc:
        print(f"Unable to read {argsp.app_info}: {exc}", file=sys.stderr)
        sys.exit(1)
else:
    for dloc in ('heads', 'tags'):
        github_raw_base_url = f'https://raw.githubusercontent.com/GluuFederation/gluu4/{dloc}/{argsp.setup_branch}/'
        app_info_url = urljoin(github_raw_base_url, 'community-edition-setup/app_info.json')
        try:
            print("Retrieving application info", app_info_url)
            with request.urlopen(app_info_url, timeout=10) as response:
                app_versions_str = response.read()
            setup_ref_kind = dloc
            break
        except (HTTPError, URLError):
            print(f"Unable to download from {dloc}. Trying next location.")
    else:
        print("Can't download app_info.json from github. Exiting ...", file=sys.stderr)
        sys.exit(1)

try:
    app_versions = json.loads(app_versions_str)
except json.decoder.JSONDecodeError as e:
    print("An error occurred while decoding app_info.json. Exiting ...", file=sys.stderr)
    sys.exit(1)

# backward compatibility
if 'GLUU_VERSION' not in app_versions and 'OX_VERSION' in app_versions:
    app_versions['GLUU_VERSION'] = app_versions['OX_VERSION']
if 'GLUU_GITVERISON' not in app_versions and 'OX_GITVERISON' in app_versions:
    app_versions['GLUU_GITVERISON'] = app_versions['OX_GITVERISON']

app_versions['SETUP_BRANCH'] = argsp.setup_branch
app_versions['SETUP_REF_KIND'] = (
    setup_ref_kind
    or app_versions.get('SETUP_REF_KIND')
    or 'heads'
)

if argsp.gluu_git_version:
    app_versions['GLUU_GITVERISON'] = {
        'Final': '.Final',
        'SNAPSHOT': '-SNAPSHOT',
    }[argsp.gluu_git_version]

if argsp.gluu_version:
    app_versions['GLUU_VERSION'] = argsp.gluu_version

if argsp.passport_version:
    app_versions['PASSPORT_VERSION'] = argsp.passport_version

if 'PASSPORT_VERSION' not in app_versions:
    if 'GLUU_GITVERISON' not in app_versions:
        print("GLUU_GITVERISON not found in app_info and --gluu-git-version not provided. Exiting ...", file=sys.stderr)
        sys.exit(1)

    app_versions['PASSPORT_VERSION'] = app_versions['GLUU_VERSION'] + app_versions['GLUU_GITVERISON']

cur_dir = os.path.dirname(os.path.realpath(__file__))
opt_dist_dir = '/var/gluu/dist' if argsp.profile == 'DISA-STIG' else '/opt/dist/'
gluu_app_dir = os.path.join(opt_dist_dir, 'gluu')
app_dir = os.path.join(opt_dist_dir, 'app')
ces_dir = '/install/community-edition-setup'
scripts_dir = os.path.join(opt_dist_dir, 'scripts')
certs_dir = '/etc/certs'
pylib_dir = os.path.join(ces_dir, 'setup_app/pylib/')

os_type, os_version = '', ''

os_release_fn = '/usr/lib/os-release'
if not os.path.exists(os_release_fn):
    os_release_fn = '/etc/os-release'

with open(os_release_fn) as f:
    reader = csv.reader(f, delimiter="=")
    for row in reader:
        if row:
            if row[0] == 'ID':
                os_type = row[1].lower()
                if os_type in ('rhel', 'redhat'):
                    os_type = 'red'
                elif 'ubuntu-core' in os_type:
                    os_type = 'ubuntu'
                elif 'sles' in os_type or 'suse' in os_type:
                    os_type = 'suse'
            elif row[0] == 'VERSION_ID':
                os_version = row[1].split('.')[0]

cmdline = False

if os_type in ('red', 'centos'):
    package_installer = 'yum'
elif os_type in ('ubuntu', 'debian'):
    package_installer = 'apt'
elif os_type in ('suse'):
    package_installer = 'zypper'
else:
    print("Unsopported OS. Exiting ...")
    sys.exit()

if os_type == 'debian':
    path_list = [ '/usr/local/sbin', '/usr/sbin', '/sbin', '/usr/local/bin', '/usr/bin', '/bin' ]
    os.environ['PATH'] = os.pathsep.join(path_list) + os.pathsep + os.environ['PATH']

print("OS type was determined as {}.".format(os_type))

try:
    locale.setlocale(locale.LC_ALL, '')
except:
    cmdline = True

missing_packages = []

if not argsp.uninstall:

    try:
        import ldap3
    except:
        missing_packages.append('python3-ldap3')

    try:
        import six
    except:
        missing_packages.append('python3-six')

    try:
        import ruamel.yaml
    except:
        if os_type in ('red', 'centos'):
            missing_packages.append('python3-ruamel-yaml')
        else:
            missing_packages.append('python3-ruamel.yaml')


    try:
        import pymysql
    except:
        if os_type in ('red', 'centos', 'suse'):
            missing_packages.append('python3-PyMySQL')
        else:
            missing_packages.append('python3-pymysql')

    try:
        import psycopg2
    except:
        missing_packages.append('python3-psycopg2')

    if not shutil.which('unzip'):
        missing_packages.append('unzip')

    if not shutil.which('tar'):
        missing_packages.append('tar')

    rpm_clone = shutil.which('rpm')
    deb_clone = shutil.which('deb')

    if missing_packages:
        packages_str = ' '.join(missing_packages)
        if os_type+os_version in ('centos9'):
            packages_str = packages_str.replace('python3-', 'python-')
        if not argsp.n:
            result = input("Missing package(s): {0}. Install now? (Y|n): ".format(packages_str))
            if result.strip() and result.strip().lower()[0] == 'n':
                sys.exit("Can't continue without installing these packages. Exiting ...")

        if os_type in ('red', 'centos'):
            print("Installing epel-release")
            cmd = '{} install -y https://dl.fedoraproject.org/pub/epel/epel-release-latest-{}.noarch.rpm'.format(package_installer, os_version)
            os.system(cmd)
            cmd = '{} clean all'
            os.system(cmd)
            print("Enabling CRB repository")
            os.system('/usr/bin/crb enable')

        elif deb_clone:
            subprocess.run(shlex.split('{} update'.format(package_installer)))

        cmd = "{} install -y {}".format(package_installer, packages_str)

        os.system(cmd)

if not os.path.exists(scripts_dir):
    os.makedirs(scripts_dir)

oxauth_war_fn = os.path.join(gluu_app_dir, 'oxauth.war')
jetty_home = '/opt/gluu/jetty'
services = ['casa.service', 'identity.service', 'opendj.service', 'oxauth.service', 'passport.service', 'fido2.service', 'idp.service', 'scim.service']

if os.path.exists('/opt/oxd-server') or os.path.exists('/etc/systemd/system/oxd-server.service'):
    services.append('oxd-server.service')

jetty_dist_string = 'jetty-distribution'
if argsp.a and hasattr(argsp, 'jetty_version') and argsp.jetty_version:
    app_versions['JETTY_VERSION'] = argsp.jetty_version

result = re.findall(r'(\d*).', app_versions['JETTY_VERSION'])

if result and result[0] and result[0].isdigit() and int(result[0]) > 9:
    jetty_dist_string = 'jetty-home'


gluu_archieve = 'gluu-{}.zip'.format(app_versions['APPS_GIT_BRANCH'])

def check_installation():
    if not (os.path.exists(jetty_home) and os.path.exists('/etc/gluu')):
        print("Gluu server seems not installed")
        sys.exit()

if argsp.uninstall:
    check_installation()
    print('\033[31m')
    print("This process is irreversible.")
    print("You will lose all data related to Gluu Server.")
    print('\033[0m')
    print()
    if not argsp.n:
        while True:
            print('\033[31m \033[1m')
            response = input("Are you sure to uninstall Gluu Server? [yes/N] ")
            print('\033[0m')
            if response.lower() in ('yes', 'n', 'no'):
                if not response.lower() == 'yes':
                    sys.exit()
                else:
                    break
            else:
                print("Please type \033[1m yes \033[0m to uninstall")

    print("Uninstalling Gluu Server...")

    if os.path.exists('/opt/opendj/bin/stop-ds'):
        print("Stopping OpenDj Server")
        os.system('/opt/opendj/bin/stop-ds')
    for uf in services:
        service,ext = os.path.splitext(uf)
        should_stop = os.path.exists(os.path.join(jetty_home, service)) or service == 'oxd-server'
        if should_stop:
            default_fn = os.path.join('/etc/default/', service)
            if os.path.exists(default_fn):
                print("Removing", default_fn)
                os.remove(default_fn)
            print("Stopping", service)
            os.system('systemctl stop ' + service)
            os.system('systemctl disable ' + service)

    remove_list = ['/etc/certs', '/etc/gluu', '/opt/gluu', '/opt/amazon-corretto*', '/opt/jre', '/opt/jetty*', '/opt/jython*', '/opt/opendj', '/opt/node*', '/opt/shibboleth-idp', '/var/gluu/identity/cr-snapshots/*']

    if os.path.exists('/opt/oxd-server'):
        remove_list.append('/opt/oxd-server')

    if not argsp.keep_downloads:
        remove_list.append('/opt/dist')

    for p in remove_list:
        cmd = 'rm -r -f ' + p
        print("Executing", cmd)
        os.system('rm -r -f ' + p)

    apache_conf_fn_list = []

    if shutil.which('zypper'):
        apache_conf_fn_list = ['/etc/apache2/vhosts.d/_https_gluu.conf']
    elif shutil.which('yum') or shutil.which('dnf'):
        apache_conf_fn_list = ['/etc/httpd/conf.d/https_gluu.conf']
    elif shutil.which('apt'):
        apache_conf_fn_list = ['/etc/apache2/sites-enabled/https_gluu.conf', '/etc/apache2/sites-available/https_gluu.conf']

    for fn in apache_conf_fn_list:
        if os.path.exists(fn):
            print("Removing", fn)
            os.unlink(fn)

    sys.exit()


passman = request.HTTPPasswordMgrWithDefaultRealm()
passman.add_password(None, maven_root, argsp.maven_user, argsp.maven_password)
authhandler = request.HTTPBasicAuthHandler(passman)
opener = request.build_opener(authhandler)
request.install_opener(opener)


def download(url, target_fn):
    dst = os.path.join(app_dir, target_fn)
    pardir, fn = os.path.split(dst)
    if not os.path.exists(pardir):
        os.makedirs(pardir)

    parsed = urlparse(url)
    if parsed.scheme not in ("https",):
        print(f"Unsupported URL scheme '{parsed.scheme}' for {url}. Exiting ...", file=sys.stderr)
        sys.exit(2)

    print("Opening url", url)

    try:
        with request.urlopen(url, timeout=30) as resp:
            if argsp.c and os.path.exists(dst) and resp.length == os.stat(dst).st_size:
                print("File", dst, "exists. Passing")
                return

            print("Downloading", url, "to", dst)
            with open(dst, 'wb') as out_file :
                shutil.copyfileobj(resp, out_file)
    except (HTTPError, URLError):
        env_var = re.sub(r'[^_a-zA-Z0-9]', '_', fn)
        if env_var[0].isnumeric():
            env_var = '_' + env_var
        print(f"Unable to download {url}, looking for environmental variable {env_var} for fallback")
        src = os.environ.get(env_var)
        if src and os.path.isfile(src):
            if os.path.exists(dst) and os.path.samefile(src, dst):
                print(f"Fallback source {src} already matches destination {dst}. Passing")
                return

            print(f"Copying {src} to {dst}")
            shutil.copy(src, dst)
        else:
            print(f"Source file {src} does not exist. Exiting ...")
            sys.exit(2)

def extract_subdir(zip_fn, sub_dir, target_dir, par_dir=None, overwrite=False):
    target_fp = os.path.join(target_dir, os.path.basename(sub_dir))

    zip_obj = zipfile.ZipFile(zip_fn, "r")
    members = zip_obj.infolist()

    if par_dir is None:
        par_dir = members[0].filename

    subdir_with_parent = os.path.join(par_dir, sub_dir) if par_dir else sub_dir

    for member in members:
        if member.filename.startswith(subdir_with_parent):
            if sub_dir:
                n = sub_dir.count('/') + 1
                member_path = Path(member.filename)
                extract_path = Path(*member_path.parts[n:]).as_posix()
            else:
                extract_path = member.filename

            extracted_path = os.path.join(target_dir, extract_path)
            if not overwrite and os.path.exists(extracted_path):
                continue

            if member.is_dir():
                if not os.path.exists(extracted_path):
                    os.makedirs(extracted_path)
            else:
                member.filename = extract_path
                zip_obj.extract(member, target_dir)

            if member.external_attr >  0xffff:
                 os.chmod(extracted_path, member.external_attr >> 16)

    zip_obj.close()


if not argsp.u:

    if argsp.profile != 'DISA-STIG':
        download('https://corretto.aws/downloads/resources/{0}/amazon-corretto-{0}-linux-x64.tar.gz'.format(app_versions['AMAZON_CORRETTO_VERSION']), os.path.join(app_dir, 'amazon-corretto-{0}-linux-x64.tar.gz'.format(app_versions['AMAZON_CORRETTO_VERSION'])))
        download('https://nodejs.org/dist/{0}/node-{0}-linux-x64.tar.xz'.format(app_versions['NODE_VERSION']), os.path.join(app_dir, 'node-{0}-linux-x64.tar.xz'.format(app_versions['NODE_VERSION'])))
        download('https://www.apple.com/certificateauthority/Apple_WebAuthn_Root_CA.pem', os.path.join(app_dir, 'Apple_WebAuthn_Root_CA.pem'))
        download('https://www.apple.com/certificateauthority/Apple_WebAuthn_Root_CA.pem', os.path.join(app_dir, 'Apple_WebAuthn_Root_CA.pem'))
        download(os.path.join(maven_root, 'npm4/passport/passport-{}.tgz'.format(app_versions['PASSPORT_VERSION'])), os.path.join(gluu_app_dir,'passport.tgz'))
        download(os.path.join(maven_root, 'npm4/passport/passport-{}-node_modules.tar.gz'.format(app_versions['PASSPORT_VERSION'])), os.path.join(gluu_app_dir,'passport-version_{}-node_modules.tar.gz'.format(app_versions['PASSPORT_VERSION'])))
        download(os.path.join(maven_base, 'org/gluu/super-gluu-radius-server/{0}{1}/super-gluu-radius-server-{0}{1}.jar'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), os.path.join(gluu_app_dir, 'super-gluu-radius-server.jar'))
        download(os.path.join(maven_base, 'org/gluu/super-gluu-radius-server/{0}{1}/super-gluu-radius-server-{0}{1}-distribution.zip'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), os.path.join(gluu_app_dir, 'gluu-radius-libs.zip'))
        download(os.path.join(maven_base, 'org/gluu/oxShibbolethStatic/{0}{1}/oxShibbolethStatic-{0}{1}.jar'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), os.path.join(gluu_app_dir,'shibboleth-idp.jar'))
        download(os.path.join(maven_base, 'org/gluu/oxshibbolethIdp/{0}{1}/oxshibbolethIdp-{0}{1}.war'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), os.path.join(gluu_app_dir,'idp.war'))
        download(os.path.join(maven_base, 'org/gluu/oxShibbolethKeyGenerator/{0}{1}/oxShibbolethKeyGenerator-{0}{1}.jar'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), os.path.join(gluu_app_dir, 'idp3_cml_keygenerator.jar'))
        download(os.path.join(maven_base, 'org/gluu/oxauth-server/{0}{1}/oxauth-server-{0}{1}.war'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), os.path.join(gluu_app_dir, 'oxauth.war'))
        download(os.path.join(maven_base, 'org/gluu/scim-server/{0}{1}/scim-server-{0}{1}.war'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), os.path.join(gluu_app_dir,'scim.war'))
        download(os.path.join(maven_base, 'org/gluu/fido2-server/{0}{1}/fido2-server-{0}{1}.war'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), os.path.join(gluu_app_dir,'fido2.war'))
        download(os.path.join(maven_base, 'org/gluu/casa/{0}{1}/casa-{0}{1}.war'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), os.path.join(gluu_app_dir,'casa.war'))
        download(os.path.join(maven_base, 'org/gluu/oxtrust-server/{0}{1}/oxtrust-server-{0}{1}.war'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), os.path.join(gluu_app_dir,'identity.war'))
        download(os.path.join(maven_base, 'org/gluu/gluu-orm-spanner-libs/{0}{1}/gluu-orm-spanner-libs-{0}{1}-distribution.zip'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), os.path.join(gluu_app_dir, 'gluu-orm-spanner-libs-distribution.zip'))
        download(os.path.join(maven_base, 'org/gluu/gluu-orm-couchbase-libs/{0}{1}/gluu-orm-couchbase-libs-{0}{1}-distribution.zip'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), os.path.join(gluu_app_dir, 'gluu-orm-couchbase-libs-distribution.zip'))

    else:
        download(os.path.join(maven_base, 'org/gluu/oxauth-client-jar-without-provider-dependencies/{0}{1}/oxauth-client-jar-without-provider-dependencies-{0}{1}.jar'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), os.path.join(gluu_app_dir, 'oxauth-client-jar-without-provider-dependencies.jar'))
        download(os.path.join(maven_base, 'org/gluu/oxauth-server-fips/{0}{1}/oxauth-server-fips-{0}{1}.war'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), oxauth_war_fn)
        download(os.path.join(maven_base, 'org/gluu/scim-server-fips/{0}{1}/scim-server-fips-{0}{1}.war'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), os.path.join(gluu_app_dir,'scim.war'))
        download(os.path.join(maven_base, 'org/gluu/fido2-server-fips/{0}{1}/fido2-server-fips-{0}{1}.war'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), os.path.join(gluu_app_dir,'fido2.war'))
        download(os.path.join(maven_base, 'org/gluu/casa-fips/{0}{1}/casa-fips-{0}{1}.war'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), os.path.join(gluu_app_dir,'casa.war'))
        download(os.path.join(maven_base, 'org/gluu/oxtrust-server-fips/{0}{1}/oxtrust-server-fips-{0}{1}.war'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), os.path.join(gluu_app_dir,'identity.war'))


    download(os.path.join(maven_base, 'org/gluu/oxauth-client-jar-with-dependencies/{0}{1}/oxauth-client-jar-with-dependencies-{0}{1}.jar'.format(app_versions['GLUU_VERSION'], app_versions['GLUU_GITVERISON'])), os.path.join(gluu_app_dir,'oxauth-client-jar-with-dependencies.jar'))
    download(os.path.join(maven_base, 'org/gluufederation/jython-installer/{0}/jython-installer-{0}.jar'.format(app_versions['JYTHON_VERSION'])), os.path.join(app_dir, 'jython-installer-{0}.jar'.format(app_versions['JYTHON_VERSION'])))
    download(os.path.join(maven_base, 'org/gluufederation/opendj/opendj-server-legacy/{0}/opendj-server-legacy-{0}.zip'.format(app_versions['OPENDJ_VERSION'])), os.path.join(app_dir,'opendj-server-{0}.zip'.format(app_versions['OPENDJ_VERSION'])))

    download('https://github.com/npcole/npyscreen/archive/master.zip', os.path.join(app_dir, 'npyscreen-master.zip'))
    download('https://repo1.maven.org/maven2/org/eclipse/jetty/{1}/{0}/{1}-{0}.tar.gz'.format(app_versions['JETTY_VERSION'], jetty_dist_string), os.path.join(app_dir,'{1}-{0}.tar.gz'.format(app_versions['JETTY_VERSION'], jetty_dist_string)))
    download('https://repo1.maven.org/maven2/com/twilio/sdk/twilio/{0}/twilio-{0}.jar'.format(app_versions['TWILIO_VERSION']), os.path.join(gluu_app_dir,'twilio-{0}.jar'.format(app_versions['TWILIO_VERSION'])))
    download('https://repo1.maven.org/maven2/org/jsmpp/jsmpp/{0}/jsmpp-{0}.jar'.format(app_versions['JSMPP_VERSION']), os.path.join(gluu_app_dir,'jsmpp-{0}.jar'.format(app_versions['JSMPP_VERSION'])))
    download('https://raw.githubusercontent.com/JanssenProject/jans/refs/heads/main/jans-linux-setup/jans_setup/static/scripts/facter', os.path.join(gluu_app_dir,'facter'))
    download('https://github.com/GluuFederation/gluu4/archive/refs/{}/{}.zip'.format(app_versions['SETUP_REF_KIND'], app_versions['SETUP_BRANCH']), os.path.join(gluu_app_dir, gluu_archieve))
    download('https://github.com/sqlalchemy/sqlalchemy/archive/rel_1_3_23.zip', os.path.join(app_dir, 'sqlalchemy.zip'))
    download('https://mds.fidoalliance.org/', os.path.join(app_dir, 'fido2/mds/toc/toc.jwt'))
    download('https://secure.globalsign.com/cacert/root-r3.crt', os.path.join(app_dir, 'fido2/mds/cert/root-r3.crt'))
    download('https://github.com/jpadilla/pyjwt/archive/refs/tags/2.12.1.zip', os.path.join(app_dir, 'pyjwt.zip'))
    download('https://gitlab.com/doctormo/python-crontab/-/archive/v3.2.0/python-crontab-v3.2.0.zip', os.path.join(app_dir, 'python-crontab.zip'))


shutil.copy(os.path.join(gluu_app_dir, 'facter'), '/usr/bin')
os.chmod('/usr/bin/facter', 33261)
if not os.path.exists(certs_dir):
    os.makedirs(certs_dir)

if argsp.upgrade:

    check_installation()

    for service in os.listdir(jetty_home):
        source_fn = os.path.join(gluu_app_dir, service +'.war')
        target_fn = os.path.join(jetty_home, service, 'webapps', service +'.war' )
        print("Updating", target_fn)
        shutil.copy(source_fn, target_fn)
        print("Restarting", service)
        os.system('systemctl restart ' + service)

else:
    print("Extracting community-edition-setup package")

    extract_subdir(
        os.path.join(gluu_app_dir, gluu_archieve),
        'community-edition-setup',
        os.path.dirname(ces_dir)
        )


    extract_libs = [
            ('npyscreen-master.zip', 'npyscreen', None)
            ]
    if argsp.profile != 'DISA-STIG':
        extract_libs += [
                    ('sqlalchemy.zip', 'lib/sqlalchemy', None),
                    ('pyjwt.zip', 'jwt', None),
                    ]

    for zip_fn, sub_dir, par_dir in extract_libs:
        print("Extracting", zip_fn)
        extract_subdir(os.path.join(app_dir, zip_fn), sub_dir, pylib_dir, par_dir)

    if argsp.profile == 'DISA-STIG':
        open(os.path.join(ces_dir, 'disa-stig'), 'w').close()

    if argsp.profile == 'DISA-STIG':
        war_zip = zipfile.ZipFile(oxauth_war_fn, "r")
        for fn in war_zip.namelist():
            if re.search(r'bc-fips-(.*?).jar$', fn) or re.search(r'bcpkix-fips-(.*?).jar$', fn):
                file_name = os.path.basename(fn)
                target_fn = os.path.join(app_dir, file_name)
                print("Extracting", fn, "to", target_fn)
                file_content = war_zip.read(fn)
                with open(target_fn, 'wb') as w:
                    w.write(file_content)
        war_zip.close()

    os.chmod('/install/community-edition-setup/setup.py', 33261)

    gluu_install = '/install/community-edition-setup/gluu_install.py'
    if os.path.exists(gluu_install):
        os.remove(gluu_install)

    if not argsp.no_setup:
        print("Launching Gluu Setup")
        setup_cmd = 'python3 {}/setup.py'.format(ces_dir)
        if argsp.args:
            setup_cmd += ' ' + argsp.args

        os.system(setup_cmd)
