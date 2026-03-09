#!/bin/bash

# Run this file after log into chroot
find / -xdev -type f ! -iname ".gitignore" ! -path "/proc/*" ! -path "/sys/*" ! -path "/dev/*" ! -user root -printf '%u:%g\t%p\0' \
  | sort -z -u \
  | while IFS=$'\t' read -r -d '' owner path; do
      printf 'chown -- %q %q\n' "$owner" "$path"
    done > system_user.list
