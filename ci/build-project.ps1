param(
    [string]$ProjectDir = ".",
    [string]$Name,
    [string]$Version
)

Write-Output $ENV:JAVA_HOME

Write-Output $Version

./java/build-project.ps1 -RepoName "de-detection-java-test" -ProjectDir $ProjectDir -Name $Name -Version $Version

exit $LASTEXITCODE
