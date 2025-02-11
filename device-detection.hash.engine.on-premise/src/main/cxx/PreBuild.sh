#!/bin/bash
set -eu

# A client uses Amazon Linux 2 based container with glibc 2.26. Until they
# update we use Zig as a C/C++ cross-compiler to target that glibc version.
if [[ $OSTYPE == linux* ]] && command -v zig >/dev/null; then
	echo "using zig cc to target GLIBC 2.26"
	export CC="zig cc -target native-linux-gnu.2.26 -s" CXX="zig c++ -target native-linux-gnu.2.26 -s"
	# Help CMake find libatomic on ARM, even though we shouldn't need it with Zig:
	case $(uname -m) in aarch64) export CMAKE_LIBRARY_PATH=/lib/aarch64-linux-gnu; esac
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
