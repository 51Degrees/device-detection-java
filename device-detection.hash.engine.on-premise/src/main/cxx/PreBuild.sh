#!/bin/bash
set -eu

# mvn clean will delete target so that's the place to be building
mkdir -p target/cxx-build
mkdir -p target/classes
cd target/cxx-build

echo "configure"
cmake ../../src/main/cxx

# put output in target/classes so that it will get jarred up
echo "cmake build"
cmake --build . --target fiftyone-hash-java

# Debug: show what was built
echo "=== Build complete ==="
ls -la ../classes/*.dylib ../classes/*.so 2>/dev/null || echo "No native libraries found"
for lib in ../classes/*.dylib; do
    if [[ -f "$lib" ]]; then
        echo "Symbol count in $lib:"
        nm -g "$lib" 2>/dev/null | wc -l
    fi
done
