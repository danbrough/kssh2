# global configuration for all scripts

export CMAKE_BUILD_PARALLEL_LEVEL=4


#  ANDROID CONFIGURATION
export ANDROID_NDK_HOME=/files/sdk/android/ndk/29.0.14206865
export ANDROID_NDK_ROOT=$ANDROID_NDK_HOME
export ANDROID_NATIVE_API_LEVEL=27
export ANDROID_API_VERSION=$ANDROID_NATIVE_API_LEVEL
export MIN_SDK_VERSION="$ANDROID_API_VERSION"


LIBDIR="$(realpath ../lib)"
BUILDDIR="$(realpath build)"

# make sure the build dir exists
[ ! -d "$BUILDDIR" ] && mkdir -p "$BUILDDIR"
cd "$BUILDDIR"

#set -eux
set -eu



