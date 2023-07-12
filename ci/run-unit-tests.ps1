param(
    [string]$ProjectDir = ".",
    [string]$Name,
    [hashtable]$Keys,
    [Parameter(Mandatory=$true)]
    [string]$RepoName
)

if ($Keys.TestResourceKey -eq "") {
    Write-Output "::warning file=$(Get-CurrentFileName),line=$(Get-CurrentLineNumber),endLine=$(Get-CurrentLineNumber),title=No Resource Key::No resource key was provided, so cloud tests will not run."
}
./java/run-unit-tests.ps1 -RepoName $RepoName -ProjectDir $ProjectDir -Name $Name -ExtraArgs "-DTestResourceKey=$($Keys.TestResourceKey)"

exit $LASTEXITCODE
