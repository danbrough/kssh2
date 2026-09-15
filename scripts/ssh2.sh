
. ./common.sh

# SSH2 CONFIGURATION
LIBSSH2_FULL_VERSION="libssh2-1.11.1"

if [ ! -f "$LIBSSH2_FULL_VERSION.tar.gz" ]; then
    wget  https://www.libssh2.org/download/$LIBSSH2_FULL_VERSION.tar.gz
fi
[ -d $LIBSSH2_FULL_VERSION ] && rm -rf $LIBSSH2_FULL_VERSION
tar -xvzf $LIBSSH2_FULL_VERSION.tar.gz

SSH2_SRC_DIR="$(realpath $LIBSSH2_FULL_VERSION)"
