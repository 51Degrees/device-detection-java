param(
    [string]$ProjectDir = ".",
    [string]$Name,
    [hashtable]$Keys
)

Write-Output "Download Lite file"
curl -L -o "de-detection-java-test/51Degrees-LiteV4.1.hash" "https://github.com/51Degrees/device-detection-data/raw/main/51Degrees-LiteV4.1.hash"

./java/run-unit-tests.ps1 -RepoName "de-detection-java-test" -ProjectDir $ProjectDir -Name $Name -ExtraArgs "-DTestResourceKey=$($Keys.TestResourceKey)"

exit $LASTEXITCODE
