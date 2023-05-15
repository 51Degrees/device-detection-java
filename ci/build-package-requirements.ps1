
param(
    [string]$ProjectDir = ".",
    [string]$Name
)

$RepoName = "device-detection-java"
$RepoPath = [IO.Path]::Combine($pwd, $RepoName)
$PathToBinaries = "$RepoPath/device-detection.hash.engine.on-premise/target/classes"

./java/build-package-requirements.ps1 -RepoName $RepoName -ProjectDir $ProjectDir -Name $Name 

$Files = Get-ChildItem -Path $PathToBinaries/* -Include "*.dll", "*.so", "*.dylib"

# Create a directory for binary files from which they will be uploaded
# as artifacts.
New-Item -path "$RepoPath/package-files/" -ItemType Directory -Force 

# Copy binary files over 
foreach($file in $Files){
    Copy-Item -Path $file -Destination "$RepoPath/package-files/$($file.Name)"
}

exit $LASTEXITCODE
