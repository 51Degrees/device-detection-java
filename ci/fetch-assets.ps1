
param (
    [Parameter(Mandatory=$true)]
    [string]$RepoName,
    [string]$DeviceDetection = "",
    [string]$DeviceDetectionUrl = ""
)

$RepoPath = [IO.Path]::Combine($pwd, $RepoName)

if ($DeviceDetection -ne "") {
    ./steps/fetch-hash-assets.ps1 -RepoName $RepoName -LicenseKey $DeviceDetection -Url $DeviceDetectionUrl
}
else {
    Write-Warning "No device detection key provided. Not downloading Hash file"
}

Write-Output "Download Lite file"
curl -L -o "$RepoPath/51Degrees-LiteV4.1.hash" "https://github.com/51Degrees/device-detection-data/raw/main/51Degrees-LiteV4.1.hash"

Write-Output "Download Evidence file"
curl -o "$RepoPath/20000 Evidence Records.yml" "https://media.githubusercontent.com/media/51Degrees/device-detection-data/main/20000%20Evidence%20Records.yml"    

Write-Output "Download User Agents file"
curl -o "$RepoPath/20000 User Agents.csv" "https://media.githubusercontent.com/media/51Degrees/device-detection-data/main/20000%20User%20Agents.csv"


Copy-Item "$RepoPath/20000 Evidence Records.yml"  "$RepoPath/device-detection.hash.engine.on-premise/src/main/cxx/device-detection-cxx/device-detection-data/20000 Evidence Records.yml"

Copy-Item "$RepoPath/20000 User Agents.csv"  "$RepoPath/device-detection.hash.engine.on-premise/src/main/cxx/device-detection-cxx/device-detection-data/20000 User Agents.csv"

Copy-Item $RepoPath/51Degrees-LiteV4.1.hash  $RepoPath/device-detection.hash.engine.on-premise/src/main/cxx/device-detection-cxx/device-detection-data/51Degrees-LiteV4.1.hash

if ($DeviceDetection != $Null) {
    Copy-Item $RepoPath/TAC-HashV41.hash  $RepoPath/device-detection.hash.engine.on-premise/src/main/cxx/device-detection-cxx/device-detection-data/TAC-HashV41.hash
}

