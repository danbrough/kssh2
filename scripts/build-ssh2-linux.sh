#!/bin/bash

cd "$(dirname "$0")"
. ./ssh2.sh

# Locate Konan dependencies
KONAN_HOME="${KONAN_DATA_DIR:-$HOME/.konan}"
# Find the most recent LLVM dependency directory
LLVM_DIR=$(find "$KONAN_HOME/dependencies" -maxdepth 1 -name "llvm-*" -type d | sort -V | tail -n 1)

if [ -z "$LLVM_DIR" ]; then
    echo "Error: Could not find LLVM dependencies in $KONAN_HOME/dependencies."
    echo "Please run 'konanc -target linux_x64' once to download them."
    exit 1
fi

echo "Using Konan LLVM: $LLVM_DIR"
export PATH="$LLVM_DIR/bin:$PATH"

# Verify Clang is available
if ! command -v clang &> /dev/null; then
    echo "Error: clang not found in Konan LLVM bin."
    exit 1
fi

# Common CMake options
CMAKE_OPTS="-DBUILD_SHARED_LIBS=OFF -DBUILD_STATIC_LIBS=ON -DCRYPTO_BACKEND=OpenSSL -DBUILD_EXAMPLES=OFF -DBUILD_TESTING=OFF"

# --- Build for Linux x86_64 (x64) ---
echo "=== Building for Linux x86_64 ==="


OPENSSL_LIB_ROOT="$LIBDIR/openssl/linux/x64"
SSH2_LIB_ROOT="$LIBDIR/ssh2/linux/x64"
BUILD_ROOT="$BUILDDIR/ssh2/linux/x64"
rm -rf "$BUILD_ROOT" "$SSH2_LIB_ROOT" 2> /dev/null
mkdir -p "$BUILD_ROOT" && cd "$BUILD_ROOT"

TOOLCHAIN=$HOME/.konan/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2
SYSROOT=$TOOLCHAIN/x86_64-unknown-linux-gnu/sysroot
CC=$TOOLCHAIN/bin/x86_64-unknown-linux-gnu-gcc
AR=$TOOLCHAIN/bin/x86_64-unknown-linux-gnu-ar
RANLIB=$TOOLCHAIN/bin/x86_64-unknown-linux-gnu-ranlib
STRIP=$TOOLCHAIN/bin/x86_64-unknown-linux-gnu-strip

cmake "$SSH2_SRC_DIR" \
    -DCMAKE_SYSTEM_NAME=Linux \
    -DCMAKE_SYSTEM_PROCESSOR=x86_64 \
    -DCMAKE_C_COMPILER="$CC" \
    -DCMAKE_AR="$AR" \
    -DCMAKE_RANLIB="$RANLIB" \
    -DCMAKE_STRIP="$STRIP" \
    -DCMAKE_SYSROOT="$SYSROOT" \
    -DCMAKE_INSTALL_DOCDIR="$BUILD_ROOT" \
    -DCMAKE_INSTALL_MANDIR="$BUILD_ROOT" \
    -DCMAKE_POSITION_INDEPENDENT_CODE=ON \
    -DCMAKE_FIND_ROOT_PATH="$SYSROOT;$OPENSSL_LIB_ROOT" \
    -DCMAKE_FIND_ROOT_PATH_MODE_PROGRAM=NEVER \
    -DCMAKE_FIND_ROOT_PATH_MODE_LIBRARY=ONLY \
    -DCMAKE_FIND_ROOT_PATH_MODE_INCLUDE=ONLY \
    -DCMAKE_FIND_ROOT_PATH_MODE_PACKAGE=ONLY \
    -DOPENSSL_INCLUDE_DIR="$OPENSSL_LIB_ROOT/include" \
    -DOPENSSL_SSL_LIBRARY="$OPENSSL_LIB_ROOT/lib/libssl.a" \
    -DOPENSSL_CRYPTO_LIBRARY="$OPENSSL_LIB_ROOT/lib/libcrypto.a" \
    -DOPENSSL_USE_STATIC_LIBS=TRUE \
    -DCMAKE_INSTALL_PREFIX="$SSH2_LIB_ROOT" \
    $CMAKE_OPTS

#cmake --build . --target install
cmake --build . --target install --parallel 8

# --- Build for Linux aarch64 (ARM64) ---
echo "=== Building for Linux aarch64 ==="

OPENSSL_LIB_ROOT="$LIBDIR/openssl/linux/arm64"
SSH2_LIB_ROOT="$LIBDIR/ssh2/linux/arm64"
BUILD_ROOT="$BUILDDIR/ssh2/linux/arm64"
rm -rf "$BUILD_ROOT" "$SSH2_LIB_ROOT" 2> /dev/null
mkdir -p "$BUILD_ROOT" && cd "$BUILD_ROOT"

# For ARM64 cross-compilation, we need the proper sysroot.
# Konan provides a sysroot for ARM64.

# Note: Konan's sysroot structure varies. A more reliable method is using the sysroot from the Linux target in Konan.
# We'll attempt to locate the aarch64 sysroot if available, otherwise fall back to host sysroot with target flag.
# Konan typically includes a minimal sysroot for cross-compilation.
TOOLCHAIN=/home/dan/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2
SYSROOT=$TOOLCHAIN/aarch64-unknown-linux-gnu/sysroot
CC=$TOOLCHAIN/bin/aarch64-unknown-linux-gnu-gcc
AR=$TOOLCHAIN/bin/aarch64-unknown-linux-gnu-ar
RANLIB=$TOOLCHAIN/bin/aarch64-unknown-linux-gnu-ranlib
STRIP=$TOOLCHAIN/bin/aarch64-unknown-linux-gnu-strip
OPENSSL_LIB_ROOT=/files/workspace/kssh2/lib/openssl/linux/arm64


cmake "$SSH2_SRC_DIR" \
    -DCMAKE_SYSTEM_NAME=Linux \
    -DCMAKE_SYSTEM_PROCESSOR=aarch64 \
    -DCMAKE_C_COMPILER="$CC" \
    -DCMAKE_AR="$AR" \
    -DCMAKE_RANLIB="$RANLIB" \
    -DCMAKE_STRIP="$STRIP" \
    -DCMAKE_SYSROOT="$SYSROOT" \
    -DCMAKE_INSTALL_DOCDIR="$BUILD_ROOT" \
    -DCMAKE_INSTALL_MANDIR="$BUILD_ROOT" \
    -DCMAKE_POSITION_INDEPENDENT_CODE=ON \
    -DCMAKE_FIND_ROOT_PATH="$SYSROOT;$OPENSSL_LIB_ROOT" \
    -DCMAKE_FIND_ROOT_PATH_MODE_PROGRAM=NEVER \
    -DCMAKE_FIND_ROOT_PATH_MODE_LIBRARY=ONLY \
    -DCMAKE_FIND_ROOT_PATH_MODE_INCLUDE=ONLY \
    -DCMAKE_FIND_ROOT_PATH_MODE_PACKAGE=ONLY \
    -DOPENSSL_INCLUDE_DIR="$OPENSSL_LIB_ROOT/include" \
    -DOPENSSL_SSL_LIBRARY="$OPENSSL_LIB_ROOT/lib/libssl.a" \
    -DOPENSSL_CRYPTO_LIBRARY="$OPENSSL_LIB_ROOT/lib/libcrypto.a" \
    -DOPENSSL_USE_STATIC_LIBS=TRUE \
    -DCMAKE_INSTALL_PREFIX="$SSH2_LIB_ROOT" \
    $CMAKE_OPTS


cmake --build . --target install --parallel 8

echo "=== Build complete ==="
