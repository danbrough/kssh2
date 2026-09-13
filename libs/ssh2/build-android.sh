#!/usr/bin/env bash

cd "$(dirname "$0")"
source ../env-android.sh
[ -f ../env-android-local.sh ] && source ../env-android-local.sh

[ ! -d ../build ] && mkdir -p ../build
cd ../build

set -eux

LIBSSH2_FULL_VERSION="libssh2-1.11.1"
ANDROID_LIB_ROOT=$(realpath ..)/lib/ssh2/android
OPENSSL_LIB_ROOT="$(realpath ..)/lib/openssl/android"

rm -rf "${ANDROID_LIB_ROOT}"


if [ ! -f "$LIBSSH2_FULL_VERSION.tar.gz" ]; then
    wget  https://www.libssh2.org/download/$LIBSSH2_FULL_VERSION.tar.gz
fi
[ -d $LIBSSH2_FULL_VERSION ] && rm -rf $LIBSSH2_FULL_VERSION
tar -xvzf $LIBSSH2_FULL_VERSION.tar.gz




cd $LIBSSH2_FULL_VERSION
LIBSSH2_FULL_PATH=$(pwd)

if [ ! "${ANDROID_NDK_HOME}" ]; then
    echo "ANDROID_NDK_HOME environment variable not set, set and rerun"
    exit 1
fi



#for ANDROID_TARGET_PLATFORM in armeabi-v7a arm64-v8a x86 x86_64; do
for ANDROID_TARGET_PLATFORM in arm64-v8a x86_64; do

    echo
    echo "------------- Building libssh2 for ${ANDROID_TARGET_PLATFORM} ---------------"
    export ANDROID_TARGET_PLATFORM
    mkdir -p "${ANDROID_LIB_ROOT}/${ANDROID_TARGET_PLATFORM}"

    #export OPENSSL_ROOT_DIR=/root/libs/openssl-lib/${ANDROID_TARGET_PLATFORM}/
    OPENSSL_ROOT_DIR="${OPENSSL_LIB_ROOT}/$ANDROID_TARGET_PLATFORM"

    echo "ANDROID_TARGET_PLATFORM: " ${ANDROID_LIB_ROOT}/${ANDROID_TARGET_PLATFORM}
    echo "OPENSSL_ROOT_DIR: $OPENSSL_ROOT_DIR"


    cd "$LIBSSH2_FULL_PATH"
    rm -rf "build-${ANDROID_TARGET_PLATFORM}"
    mkdir "build-${ANDROID_TARGET_PLATFORM}"
    cd "build-${ANDROID_TARGET_PLATFORM}"

    cmake ../ \
        -DOPENSSL_ROOT_DIR=${OPENSSL_ROOT_DIR} \
        -DOPENSSL_INCLUDE_DIR=${OPENSSL_ROOT_DIR}/include \
        -DOPENSSL_SSL_LIBRARY=${OPENSSL_ROOT_DIR}/lib/libssl.a \
        -DOPENSSL_CRYPTO_LIBRARY=${OPENSSL_ROOT_DIR}/lib/libcrypto.a \
        -DCMAKE_SYSTEM_NAME=Android \
        -DCMAKE_SYSTEM_VERSION=$ANDROID_API_VERSION \
        -DCMAKE_ANDROID_ARCH_ABI=$ANDROID_TARGET_PLATFORM \
        -DCMAKE_ANDROID_NDK=$ANDROID_NDK_HOME \
        -DCMAKE_LIBRARY_OUTPUT_DIRECTORY=${ANDROID_LIB_ROOT}/${ANDROID_TARGET_PLATFORM} \
        -DCMAKE_BUILD_TYPE=Release \
        -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK_HOME/build/cmake/android.toolchain.cmake \
        -DANDROID_PLATFORM=$ANDROID_API_VERSION \
        -DANDROID_ABI=$ANDROID_TARGET_PLATFORM \
        -DANDROID_NATIVE_API_LEVEL=$ANDROID_API_VERSION \
        -DCMAKE_INSTALL_PREFIX=${ANDROID_LIB_ROOT}/${ANDROID_TARGET_PLATFORM} \
        -DBUILD_EXAMPLES=OFF \
        -DBUILD_TESTING=OFF \
        -DBUILD_SHARED_LIBS=OFF

    if [ $? -ne 0 ]; then
        echo "Error executing cmake"
        exit 1
    fi

    cmake --build .

    if [ $? -ne 0 ]; then
        echo "Error building for platform:${ANDROID_TARGET_PLATFORM}"
        exit 1
    fi

    make install
done
