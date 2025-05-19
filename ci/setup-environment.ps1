param(
    [Parameter(Mandatory=$true)]
    [string]$RepoName,
    [Parameter(Mandatory=$true)]
    [string]$JavaSDKEnvVar
)
$ErrorActionPreference = "Stop"
$PSNativeCommandUseErrorActionPreference = $true

./java/setup-enviroment.ps1 -RepoName $RepoName -JavaSDKEnvVar $JavaSDKEnvVar

exit $LASTEXITCODE
