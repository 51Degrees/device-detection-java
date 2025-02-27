param (
    [Parameter(Mandatory)][string]$RepoName,
    [string]$DataFile = "$RepoName/TAC-HashV41.hash"
)
$ErrorActionPreference = "Stop"

./tools/ci/generate-accessors.ps1 @PSBoundParameters

Copy-Item "tools/Java/DeviceDataBase.java" "device-detection-java/device-detection.shared/src/main/java/fiftyone/devicedetection/shared/"
Copy-Item "tools/Java/DeviceData.java" "device-detection-java/device-detection.shared/src/main/java/fiftyone/devicedetection/shared/"
