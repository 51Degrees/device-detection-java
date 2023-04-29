param (
    [Parameter(Mandatory=$true)]
    [string]$VariableName
)

./java/get-next-package-version.ps1 -RepoName "de-detection-java-test" -VariableName $VariableName


exit $LASTEXITCODE
