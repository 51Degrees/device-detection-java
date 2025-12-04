param (
    [string]$DeviceDetection,
    [string]$DeviceDetectionUrl
)
$ErrorActionPreference = "Stop"

$deviceDetectionData = "$PSScriptRoot/../device-detection.hash.engine.on-premise/src/main/cxx/device-detection-cxx/device-detection-data"

$assets = "TAC-HashV41.hash", "51Degrees-LiteV4.1.hash", "20000 Evidence Records.yml", "20000 User Agents.csv"
./steps/fetch-assets.ps1 -DeviceDetection:$DeviceDetection -DeviceDetectionUrl:$DeviceDetectionUrl -Assets $assets
foreach ($asset in $assets) {
    New-Item -ItemType SymbolicLink -Force -Target "$PWD/assets/$asset" -Path "$PSScriptRoot/../$asset", "$deviceDetectionData/$asset"
}
