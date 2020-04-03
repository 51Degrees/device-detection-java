#!/bin/bash

if [ "$1" = "hash" ]; then
    API=Hash
fi
if [ "$2" = "hash" ]; then
    API=Hash
fi

SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"

if [ "$API" = "Hash" ]; then
  SRCOUT=$SCRIPTPATH/device-detection.hash.engine.on-premise/src/main/java/fiftyone/devicedetection/hash/engine/onpremise
  RES=$SCRIPTPATH/device-detection.hash.engine.on-premise/src/main/resources
fi

SRCMAIN=$SCRIPTPATH/device-detection-cxx/src
SRCHASH=$SRCMAIN/hash
SRCCM=$SRCMAIN/common-cxx
TH="-D FIFTYONEDEGREES_NO_THREADING"
GCCARGS="-c -std=c11 -fPIC -pthread -O3"
GXXARGS="-c -std=c++11 -fPIC -pthread -O3"
LDARGS="-shared -O3 -pthread -std=c++11"
unameOut="$(uname -s)"
case "${unameOut}" in
    Linux*)
    OS=linux
    LDARGS="$LDARGS -static-libgcc -static-libstdc++"
    ;;
    Darwin*)
    OS=mac
    ;;
#    CYGWIN*)    OS=Cygwin;;
#    MINGW*)     OS=MinGw;;
    *)          OS="UNKNOWN:${unameOut}"
esac

if [ "$OS" = "UNKNOWN:${unameOut}" ]; then
  { echo >&2 "Operating system is UNKNOWN:${unameOut}. Aborting."; exit 1; }
fi

if command -v swig >/dev/null 2>&1; then
    if [ "$API" = "Hash" ]; then
        swig -c++ -java -package fiftyone.devicedetection.hash.engine.onpremise.interop.swig -outdir $SRCOUT/interop/swig -o $SCRIPTPATH/Java_Hash_Engine.cpp $SCRIPTPATH/device-detection.hash.engine.on-premise/hash_java.i
    fi
else
    { echo >&2 "Swig is required to generate wrapper but it's not installed."; }
fi

if [[ -z "${JAVA_HOME}" ]]; then
  { echo >&2 "JAVA_HOME is undefined. Aborting."; exit 1; }
fi

rm -r obj/
mkdir obj

