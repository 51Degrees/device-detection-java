param(
    [Parameter(Mandatory=$true)]
    [string]$RepoName,
    [Parameter(Mandatory=$true)]
    [string]$JavaSDKEnvVar,
    [string]$ProjectDir = "."
)
$ErrorActionPreference = "Stop"
$PSNativeCommandUseErrorActionPreference = $true

./java/setup-enviroment.ps1 -RepoName $RepoName -ProjectDir $ProjectDir -JavaSDKEnvVar $JavaSDKEnvVar

exit $LASTEXITCODE
