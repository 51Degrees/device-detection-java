param(
    [string]$ProjectDir = "."
)

./java/run-unit-tests.ps1 -RepoName "de-detection-java-test" -ProjectDir $ProjectDir -Name ""

exit $LASTEXITCODE