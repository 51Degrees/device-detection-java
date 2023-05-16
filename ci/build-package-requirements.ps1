
param(
    [string]$ProjectDir = ".",
    [string]$Name,
    [Parameter(Mandatory=$true)]
    $RepoName
)

$RepoPath = [IO.Path]::Combine($pwd, $RepoName)
$PathToBinaries = "$RepoPath/device-detection.hash.engine.on-premise/target/classes"

./java/build-package-requirements.ps1 -RepoName $RepoName -ProjectDir $ProjectDir -Name $Name 

$Files = Get-ChildItem -Path $PathToBinaries/* -Include "*.dll", "*.so", "*.dylib"

# Create a directory for binary files from which they will be uploaded
# as artifacts.
$PackageFolder = "package-files"
New-Item -path $PackageFolder  -ItemType Directory -Force 

# Copy binary files over 
foreach($file in $Files){
    Copy-Item -Path $file -Destination "$PackageFolder/$($file.Name)"
}

exit $LASTEXITCODE
