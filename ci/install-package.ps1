
param(
    [string]$ProjectDir = ".",
    [string]$Name
)

./java/install-package.ps1 -RepoName "device-detection-java" -ProjectDir $ProjectDir -Name $Name

exit $LASTEXITCODE

