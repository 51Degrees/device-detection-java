
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
    [string]$CodeSigningCertPassword,
    [Parameter(Mandatory=$true)]
    $Version
)

./java/publish-package-maven.ps1 -RepoName "de-detection-java-test" -MavenSettings $MavenSettings -JavaGpgKeyPassphrase $JavaGpgKeyPassphrase -CodeSigningCert $CodeSigningCert -JavaPGP $JavaPGP -CodeSigningCertAlias $CodeSigningCertAlias -CodeSigningCertPassword $CodeSigningCertPassword -Version $Version


exit $LASTEXITCODE
