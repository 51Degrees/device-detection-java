
param(
    [string]$ProjectDir = ".",
    [string]$Name = "Windows_Java_8",
    [string]$PackageVersion = "0.0.0"
    
)

./java/run-integration-tests.ps1 -RepoName "de-detection-java-test" -ProjectDir $ProjectDir -Name $Name -PackageVersion $PackageVersion

exit $LASTEXITCODE
