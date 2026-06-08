
param(
    [string]$Name = "Windows_Java_8",
    [string]$Version = "0.0.0",
    # Keys contain the License and Resource Keys needed to run the tests.
    [Parameter(Mandatory=$true)]
    [Hashtable]$Keys,
    [Parameter(Mandatory=$true)]
    [string]$RepoName,
    [string]$OrgName

    
)

$RepoPath = [IO.Path]::Combine($pwd, $RepoName)
$ExamplesRepoName = "$RepoName-examples"

try {
    Write-Output "Cloning '$ExamplesRepoName'"
    ./steps/clone-repo.ps1 -RepoName "device-detection-java-examples" -OrgName $OrgName
    
    Write-Output "Moving TAC file for examples"
    $TacFile = [IO.Path]::Combine($RepoPath, "TAC-HashV41.hash") 
    Copy-Item $TacFile device-detection-java-examples/device-detection-data/TAC-HashV41.hash

    Write-Output "Moving evidence files for examples"
    $UAFile = [IO.Path]::Combine($RepoPath, "20000 User Agents.csv") 
    $EvidenceFile = [IO.Path]::Combine($RepoPath, "20000 Evidence Records.yml")
    Copy-Item $UAFile "device-detection-java-examples/device-detection-data/20000 User Agents.csv"
    Copy-Item $EvidenceFile "device-detection-java-examples/device-detection-data/20000 Evidence Records.yml"
    
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
        $destDir = "..\$RepoName\test-results\integration"
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

$status = $LASTEXITCODE

Write-Host 'Running Selenium tests...'
try {
    # Build and start the cloud example (already pinned to the dev version above).
    Push-Location "device-detection-java-examples"
    try {
        mvn -B --no-transfer-progress -pl web/getting-started.cloud -am package -DskipTests
        $jar = (Get-ChildItem web/getting-started.cloud/target/*-jar-with-dependencies.jar | Select-Object -First 1).FullName
        $env:PORT = 8099
        $env:TestCloudEndpoint = "https://cloud.51degrees.com/api/v4"
        $env:TestResourceKey = $Keys.TestResourceKey
        $example = java -jar $jar 2>&1 &
    } finally { Pop-Location }

    # Get the shared contract tests.
    if (-not (Test-Path selenium-api-tests)) {
        git clone --depth 1 https://github.com/51Degrees/selenium-api-tests.git
    }
    # Wait for the example to come up.
    curl -sS -o /dev/null --retry 5 --retry-connrefused "http://localhost:$env:PORT"

    $env:CLOUD_ROOT_URL = "https://cloud.51degrees.com/"
    $env:PAID_RESOURCE_KEY = $Keys.TestResourceKey
    $env:EXAMPLE_URL = "http://localhost:$env:PORT"
    $env:EXAMPLE_LANG = 'java'
    dotnet test selenium-api-tests -c Release --filter TestCategory=Contract
} catch {
    if ($example) { Write-Host '>>> example app output >>>'; Receive-Job $example | Out-Host; Write-Host '<<< app output <<<' }
    throw
} finally {
    if ($example) { Remove-Job -Force $example }
}
