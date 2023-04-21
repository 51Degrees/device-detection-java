param(
    [string]$ProjectDir = ".",
    [string]$Name,
    [Parameter(Mandatory=$true)]
    [string]$Version
)

Write-Output $ENV:JAVA_HOME

./java/build-project.ps1 -RepoName "de-detection-java-test" -ProjectDir $ProjectDir -Name $Name -Version $Version

exit $LASTEXITCODE
