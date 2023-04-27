
param(
    [Parameter(Mandatory=$true)]
    [string]$MavenSettings,
    [Parameter(Mandatory=$true)]
    [string]$JavaGpgKeyPassphrase,
    [Parameter(Mandatory=$true)]
    [string]$CodeSigningCert,
    [Parameter(Mandatory=$true)]
    [string]$JavaPGP,
    [Parameter(Mandatory=$true)]
    [string]$CodeSigningCertAlias,
    [Parameter(Mandatory=$true)]
    [string]$CodeSigningCertPassword
)

./java/publish-package-maven.ps1 -RepoName "de-detection-java-test"

exit $LASTEXITCODE
