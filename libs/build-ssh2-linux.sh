#!/usr/bin/env bash

cd "$(dirname "$0")"
. ./ssh2.sh


OPENSSL_LIB_ROOT="$LIBDIR/openssl/linux"
INSTALL_PREFIX="$LIBDIR/ssh2/linux"
export CC="clang"
export CXX="clang++"
export PATH="/home/dan/.konan/kotlin-native-prebuilt-linux-x86_64-2.4.20/bin:/home/dan/.konan/dependencies/llvm-21-x86_64-linux-essentials-116/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"

do_build(){
  echo
  echo -------------      do_build INSTALL_DIR=$INSTALL_DIR TARGET_HOST=$TARGET_HOST

  rm -rf "$INSTALL_DIR" > /dev/null
  cd "$SSH2_SRC_DIR"
  if [ -f Makefile ]; then
        make clean || true
  fi
  ./configure --prefix="$INSTALL_DIR" --host=$TARGET_HOST \
    --with-crypto=openssl --with-libssl-prefix="$SSL_DIR"  --enable-static=yes
  make -j4
}

buildLinuxX64(){
  INSTALL_DIR="$INSTALL_PREFIX/x64"
  TARGET_HOST="x86_64-unknown-linux-gnu"
  SSL_DIR="$OPENSSL_LIB_ROOT/x64"
  # Define the base x86_64 toolchain path (Adjust folder name to match your actual .konan dir)
  KONAN_TC="$HOME/.konan/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2"

  # Direct Clang to target x86_64 Linux, using the specific sysroot and toolchain linker
  export CFLAGS="--target=x86_64-unknown-linux-gnu --gcc-toolchain=$KONAN_TC --sysroot=$KONAN_TC/x86_64-unknown-linux-gnu/sysroot -fuse-ld=$KONAN_TC/bin/x86_64-unknown-linux-gnu-ld"
  export CXXFLAGS="--target=x86_64-unknown-linux-gnu --gcc-toolchain=$KONAN_TC --sysroot=$KONAN_TC/x86_64-unknown-linux-gnu/sysroot -fuse-ld=$KONAN_TC/bin/x86_64-unknown-linux-gnu-ld"

  # Use the matching binary utilities from the x86_64 Konan toolchain
  export AR="$KONAN_TC/bin/x86_64-unknown-linux-gnu-ar"
  export NM="$KONAN_TC/bin/x86_64-unknown-linux-gnu-nm"
  export RANLIB="$KONAN_TC/bin/x86_64-unknown-linux-gnu-ranlib"
  export PATH="/home/dan/.konan/dependencies/llvm-21-x86_64-linux-essentials-116/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"

  do_build
}

buildLinuxX64