#!/bin/bash

if [ ! -e binaries ]; then
    wget https://github.com/duckdb/duckdb/releases/download/v0.8.1/libduckdb-linux-amd64.zip
    unzip libduckdb-linux-amd64.zip -d binaries
    rm libduckdb-linux-amd64.zip
fi

export DUCKDB_HOME="$(pwd)/binaries"