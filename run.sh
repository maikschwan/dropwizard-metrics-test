#!/bin/bash -x

set -e
# The script that is executed within the docker container to start testmetrics
COMMAND="server"
if [ -n "$1" ]; then
    COMMAND="$1"
fi
# Start the actual application
exec java --add-opens java.base/java.lang=ALL-UNNAMED ${JAVA_OPTS} -jar testmetrics.jar ${COMMAND} config.yml
