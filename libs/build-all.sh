#!/usr/bin/env bash


cd "$(dirname "$0")"
. ./common.sh

./build-openssl-android.sh
./build-ssh2-android.sh


