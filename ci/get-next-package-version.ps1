param (
    [Parameter(Mandatory=$true)]
    [string]$VariableName,
    [Parameter(Mandatory=$true)]
    [string]$RepoName
)

./java/get-next-package-version.ps1 -RepoName $RepoName -VariableName $VariableName


exit $LASTEXITCODE
