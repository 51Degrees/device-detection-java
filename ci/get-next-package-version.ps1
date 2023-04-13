param (
    [Parameter(Mandatory=$true)]
    [string]$VariableName
)

./java/get-next-package-version.ps1 -RepoName "device-detection-java-test" -VariableName "GitVersion"

exit $LASTEXITCODE