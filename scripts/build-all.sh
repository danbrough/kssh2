#!/usr/bin/env bash


cd "$(dirname "$0")"

./build-openssl-linux
./build-ssh2-linux
./build-openssl-android.sh
./build-ssh2-android.sh


