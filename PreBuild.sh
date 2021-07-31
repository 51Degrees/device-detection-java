#!/bin/bash

mkdir build
cd build
cmake ../..
cmake --build . --target fiftyone-hash-java