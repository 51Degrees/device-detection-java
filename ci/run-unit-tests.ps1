param(
    [string]$ProjectDir = ".",
    [string]$Name,
    [hashtable]$Keys
)

./java/run-unit-tests.ps1 -RepoName "device-detection-java" -ProjectDir $ProjectDir -Name $Name -ExtraArgs "-DTestResourceKey=$($Keys.TestResourceKey)"

exit $LASTEXITCODE
