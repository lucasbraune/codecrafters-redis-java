#!/bin/sh
set -e
tmpFile=$(mktemp -d)
javac -sourcepath src/main/java src/main/java/redis/clone/Main.java -d "$tmpFile"
mkdir -p build
jar cf build/java_redis.jar -C "$tmpFile"/ .
exec java -cp build/java_redis.jar redis.clone/Main "$@"
