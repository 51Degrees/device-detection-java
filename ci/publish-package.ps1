
param(
    [Parameter(Mandatory=$true)]
    [string]$MavenSettings,
    [Parameter(Mandatory=$true)]
    $Version,
    [Parameter(Mandatory=$true)]
    [string]$RepoName

)

./java/publish-package-maven.ps1 -RepoName $RepoName -MavenSettings $MavenSettings -Version $Version


exit $LASTEXITCODE
