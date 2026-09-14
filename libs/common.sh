# global configuration for all scripts




export SCRIPT_NAME="$(echo $0 | sed -e 's|^\./||g' -e 's|\.sh$||g')"

# add the default configuration from source control
[ -f "$SCRIPT_NAME-env.sh" ] && . "./$SCRIPT_NAME-env.sh"

# add any local overrides
[ -f "$SCRIPT_NAME-env-local.sh" ] && . "./$SCRIPT_NAME-env-local.sh"


# make sure the build dir exists
[ ! -d ./build ] && mkdir -p ./build
cd ./build


set -eux



