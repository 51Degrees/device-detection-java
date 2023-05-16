param(
    [string]$ProjectDir = ".",
    [string]$Name,
    [Parameter(Mandatory=$true)]
    [string]$RepoName
)

Write-Output $ENV:JAVA_HOME

./java/build-project.ps1 -RepoName $RepoName -ProjectDir $ProjectDir -Name $Name

exit $LASTEXITCODE
