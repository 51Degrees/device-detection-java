param(
    [string]$Name,
    [Parameter(Mandatory=$true)]
    [string]$RepoName
)

Write-Output $ENV:JAVA_HOME

./java/build-project.ps1 -RepoName $RepoName -Name $Name

exit $LASTEXITCODE
