@ECHO OFF

SETLOCAL enabledelayedexpansion

ECHO %1
ECHO %2

mkdir build32
cd build32
cmake ../.. -A Win32
cmake --build . --target fiftyone-hash-java --config Release
cd ..
mkdir build64
cd build64
cmake ../.. -A x64
cmake --build . --target fiftyone-hash-java --config Release