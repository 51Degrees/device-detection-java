#!/bin/bash
set -eu

# A client uses Amazon Linux 2 based container with glibc 2.26. Until they
# update we use Zig as a C/C++ cross-compiler to target that glibc version. Only
# for ARM, as that's the platform the client is currently using.
if [[ $OSTYPE == linux* && $(uname -m) == aarch64 ]] && command -v zig >/dev/null; then
	echo "using zig cc to target GLIBC 2.26"
	export CC="zig cc -target aarch64-linux-gnu.2.26 -s" CXX="zig c++ -target aarch64-linux-gnu.2.26 -s"
	export CMAKE_LIBRARY_PATH=/lib/aarch64-linux-gnu
fi

# mvn clean will delete target so that's the place to be building
mkdir -p target/cxx-build
mkdir target/classes
cd target/cxx-build
# shellcheck disable=SC1073
echo "configure"
cmake ../../src/main/cxx
# put output in target/classes so that it will get jarred up
echo "cmake build"
cmake --build . --target fiftyone-hash-java

if [[ $OSTYPE == darwin* ]]; then
	echo "additional ARM64 build"
	echo "configure"
	cmake ../../src/main/cxx -DARCH=aarch64 -DCMAKE_OSX_ARCHITECTURES=arm64 -DBUILD_TESTING=OFF
	echo "cmake build"
	cmake --build . --target fiftyone-hash-java
fi
