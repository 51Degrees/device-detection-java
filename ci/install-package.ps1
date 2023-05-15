
param(
    [string]$ProjectDir = ".",
    [string]$Name,
    [Parameter(Mandatory=$true)]
    [string]$RepoName
)

./java/install-package.ps1 -RepoName $RepoName -ProjectDir $ProjectDir -Name $Name

exit $LASTEXITCODE

