
param(
    [string]$ProjectDir = ".",
    [string]$Name,
    [string]$Version,
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
    [string]$MavenSettings
)

# Name of this repository
$RepoName = "device-detection-java"

# Path to this repository
$RepoPath = [IO.Path]::Combine($pwd, $RepoName)

# Path to where the binaries should be located in order to include them in the packages
$BinariesPath = "$RepoPath/device-detection.hash.engine.on-premise/target/classes/"

# Path to where the dll files are downloaded for all the platfoms
$PackageFilesPath = "$RepoPath/package-files/"

# Create a directory for binary files from which they will be uploaded
# as artifacts.
New-Item -path $BinariesPath -ItemType Directory -Force 

# Copy files over from target to package-files folder
$Files = Get-ChildItem -Path $PackageFilesPath/* -Recurse -Include "*.dll", "*.so", "*.dylib"
foreach($file in $Files){
    Copy-Item -Path $file -Destination "$BinariesPath/$($file.Name)"
}

./java/build-package.ps1 -RepoName $RepoName -ProjectDir $ProjectDir -Name $Name -Version $Version -ExtraArgs "-DskipNativeBuild=true" -JavaGpgKeyPassphrase $JavaGpgKeyPassphrase -CodeSigningCert $CodeSigningCert -JavaPGP $JavaPGP -CodeSigningCertAlias $CodeSigningCertAlias -CodeSigningCertPassword $CodeSigningCertPassword -MavenSettings $MavenSettings 


exit $LASTEXITCODE
