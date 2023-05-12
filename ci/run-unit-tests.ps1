param(
    [string]$ProjectDir = ".",
    [string]$Name,
    [hashtable]$Keys
)

./java/run-unit-tests.ps1 -RepoName "de-detection-java-test" -ProjectDir $ProjectDir -Name $Name -ExtraArgs "-DTestResourceKey=$($Keys.TestResourceKey)"

exit $LASTEXITCODE
