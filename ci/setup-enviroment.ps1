param(
    [Parameter(Mandatory=$true)]
    [string]$RepoName,
    [Parameter(Mandatory=$true)]
    [string]$JavaSDKEnvVar,
    [string]$ProjectDir = "."
)

./java/setup-enviroment.ps1 -RepoName "device-detection-java-test" -ProjectDir $ProjectDir -JavaSDKEnvVar $JavaSDKEnvVar

exit $LASTEXITCODE