#!/bin/bash

# Run this file after log into chroot
find / -xdev -type f ! -iname ".gitignore" ! -path "/proc/*" ! -path "/sys/*" ! -path "/dev/*" ! -group root -printf '%g\t%p\0' \
  | sort -z -u \
  | while IFS=$'\t' read -r -d '' grp path; do
      printf 'chgrp -- %q %q\n' "$grp" "$path"
    done > system_group.list
