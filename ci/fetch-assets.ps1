
param (
    [Parameter(Mandatory=$true)]
    [string]$RepoName,
    [Parameter(Mandatory=$true)]
    [string]$DeviceDetection
)

$RepoPath = [IO.Path]::Combine($pwd, $RepoName)

./steps/fetch-hash-assets.ps1 -RepoName $RepoName -LicenseKey $DeviceDetection

Copy-Item $RepoPath/TAC-HashV41.hash  $RepoPath/device-detection.hash.engine.on-premise/src/main/cxx/device-detection-cxx/device-detection-data/TAC-HashV41.hash

