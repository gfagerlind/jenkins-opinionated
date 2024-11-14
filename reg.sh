#!/usr/bin/bash
set -ue -o pipefail
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
make -s -j -C ${SCRIPT_DIR} -f reg.mak $@
