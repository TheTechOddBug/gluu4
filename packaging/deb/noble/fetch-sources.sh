 #!/bin/bash
+set -euo pipefail

VER=$1
INSTALL_VER=$2
CASA_SOURCE=$3
OXD_SOURCE=$4

DIRWEB="gluu-server.amd64/gluu-server/opt/dist/gluu"
COMMUNITY="gluu-server.amd64/gluu-server/install"
OPT="gluu-server.amd64/gluu-server/opt"
GLUU_ROOT="gluu-server.amd64/gluu-server"

INSTALL="master"
if [ -n "${INSTALL_VER}" ]; then
    INSTALL=$INSTALL_VER
fi

if [ -n "${VER}" ]; then
  #  wget -nv https://ox.gluu.org/maven/org/gluu/fido2-server/$VER/fido2-server-$VER.war -O  $DIRWEB/fido2.war
  #  wget -nv https://ox.gluu.org/maven/org/gluu/scim-server/$VER/scim-server-$VER.war -O  $DIRWEB/scim.war
  #  wget -nv http://ox.gluu.org/maven/org/gluu/oxauth-client/$VER/oxauth-client-$VER-jar-with-dependencies.jar -O $DIRWEB/oxauth-client-jar-with-dependencies.jar
  #  wget -nv http://ox.gluu.org/maven/org/gluu/oxshibbolethIdp/$VER/oxshibbolethIdp-$VER.war -O $DIRWEB/idp.war
  #  wget -nv http://ox.gluu.org/maven/org/gluu/oxtrust-server/$VER/oxtrust-server-$VER.war -O $DIRWEB/identity.war
  #  wget -nv http://ox.gluu.org/maven/org/gluu/oxauth-server/$VER/oxauth-server-$VER.war -O $DIRWEB/oxauth.war
  #  #wget -nv http://ox.gluu.org/maven/org/gluu/oxauth-rp/$VER/oxauth-rp-$VER.war -O $DIRWEB/oxauth-rp.war
  #  wget -nv http://ox.gluu.org/maven/org/gluu/oxShibbolethStatic/$VER/oxShibbolethStatic-$VER.jar -O $DIRWEB/shibboleth-idp.jar
  #  wget -nv http://ox.gluu.org/maven/org/gluu/oxShibbolethKeyGenerator/$VER/oxShibbolethKeyGenerator-$VER.jar -O $DIRWEB/idp3_cml_keygenerator.jar
    
  #  wget -nv https://github.com/GluuFederation/community-edition-setup/archive/$INSTALL.zip -O $DIRWEB/community-edition-setup.zip
  # Use an immutable ref (tag/commit SHA) and verify digest
    wget -nv "https://raw.githubusercontent.com/GluuFederation/community-edition-setup/${INSTALL}/install.py" -O "gluu-server.amd64/gluu-server/opt/gluu/bin/install.py"
    echo "<sha256>  gluu-server.amd64/gluu-server/opt/gluu/bin/install.py" | sha256sum -c -   
    chmod +x gluu-server.amd64/gluu-server/opt/gluu/bin/install.py
    
  #  wget https://ox.gluu.org/npm/passport/passport-4.3.1.tgz -O $DIRWEB/passport.tgz
  #  wget https://ox.gluu.org/npm/passport/passport-$INSTALL-node_modules.tar.gz -O $DIRWEB/passport-$INSTALL-node_modules.tar.gz
    wget -nv "https://raw.githubusercontent.com/GluuFederation/community-edition-package/${INSTALL}/package/systemd/gluu-serverd" -O "gluu-server.amd64/gluu-server/tmp/gluu-serverd"
    echo "<sha256>  gluu-server.amd64/gluu-server/tmp/gluu-serverd" | sha256sum -c -
    chmod +x gluu-server.amd64/gluu-server/tmp/gluu-serverd
  #  wget -nv https://ox.gluu.org/maven/org/gluu/super-gluu-radius-server/$VER/super-gluu-radius-server-$VER-distribution.zip -O $DIRWEB/gluu-radius-libs.zip
  #  wget -nv https://ox.gluu.org/maven/org/gluu/super-gluu-radius-server/$VER/super-gluu-radius-server-$VER.jar -O $DIRWEB/super-gluu-radius-server.jar
  
    # systemd files for services
  #  wget https://raw.githubusercontent.com/GluuFederation/community-edition-package/$INSTALL/package/systemd/scim.service -O gluu-server.amd64/gluu-server/lib/systemd/system/scim.service
  #  wget https://raw.githubusercontent.com/GluuFederation/community-edition-package/$INSTALL/package/systemd/fido2.service -O gluu-server.amd64/gluu-server/lib/systemd/system/fido2.service
  #  wget https://raw.githubusercontent.com/GluuFederation/community-edition-package/$INSTALL/package/systemd/identity.service -O gluu-server.amd64/gluu-server/lib/systemd/system/identity.service 
  #  wget https://raw.githubusercontent.com/GluuFederation/community-edition-package/$INSTALL/package/systemd/opendj.service -O gluu-server.amd64/gluu-server/lib/systemd/system/opendj.service
  #  wget https://raw.githubusercontent.com/GluuFederation/community-edition-package/$INSTALL/package/systemd/oxauth-rp.service -O gluu-server.amd64/gluu-server/lib/systemd/system/oxauth-rp.service
  #  wget https://raw.githubusercontent.com/GluuFederation/community-edition-package/$INSTALL/package/systemd/oxauth.service -O gluu-server.amd64/gluu-server/lib/systemd/system/oxauth.service
  #  wget https://raw.githubusercontent.com/GluuFederation/community-edition-package/$INSTALL/package/systemd/passport.service -O gluu-server.amd64/gluu-server/lib/systemd/system/passport.service
  #  wget https://raw.githubusercontent.com/GluuFederation/community-edition-package/$INSTALL/package/systemd/idp.service -O gluu-server.amd64/gluu-server/lib/systemd/system/idp.service
    
    # Casa files
  #  wget https://ox.gluu.org/maven/org/gluu/casa/$CASA_SOURCE/casa-$CASA_SOURCE.war -O $DIRWEB/casa.war
  #  wget https://repo1.maven.org/maven2/com/twilio/sdk/twilio/7.17.0/twilio-7.17.0.jar -O $DIRWEB/twilio-7.17.0.jar
  #  wget https://search.maven.org/remotecontent?filepath=org/jsmpp/jsmpp/2.3.7/jsmpp-2.3.7.jar -O $DIRWEB/jsmpp-2.3.7.jar

  #  mkdir -p $GLUU_ROOT/etc/certs
  #  wget https://github.com/GluuFederation/casa/raw/$INSTALL/extras/casa.pub -O $GLUU_ROOT/etc/certs/casa.pub
    
  #  wget https://raw.githubusercontent.com/GluuFederation/community-edition-package/$INSTALL/package/systemd/casa.service -O gluu-server.amd64/gluu-server/lib/systemd/system/casa.service
    
    # oxd files
  #  mkdir -p $DIRWEB/oxd-server/bin $DIRWEB/oxd-server/data $DIRWEB/oxd-server/lib $DIRWEB/oxd-server/conf
  #  wget https://raw.githubusercontent.com/GluuFederation/oxd/$INSTALL/oxd-server/src/main/bin/lsox.sh -O $DIRWEB/oxd-server/bin/lsox.sh
  #  wget https://raw.githubusercontent.com/GluuFederation/oxd/$INSTALL/oxd-server/src/main/bin/oxd-start.sh -O $DIRWEB/oxd-server/bin/oxd-start.sh
  #  wget https://raw.githubusercontent.com/GluuFederation/oxd/$INSTALL/debian/oxd-server -O $DIRWEB/oxd-server/bin/oxd-server
    
  #  wget https://github.com/GluuFederation/oxd/raw/$INSTALL/oxd-server/src/main/resources/oxd-server.keystore -O $DIRWEB/oxd-server/conf/oxd-server.keystore
  #  wget https://raw.githubusercontent.com/GluuFederation/oxd/$INSTALL/oxd-server/src/main/resources/oxd-server.yml -O $DIRWEB/oxd-server/conf/oxd-server.yml
  #  wget https://raw.githubusercontent.com/GluuFederation/oxd/$INSTALL/oxd-server/src/main/resources/swagger.yaml -O $DIRWEB/oxd-server/conf/swagger.yaml
    
  #  wget https://ox.gluu.org/maven/org/gluu/oxd-server/$OXD_SOURCE/oxd-server-$OXD_SOURCE.jar -O $DIRWEB/oxd-server/lib/oxd-server.jar
  #  cp /home/jenkins/oxd_files/bcprov-jdk15on-1.64.jar $DIRWEB/oxd-server/lib/    
  #  cp /home/jenkins/oxd_files/bcpkix-jdk15on-1.54.jar $DIRWEB/oxd-server/lib/
    
  #  wget https://raw.githubusercontent.com/GluuFederation/community-edition-package/$INSTALL/package/systemd/oxd-server.service -O $DIRWEB/oxd-server/oxd-server.service
  #  pushd $DIRWEB/
  #    tar -czf oxd-server.tgz oxd-server
  #    rm -rf oxd-server
  #  popd
    
  #  wget https://raw.githubusercontent.com/GluuFederation/gluu-snap/master/facter/facter -O $GLUU_ROOT/usr/bin/facter
  #  chmod +x $GLUU_ROOT/usr/bin/facter    
fi
