
param(
    [string]$ProjectDir = ".",
    [string]$Name
)

./java/run-integration-tests.ps1 -RepoName "device-detection-java" -ProjectDir $ProjectDir -Name $Name

exit $LASTEXITCODE