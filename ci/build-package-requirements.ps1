
param(
    [string]$ProjectDir = ".",
    [string]$Name
)

$RepoName = "de-detection-java-test"
$RepoPath = [IO.Path]::Combine($pwd, $RepoName)
$PathToBinaries = "$RepoPath/device-detection.hash.engine.on-premise/target/classes"

./java/build-package-requirements.ps1 -RepoName "de-detection-java-test" -ProjectDir $ProjectDir -Name $Name 

$Files = Get-ChildItem -Path $PathToBinaries/* -Include "*.dll", "*.so"

# Create a directory for binary files from which they will be uploaded
# as artifacts.
New-Item -path "$RepoPath/package-files/" -ItemType Directory -Force 

# Copy dll files over 
foreach($file in $Files){
    Copy-Item -Path $file -Destination "$RepoPath/package-files/$($file.Name)"
}

exit $LASTEXITCODE
