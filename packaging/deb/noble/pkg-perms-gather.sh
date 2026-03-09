#!/bin/bash

# Run this file after log into chroot
base_path=`pwd`
find $base_path/gluu-server.amd64/opt -type f ! -type l ! -iname ".gitignore" ! -path "$base_path/proc/*" ! -path "$base_path/sys/*" ! -path "$base_path/dev/*" -printf "%h/%f - %m\n"|sort |uniq > pkg_perm.list
