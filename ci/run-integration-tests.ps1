
param(
    [string]$Name = "Windows_Java_8",
    [string]$Version = "0.0.0",
    # Keys contain the License and Resource Keys needed to run the tests.
    [Hashtable]$Keys
    
)

$RepoPath = [IO.Path]::Combine($pwd, "de-detection-java-test")
$ExamplesRepoName = "device-detection-java-examples"

try {
    Write-Output "Cloning '$ExamplesRepoName'"
    ./steps/clone-repo.ps1 -RepoName "device-detection-java-examples"
    
    Write-Output "Moving TAC file for examples"
    $TacFile = [IO.Path]::Combine($RepoPath, "TAC-HashV41.hash") 
    Move-Item $TacFile device-detection-java-examples/device-detection-data/TAC-HashV41.hash

    Write-Output "Moving evidence files for examples"
    $UAFile = [IO.Path]::Combine($RepoPath, "20000 User Agents.csv") 
    $EvidenceFile = [IO.Path]::Combine($RepoPath, "20000 Evidence Records.yml")
    Move-Item $UAFile "device-detection-java-examples/device-detection-data/20000 User Agents.csv"
    Move-Item $EvidenceFile "device-detection-java-examples/device-detection-data/20000 Evidence Records.yml"
    
    Write-Output "Entering device-detection-java directory"
    Push-Location $RepoPath
    # If the Version parameter is set to "0.0.0", set the Version variable to the version specified in the pom.xml file
    if ($Version -eq "0.0.0"){
        $Version = mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression="project.version" -q -DforceStdout
    }

    Pop-Location

    Write-Output "Entering device-detection-examples directory"
    Push-Location $ExamplesRepoName


    Write-Output "Setting examples device-detection package dependency to version '$Version'"
    mvn versions:set-property -Dproperty="device-detection.version" "-DnewVersion=$Version"

    Write-Output "Testing Examples"
    mvn clean test "-DTestResourceKey=$($Keys.TestResourceKey)" "-DSuperResourceKey=$($Keys.TestResourceKey)" "-DLicenseKey=$($Keys.DeviceDetection)"

    Write-Output "Copying test results".
    # Copy the test results into the test-results folder
    Get-ChildItem -Path . -Directory -Depth 1 | 
    Where-Object { Test-Path "$($_.FullName)\pom.xml" } | 

    ForEach-Object { 
        $targetDir = "$($_.FullName)\target\surefire-reports"
        $destDir = "..\de-detection-java-test\test-results\integration"
        if(!(Test-Path $destDir)) { New-Item -ItemType Directory -Path $destDir }
        if(Test-Path $targetDir) {
            Get-ChildItem -Path $targetDir |
            ForEach-Object {
                Copy-Item -Path $_.FullName -Destination $destDir
            }
        }
    }
}

finally {

    Write-Output "Leaving '$ExamplesRepoName'"
    Pop-Location

}

exit $LASTEXITCODE
