param (
    [Parameter(Mandatory)][string]$RepoName,
    [string]$MetaDataPath = "$PWD/common-metadata",
    [string]$DataType = "HashV41"
)
$ErrorActionPreference = "Stop"

./tools/ci/generate-accessors.ps1 -RepoName:$RepoName -MetaDataPath:$MetaDataPath -DataType:$DataType

Copy-Item "tools/Java/DeviceDataBase.java" "device-detection-java/device-detection.shared/src/main/java/fiftyone/devicedetection/shared/"
Copy-Item "tools/Java/DeviceData.java" "device-detection-java/device-detection.shared/src/main/java/fiftyone/devicedetection/shared/"
