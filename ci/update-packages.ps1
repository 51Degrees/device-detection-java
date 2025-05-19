
param(
    [string]$Name,
    [Parameter(Mandatory=$true)]
    [string]$RepoName
)

./java/run-update-dependencies.ps1 -RepoName $RepoName -Name $Name

exit $LASTEXITCODE
