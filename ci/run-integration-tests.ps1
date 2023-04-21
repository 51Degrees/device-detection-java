
param(
    [string]$Name = "Windows_Java_8",
    [string]$Version = "0.0.0",
    # Keys contain the License and Resource Keys needed to run the tests.
    # If running locally pass: 
    # $Keys = @{"TestResourceKey" = "Insert Key here" "DeviceDetection" = "Insert Key here"}
    [Parameter(Mandatory=$true)]
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
    
    Write-Output "Download Evidence file"
    curl -o "device-detection-java-examples/device-detection-data/20000 Evidence Records.yml" "https://media.githubusercontent.com/media/51Degrees/device-detection-data/master/20000%20Evidence%20Records.yml"
    
    Write-Output "Download User Agents file"
    curl -o "device-detection-java-examples/device-detection-data/20000 User Agents.csv" "https://media.githubusercontent.com/media/51Degrees/device-detection-data/master/20000%20User%20Agents.csv"


    Write-Output "Entering device-detection-examples directory"
    Push-Location device-detection-java-examples 

    Write-Output "Setting examples device-detection package dependency to version '$Version'"
    mvn versions:set-property -Dproperty="device-detection.version" "-DnewVersion=$Version"

    Write-Output "Testing Examples"
    mvn clean test "-DTestResourceKey=$($Keys.TestResourceKey)" "-DSuperResourceKey=$($Keys.TestResourceKey)" "-DLicenseKey=$($Keys.DeviceDetection)"

    Write-Output "Copying test results"
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

    Write-Output "Leaving '$RepoPath'"
    Pop-Location

}

exit $LASTEXITCODE
