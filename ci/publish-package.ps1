
param(
    [Parameter(Mandatory=$true)]
    [string]$MavenSettings,
    [Parameter(Mandatory=$true)]
    $Version

)

./java/publish-package-maven.ps1 -RepoName "de-detection-java-test" -MavenSettings $MavenSettings -Version $Version


exit $LASTEXITCODE
