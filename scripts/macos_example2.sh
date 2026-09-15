#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

# --- CONFIGURATION ---
OPENSSL_VERSION="3.3.4" # You can change this to your preferred version
WORK_DIR="$(pwd)/openssl-build"
SOURCE_DIR="${WORK_DIR}/openssl-${OPENSSL_VERSION}"
TARBALL="openssl-${OPENSSL_VERSION}.tar.gz"

# Build target paths
PREFIX_X86="${WORK_DIR}/build-x86_64"
PREFIX_ARM="${WORK_DIR}/build-arm64"
PREFIX_UNIVERSAL="${WORK_DIR}/universal"

# Setup clean working directories
echo "Creating working directories..."
mkdir -p "${WORK_DIR}"
cd "${WORK_DIR}"

# --- DOWNLOAD SOURCE ---
if [ ! -f "${TARBALL}" ]; then
    echo "Downloading OpenSSL v${OPENSSL_VERSION}..."
    curl -LO "https://github.com{OPENSSL_VERSION}/${TARBALL}"
fi

function extract_source() {
    echo "Extracting source..."
    rm -rf "${SOURCE_DIR}"
    tar -xzf "${TARBALL}"
}

# --- BUILD INTEL (x86_64) ---
echo "=========================================="
echo "Building for macOS Intel (x86_64)..."
echo "=========================================="
extract_source
cd "${SOURCE_DIR}"

./Configure darwin64-x86_64-cc \
    --prefix="${PREFIX_X86}" \
    no-shared \
    no-tests

make -j$(sysctl -n hw.ncpu)
make install_dev  # install_dev skips building documentation to save time

# --- BUILD APPLE SILICON (arm64) ---
echo "=========================================="
echo "Building for macOS Apple Silicon (arm64)..."
echo "=========================================="
extract_source
cd "${SOURCE_DIR}"

# Crucial: Specify the arm64 architecture flag to the compiler for cross-compiling
./Configure darwin64-arm64-cc \
    --prefix="${PREFIX_ARM}" \
    no-shared \
    no-tests

make -j$(sysctl -n hw.ncpu)
make install_dev

# --- CREATE UNIVERSAL BINARIES ---
echo "=========================================="
echo "Merging architectures into Universal Binaries..."
echo "=========================================="

mkdir -p "${PREFIX_UNIVERSAL}/lib"

# Combine static libraries using lipo
lipo -create "${PREFIX_X86}/lib/libcrypto.a" "${PREFIX_ARM}/lib/libcrypto.a" \
     -output "${PREFIX_UNIVERSAL}/lib/libcrypto.a"

lipo -create "${PREFIX_X86}/lib/libssl.a" "${PREFIX_ARM}/lib/libssl.a" \
     -output "${PREFIX_UNIVERSAL}/lib/libssl.a"

# Copy headers (headers are identical for both architectures in OpenSSL 3.x)
cp -R "${PREFIX_X86}/include" "${PREFIX_UNIVERSAL}/include"

# --- VERIFICATION ---
echo "=========================================="
echo "Verification:"
echo "=========================================="
file "${PREFIX_UNIVERSAL}/lib/libcrypto.a"
file "${PREFIX_UNIVERSAL}/lib/libssl.a"

echo "Success! Universal libraries are available at: ${PREFIX_UNIVERSAL}"
