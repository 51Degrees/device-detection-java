param(
    [string]$ProjectDir = "."
)

./java/run-unit-tests.ps1 -RepoName "device-detection-java-test" -ProjectDir $ProjectDir -Name ""

exit $LASTEXITCODE