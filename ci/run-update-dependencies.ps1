
param(
    [string]$ProjectDir = ".",
    [string]$Name
)

./java/run-update-dependencies.ps1 -RepoName "de-detection-java-test" -ProjectDir $ProjectDir -Name $Name

exit $LASTEXITCODE

