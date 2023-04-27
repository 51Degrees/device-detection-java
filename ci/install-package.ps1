
param(
    [string]$ProjectDir = ".",
    [string]$Name
)

./java/install-package.ps1 -RepoName "de-detection-java-test" -ProjectDir $ProjectDir -Name $Name

exit $LASTEXITCODE

