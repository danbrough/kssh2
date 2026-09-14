#!/usr/bin/env bash


cd "$(dirname "$0")"
. ./common.sh



# ==============================================================================
# CONFIGURATION
# ==============================================================================


# Ensure ANDROID_NDK_HOME is set
if [ -z "${ANDROID_NDK_HOME}" ]; then
    echo "ERROR: Please set your ANDROID_NDK_HOME environment variable."
    exit 1
fi

# Detect Host OS
HOST_OS=$(uname -s | tr '[:upper:]' '[:lower:]')
case "${HOST_OS}" in
    linux*)  HOST_TAG="linux-x86_64" ;;
    darwin*) HOST_TAG="darwin-x86_64" ;;  #yes this should work on apple silicon
    *)       echo "ERROR: Unsupported host OS: ${HOST_OS}"; exit 1 ;;
esac

TOOLCHAIN_BIN="${ANDROID_NDK_HOME}/toolchains/llvm/prebuilt/${HOST_TAG}/bin"


# Download and extract OpenSSL source if it doesn't exist
SRC_DIR="openssl-${OPENSSL_VERSION}"
if [ ! -d "${SRC_DIR}" ]; then
    TAR_FILE="openssl-${OPENSSL_VERSION}.tar.gz"
    if [ ! -f "${TAR_FILE}" ]; then
        echo "Downloading OpenSSL v${OPENSSL_VERSION}..."
        curl -LO "https://www.openssl.org/source/${TAR_FILE}"
    fi
    tar -xzf "${TAR_FILE}"
fi

OUTPUT_DIR="$(realpath ..)/lib/openssl/android"
rm -rf "${OUTPUT_DIR}" && mkdir -p "${OUTPUT_DIR}"

# List of targets: OpenSSL_Architecture_Name | NDK_Architecture_Name
# Format: "OPENSSL_TARGET ARCH_NAME"
TARGETS=(
    "android-arm64 arm64-v8a"
    "android-x86_64 x86_64"
    #"android-arm armeabi-v7a"
    #"android-x86 x86"
)

# ==============================================================================
# BUILD LOOP
# ==============================================================================
ORIGINAL_PATH=$PATH

for TARGET in "${TARGETS[@]}"; do
    read -r OPENSSL_ARCH ANDROID_ABI <<< "${TARGET}"

    echo "----------------------------------------------------"
    echo "Building OpenSSL for ${ANDROID_ABI} (${OPENSSL_ARCH})..."
    echo "----------------------------------------------------"

    cd "${SRC_DIR}"

    # Clean previous builds
    if [ -f Makefile ]; then
        make clean || true
    fi

    # Configure toolchain variables expected by OpenSSL's build system
    export PATH="${TOOLCHAIN_BIN}:${ORIGINAL_PATH}"

    # Configure options:
    # 'no-shared' ensures we ONLY make .a static files
    # 'no-tests' saves massive build time
    # 'enable-pic' enforces -fPIC compilation
    #no-shared to disable shared libs
    ./Configure "${OPENSSL_ARCH}" \
        -D__ANDROID_API__="${MIN_SDK_VERSION}" \
        no-tests no-shared \
        enable-pic \
        --prefix="${OUTPUT_DIR}/${ANDROID_ABI}" \
        -Wno-macro-redefined    -mno-outline-atomics

    # Build and install locally inside the prefix folder
    make -j"$(nproc 2>/dev/null || sysctl -n hw.ncpu 2>/dev/null || echo 4)"
    make install_sw # Installs software libraries/headers only (skips documentation)

    cd ..
done

echo "===================================================="
echo "SUCCESS! Static libraries built inside: ${OUTPUT_DIR}"
echo "===================================================="