if [ "$API" = "Hash" ]; then

    echo "Building Hash Device Detection library."
    for ARCH in "x86" "x64"
    do
        echo $ARCH
        if [ "$ARCH" = "x86" ]
        then
            M=-m32
        else
            M=-m64
        fi

        # SWIG
        g++ $M $GXXARGS $SCRIPTPATH/Java_Hash_Engine.cpp -o obj/Java_Hash_Engine.o -I $JAVA_HOME/include -I $JAVA_HOME/include/linux -I $JAVA_HOME/include/darwin

        # Hash

        g++ $M $GXXARGS $SRCHASH/ComponentMetaDataBuilderHash.cpp -o obj/ComponentMetaDataBuilderHash.o
        g++ $M $GXXARGS $SRCHASH/ComponentMetaDataCollectionHash.cpp -o obj/ComponentMetaDataCollectionHash.o
        g++ $M $GXXARGS $SRCHASH/ConfigHash.cpp -o obj/ConfigHash.o
        g++ $M $GXXARGS $SRCHASH/EngineHash.cpp -o obj/EngineHash.o
        g++ $M $GXXARGS $SRCHASH/MetaDataHash.cpp -o obj/MetaDataHash.o
        g++ $M $GXXARGS $SRCHASH/ProfileMetaDataBuilderHash.cpp -o obj/ProfileMetaDataBuilderHash.o
        g++ $M $GXXARGS $SRCHASH/ProfileMetaDataCollectionHash.cpp -o obj/ProfileMetaDataCollectionHash.o
        g++ $M $GXXARGS $SRCHASH/PropertyMetaDataBuilderHash.cpp -o obj/PropertyMetaDataBuilderHash.o
        g++ $M $GXXARGS $SRCHASH/PropertyMetaDataCollectionForComponentHash.cpp -o obj/PropertyMetaDataForComponentHash.o
        g++ $M $GXXARGS $SRCHASH/PropertyMetaDataCollectionHash.cpp -o obj/PropertyMetaDataCollectionHash.o
        g++ $M $GXXARGS $SRCHASH/ResultsHash.cpp -o obj/ResultsHash.o
        g++ $M $GXXARGS $SRCHASH/ValueMetaDataBuilderHash.cpp -o obj/ValueMetaDataBuilderHash.o
        g++ $M $GXXARGS $SRCHASH/ValueMetaDataCollectionBaseHash.cpp -o obj/ValueMetaDataCollectionBaseHash.o
        g++ $M $GXXARGS $SRCHASH/ValueMetaDataCollectionForProfileHash.cpp -o obj/ValueMetaDataCollectionForProfileHash.o
        g++ $M $GXXARGS $SRCHASH/ValueMetaDataCollectionForPropertyHash.cpp -o obj/ValueMetaDataCollectionForPropertyHash.o
        g++ $M $GXXARGS $SRCHASH/ValueMetaDataCollectionHash.cpp -o obj/ValueMetaDataCollectionHash.o
        gcc $M $GCCARGS $SRCHASH/graph.c -o obj/graph.o
        gcc $M $GCCARGS $SRCHASH/hash.c -o obj/hash.o

        # Device detection
        g++ $M $GXXARGS $SRCMAIN/ConfigDeviceDetection.cpp -o obj/ConfigDeviceDetection.o
        g++ $M $GXXARGS $SRCMAIN/EngineDeviceDetection.cpp -o obj/EngineDeviceDetection.o
        g++ $M $GXXARGS $SRCMAIN/EvidenceDeviceDetection.cpp -o obj/EvidenceDeviceDetection.o
        g++ $M $GXXARGS $SRCMAIN/ResultsDeviceDetection.cpp -o obj/ResultsDeviceDetection.o
        gcc $M $GCCARGS $SRCMAIN/dataset-dd.c -o obj/dataset-dd.o
        gcc $M $GCCARGS $SRCMAIN/results-dd.c -o obj/results-dd.o

        # Common
        g++ $M $GXXARGS $SRCCM/CollectionConfig.cpp -o obj/CollectionConfig.o
        g++ $M $GXXARGS $SRCCM/ComponentMetaData.cpp -o obj/ComponentMetaData.o
        g++ $M $GXXARGS $SRCCM/ConfigBase.cpp -o obj/ConfigBase.o
        g++ $M $GXXARGS $SRCCM/Date.cpp -o obj/Date.o
        g++ $M $GXXARGS $SRCCM/EngineBase.cpp -o obj/EngineBase.o
        g++ $M $GXXARGS $SRCCM/EvidenceBase.cpp -o obj/EvidenceBase.o
        g++ $M $GXXARGS $SRCCM/Exceptions.cpp -o obj/Exceptions.o
        g++ $M $GXXARGS $SRCCM/MetaData.cpp -o obj/MetaData.o
        g++ $M $GXXARGS $SRCCM/ProfileMetaData.cpp -o obj/ProfileMetaData.o
        g++ $M $GXXARGS $SRCCM/PropertyMetaData.cpp -o obj/PropertyMetaData.o
        g++ $M $GXXARGS $SRCCM/RequiredPropertiesConfig.cpp -o obj/RequiredPropertiesConfig.o
        g++ $M $GXXARGS $SRCCM/ResultsBase.cpp -o obj/ResultsBase.o
        g++ $M $GXXARGS $SRCCM/ValueMetaData.cpp -o obj/ValueMetaData.o
        gcc $M $GCCARGS $SRCCM/cache.c -o obj/cache.o
        gcc $M $GCCARGS $SRCCM/collection.c -o obj/collection.o
        gcc $M $GCCARGS $SRCCM/component.c -o obj/component.o
        gcc $M $GCCARGS $SRCCM/data.c -o obj/data.o
        gcc $M $GCCARGS $SRCCM/dataset.c -o obj/dataset.o
        gcc $M $GCCARGS $SRCCM/evidence.c -o obj/evidence.o
        gcc $M $GCCARGS $SRCCM/exceptionsc.c -o obj/exceptionsc.o
        gcc $M $GCCARGS $SRCCM/file.c -o obj/file.o
        gcc $M $GCCARGS $SRCCM/headers.c -o obj/headers.o
        gcc $M $GCCARGS $SRCCM/list.c -o obj/list.o
        gcc $M $GCCARGS $SRCCM/memory.c -o obj/memory.o
        gcc $M $GCCARGS $SRCCM/overrides.c -o obj/overrides.o
        gcc $M $GCCARGS $SRCCM/pool.c -o obj/pool.o
        gcc $M $GCCARGS $SRCCM/profile.c -o obj/profile.o
        gcc $M $GCCARGS $SRCCM/properties.c -o obj/properties.o
        gcc $M $GCCARGS $SRCCM/property.c -o obj/property.o
        gcc $M $GCCARGS $SRCCM/resource.c -o obj/resource.o
        gcc $M $GCCARGS $SRCCM/results.c -o obj/results.o
        gcc $M $GCCARGS $SRCCM/status.c -o obj/status.o
        gcc $M $GCCARGS $SRCCM/string.c -o obj/string.o
        gcc $M $GCCARGS $SRCCM/threading.c -o obj/threading.o
        gcc $M $GCCARGS $SRCCM/tree.c -o obj/tree.o
        gcc $M $GCCARGS $SRCCM/value.c -o obj/value.o

        mkdir -p $RES
        # Shared Library
        g++ $M $LDARGS obj/*.o -o $RES/DeviceDetectionHashEngine-$OS-$ARCH.so
    done
fi