
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
function Get-CurrentFileName {
    $MyInvocation.ScriptName
}
function Get-CurrentLineNumber {
    $MyInvocation.ScriptLineNumber
}
$RepoPath = [IO.Path]::Combine($pwd, $RepoName)
$ExamplesRepoName = "$RepoName-examples"

try {
    Write-Output "Cloning '$ExamplesRepoName'"
    ./steps/clone-repo.ps1 -RepoName "device-detection-java-examples" -OrgName $OrgName
   
    if ($Keys.DeviceDetection -ne "") {
        Write-Output "Moving TAC file for examples"
        $TacFile = [IO.Path]::Combine($RepoPath, "TAC-HashV41.hash") 
        Copy-Item $TacFile device-detection-java-examples/device-detection-data/TAC-HashV41.hash
    }
    else {
        Copy-Item $RepoPath/device-detection.hash.engine.on-premise/src/main/cxx/device-detection-cxx/device-detection-data/51Degrees-LiteV4.1.hash device-detection-java-examples/device-detection-data/51Degrees-LiteV4.1.hash
        Write-Output "::warning file=$(Get-CurrentFileName),line=$(Get-CurrentLineNumber),endLine=$(Get-CurrentLineNumber),title=No On-Premise Data File::No on-premise license was provided, so some on-premise tests will not run."
    }

    if ($Keys.TestResourceKey -eq "") {
        Write-Output "::warning file=$(Get-CurrentFileName),line=$(Get-CurrentLineNumber),endLine=$(Get-CurrentLineNumber),title=No Resource Key::No resource key was provided, so cloud tests will not run."
    }
    
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

exit $LASTEXITCODE
