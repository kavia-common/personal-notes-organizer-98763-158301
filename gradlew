#!/usr/bin/env bash
# Proxy gradle wrapper to run Android project wrapper inside android_frontend.
set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}/android_frontend"
# If running under bash, we can exec the inner wrapper; otherwise, just invoke.
exec ./gradlew "$@"
