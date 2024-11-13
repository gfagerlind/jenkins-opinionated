#!/usr/bin/env bash
set -ue -o pipefail
JENKINS_HOME=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )/jenkins_home
GITROOT=$(git rev-parse --show-toplevel)

docker build . -t jo
docker run \
    -p 8080:8080 \
    -v "${GITROOT}":/git/jenkins-opinionated \
    -v "${JENKINS_HOME}":/var/jenkins_home \
    jo
