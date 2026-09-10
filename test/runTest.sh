#!/bin/bash

[ ! -v KLOG_COLOR ] && KLOG_COLOR=1
[ ! -v KLOG_LEVEL ] && KLOG_LEVEL=TRACE

export KLOG_COLOR KLOG_LEVEL
DIR="$(pwd)"
cd `dirname "$0"` && cd ..
ROOTDIR="$(pwd)"
MODULE=test
#EXE=ssh2demo

if [ "$OSTYPE" == "linux-gnu" ]; then
  TARGET=linux
else
  TARGET=macos
fi

if [ "$HOSTTYPE" == "x86_64" ]; then
  TARGET=${TARGET}X64
else
  TARGET=${TARGET}Arm64
fi


./gradlew :$MODULE:linkThangDebugShared$TARGET :$MODULE:shadowJar

LIB_PATH="$(realpath test/build/bin/$TARGET/thangDebugShared/)"

exec java -Djava.library.path=$LIB_PATH -jar test/build/libs/test-0.0.2-all.jar




