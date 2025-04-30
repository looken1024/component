#!/usr/bin/env bash
set -x -e

mkdir -p build
cd build
cmake ..
make -j8
cd ..
