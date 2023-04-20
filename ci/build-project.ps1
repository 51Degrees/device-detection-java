param(
    [string]$ProjectDir = ".",
    [string]$Name
)

Write-Output $ENV:JAVA_HOME

#./java/build-project.ps1 -RepoName "de-detection-java-test" -ProjectDir $ProjectDir -Name $Name

exit $LASTEXITCODE
