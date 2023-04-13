param(
    [string]$ProjectDir = ".",
    [string]$Name
)

./java/build-project.ps1 -RepoName "device-detection-java" -ProjectDir $ProjectDir -Name $Name

exit $LASTEXITCODE