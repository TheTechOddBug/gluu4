#!/bin/bash
set -e

rm -f ../*.{deb,dsc,tar.gz,build,changes,buildinfo}

pushd ../gluu-server.amd64 > /dev/null
debuild clean
popd > /dev/null
