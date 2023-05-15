
param(
    [string]$ProjectDir = ".",
    [string]$Name
)

./java/run-update-dependencies.ps1 -RepoName "device-detection-java" -ProjectDir $ProjectDir -Name $Name

exit $LASTEXITCODE

