#!/usr/bin/env bash


. ./openssl.sh


OUTPUT_DIR="$LIBDIR/openssl/linux"
#rm -rf "${OUTPUT_DIR}" && mkdir -p "${OUTPUT_DIR}"


export CC="clang"
export CXX="clang++"


# ==============================================================================
# BUILD LOOP
# ==============================================================================
ORIGINAL_PATH=$PATH

do_build(){
  cd "${OPENSSL_SRC_DIR}"
  INSTALLDIR="${OUTPUT_DIR}/${ARCH_NAME}"
  rm -rf "$INSTALLDIR" 2> /dev/null && mkdir -p "$INSTALLDIR"
  OPENSSL_ARCH="$1"
  ARCH_NAME="$2"
  echo "----------------------------------------------------"
  echo "Building OpenSSL for Linux ${OPENSSL_ARCH} ..."
  echo "----------------------------------------------------"

  # Clean previous builds
  if [ -f Makefile ]; then
      make clean || true
  fi


  # Configure options:
  # 'no-shared' ensures we ONLY make .a static files
  # 'no-tests' saves massive build time
  # 'enable-pic' enforces -fPIC compilation
  #no-shared to disable shared libs
  ./Configure "${OPENSSL_ARCH}" \
      no-tests no-shared enable-pic \
      --prefix="${OUTPUT_DIR}/${ARCH_NAME}" \
      -Wno-macro-redefined

  # Build and install locally inside the prefix folder
  make -j"$(nproc 2>/dev/null || sysctl -n hw.ncpu 2>/dev/null || echo 4)"
  make install_sw # Installs software libraries/headers only (skips documentation)

  cd ..
  echo "===================================================="
  echo "SUCCESS! Static libraries built inside: ${OUTPUT_DIR}"
  echo "===================================================="
}


buildLinuxArm64(){
OPENSSL_ARCH="linux-aarch64"
ARCH_NAME="arm64"

# Define the base toolchain path
export KONAN_TC="$HOME/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2"

# Explicitly force Clang to use the cross-toolchain linker (-fuse-ld)
export CFLAGS="--target=aarch64-unknown-linux-gnu --gcc-toolchain=$KONAN_TC --sysroot=$KONAN_TC/aarch64-unknown-linux-gnu/sysroot -fuse-ld=$KONAN_TC/bin/aarch64-unknown-linux-gnu-ld"
export CXXFLAGS="--target=aarch64-unknown-linux-gnu --gcc-toolchain=$KONAN_TC --sysroot=$KONAN_TC/aarch64-unknown-linux-gnu/sysroot -fuse-ld=$KONAN_TC/bin/aarch64-unknown-linux-gnu-ld"

# Keep the cross-toolchain binary utilities from the previous step
export AR="$KONAN_TC/bin/aarch64-unknown-linux-gnu-ar"
export NM="$KONAN_TC/bin/aarch64-unknown-linux-gnu-nm"
export RANLIB="$KONAN_TC/bin/aarch64-unknown-linux-gnu-ranlib"

do_build $OPENSSL_ARCH $ARCH_NAME
}

buildLinuxX64(){
OPENSSL_ARCH="linux-x86_64"
ARCH_NAME="x64"

# Define the base toolchain path
export KONAN_TC="$HOME/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2"

# Explicitly force Clang to use the cross-toolchain linker (-fuse-ld)
export CFLAGS="--target=aarch64-unknown-linux-gnu --gcc-toolchain=$KONAN_TC --sysroot=$KONAN_TC/aarch64-unknown-linux-gnu/sysroot -fuse-ld=$KONAN_TC/bin/aarch64-unknown-linux-gnu-ld"
export CXXFLAGS="--target=aarch64-unknown-linux-gnu --gcc-toolchain=$KONAN_TC --sysroot=$KONAN_TC/aarch64-unknown-linux-gnu/sysroot -fuse-ld=$KONAN_TC/bin/aarch64-unknown-linux-gnu-ld"

# Keep the cross-toolchain binary utilities from the previous step
export AR="$KONAN_TC/bin/aarch64-unknown-linux-gnu-ar"
export NM="$KONAN_TC/bin/aarch64-unknown-linux-gnu-nm"
export RANLIB="$KONAN_TC/bin/aarch64-unknown-linux-gnu-ranlib"

do_build $OPENSSL_ARCH $ARCH_NAME
}



buildLinuxX64(){
OPENSSL_ARCH="linux-x86_64"
ARCH_NAME="x64"

echo "----------------------------------------------------"
echo "Building OpenSSL for Linux ${OPENSSL_ARCH} ..."
echo "----------------------------------------------------"

# Define the base x86_64 toolchain path (Adjust folder name to match your actual .konan dir)
export KONAN_TC="$HOME/.konan/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2"

# Direct Clang to target x86_64 Linux, using the specific sysroot and toolchain linker
export CFLAGS="--target=x86_64-unknown-linux-gnu --gcc-toolchain=$KONAN_TC --sysroot=$KONAN_TC/x86_64-unknown-linux-gnu/sysroot -fuse-ld=$KONAN_TC/bin/x86_64-unknown-linux-gnu-ld"
export CXXFLAGS="--target=x86_64-unknown-linux-gnu --gcc-toolchain=$KONAN_TC --sysroot=$KONAN_TC/x86_64-unknown-linux-gnu/sysroot -fuse-ld=$KONAN_TC/bin/x86_64-unknown-linux-gnu-ld"

# Use the matching binary utilities from the x86_64 Konan toolchain
export AR="$KONAN_TC/bin/x86_64-unknown-linux-gnu-ar"
export NM="$KONAN_TC/bin/x86_64-unknown-linux-gnu-nm"
export RANLIB="$KONAN_TC/bin/x86_64-unknown-linux-gnu-ranlib"
do_build $OPENSSL_ARCH $ARCH_NAME
}


buildLinuxArm64
buildLinuxX64

