
param(
    [string]$Name,
    [Parameter(Mandatory=$true)]
    [string]$RepoName
)

./java/install-package.ps1 -RepoName $RepoName -Name $Name

exit $LASTEXITCODE

