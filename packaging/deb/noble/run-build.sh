#!/bin/bash

#!/bin/bash

LOCKFILE="/tmp/deb-build.lock"

exec 200>"$LOCKFILE"
if ! flock -n 200; then
    echo "Build already in progress"
    exit 1
fi

./deb-build.sh "$1"
