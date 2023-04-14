param(
    [string]$ProjectDir = ".",
    [string]$Name
)

./java/build-project.ps1 -RepoName "device-detection-java-test" -ProjectDir $ProjectDir -Name $Name

exit $LASTEXITCODE
