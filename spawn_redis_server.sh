#!/bin/sh
set -e
tmpFile=$(mktemp -d)
javac -sourcepath src/main/java src/main/java/codecrafters/redis/App.java -d "$tmpFile"
mkdir -p build
jar cf build/java_redis.jar -C "$tmpFile"/ .
exec java -cp build/java_redis.jar codecrafters.redis/App "$@"
