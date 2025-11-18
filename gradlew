#!/usr/bin/env sh

set -e

BASEDIR="$(cd "$(dirname "$0")" && pwd)"
WRAPPER_DIR="$BASEDIR/gradle/wrapper"
WRAPPER_JAR="$WRAPPER_DIR/gradle-wrapper.jar"
WRAPPER_BIN="$WRAPPER_DIR/gradle-wrapper"

if [ -f "$WRAPPER_BIN" ] && [ -x "$WRAPPER_BIN" ]; then
  exec "$WRAPPER_BIN" "$@"
fi

if [ -f "$WRAPPER_JAR" ]; then
  exec java -Xmx64m -Xms64m -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
fi

if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

echo "Gradle wrapper executable not found. Please generate the wrapper (./gradlew wrapper) or install Gradle." >&2
exit 1
