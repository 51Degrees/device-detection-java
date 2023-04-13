param (
    [Parameter(Mandatory=$true)]
    [string]$VariableName
)

#./steps/get-next-package-version.ps1 -RepoName "device-detection-java-test" -VariableName "GitVersion"

Set-Variable -Name $VariableName -Value 4.4.21-gh-refact.1 -Scope Global

exit $LASTEXITCODE
