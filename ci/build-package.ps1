
param(
    [string]$ProjectDir = ".",
    [string]$Name
)

# Name of this repository
$RepoName = "de-detection-java-test"

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
$Files = Get-ChildItem -Path $PackageFilesPath/* -Include *.dll
foreach($file in $Files){
    Copy-Item -Path $file -Destination "$BinariesPath/$($file.Name)"
}

./java/build-package.ps1 -RepoName "de-detection-java-test" -ProjectDir $ProjectDir -Name $Name 


exit $LASTEXITCODE
