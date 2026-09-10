#!/bin/bash

[ -z  "$KLOG_COLOR" ] && KLOG_COLOR=1
[ -z "$KLOG_LEVEL" ] && KLOG_LEVEL=TRACE

export KLOG_COLOR KLOG_LEVEL
DIR="$(pwd)"
cd `dirname "$0"`
MODULE="${PWD##*/}"

cd ..
ROOTDIR="$(pwd)"

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

LIB_PATH="$(realpath $MODULE/build/bin/$TARGET/thangDebugShared/)"

VERSION="$(cat gradle.properties  | grep version | sed  -e 's|project.version=||g')"
exec java --enable-native-access=ALL-UNNAMED -Djava.library.path=$LIB_PATH -jar $MODULE/build/libs/$MODULE-$VERSION-all.jar


