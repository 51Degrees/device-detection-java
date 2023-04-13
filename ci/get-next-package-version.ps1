param (
    [Parameter(Mandatory=$true)]
    [string]$VariableName
)

./steps/get-next-package-version.ps1 -RepoName "pipeline-java-test" -VariableName "GitVersion"

Set-Variable -Name $VariableName -Value $GitVersion.SemVer -Scope Global

exit $LASTEXITCODE