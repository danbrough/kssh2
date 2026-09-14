
# Download and extract OpenSSL source if it doesn't exist
export OPENSSL_SRC_DIR="openssl-${OPENSSL_VERSION}"
if [ ! -d "${OPENSSL_SRC_DIR}" ]; then
    TAR_FILE="openssl-${OPENSSL_VERSION}.tar.gz"
    if [ ! -f "${TAR_FILE}" ]; then
        echo "Downloading OpenSSL v${OPENSSL_VERSION}..."
        curl -LO "https://www.openssl.org/source/${TAR_FILE}"
    fi
    tar -xzf "${TAR_FILE}"
fi

