#!/usr/bin/env bash
set -ue -o pipefail
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
JENKINS_HOME="${SCRIPT_DIR}/jenkins_home"
GITROOT=$(git rev-parse --show-toplevel)
PORT=8123

docker build -t jo ${SCRIPT_DIR}
docker run \
    --rm \
    -p ${PORT}:8080 \
    -v "${GITROOT}":/git/jenkins-opinionated \
    -v "${JENKINS_HOME}":/var/jenkins_home \
    jo
