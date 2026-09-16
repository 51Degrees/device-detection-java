
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
$examplesStatus = 0

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
    # Checked after the Selenium tests, so a failure here does not stop them.
    $examplesStatus = $LASTEXITCODE

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

# Runs the shared Selenium contract tests (category Contract in
# selenium-api-tests) against one web example. The example is built, started
# in the background on its own port and left to serve until the tests finish.
# Any failure is added to $Failures rather than thrown, so that one example
# failing does not stop the other from being tested.
function Invoke-ContractTests {
    param(
        [Parameter(Mandatory)][string]$Label,
        [Parameter(Mandatory)][string]$Module,
        [Parameter(Mandatory)][int]$Port,
        [Parameter(Mandatory)][AllowEmptyCollection()]
        [System.Collections.Generic.List[string]]$Failures,
        # Environment variables the example needs, removed again afterwards.
        [hashtable]$ExampleEnv = @{}
    )
    Write-Host "Running Selenium contract tests against the $Label example on port $Port"
    $example = $null
    $failure = $null
    $stdout = Join-Path (Get-Location).Path "example-$Port.out.log"
    $stderr = Join-Path (Get-Location).Path "example-$Port.err.log"
    try {
        # Build and start the example (already pinned to the dev version above).
        # The example finds its web content relative to the examples repository
        # root, so the process is started from there.
        Push-Location "device-detection-java-examples"
        try {
            mvn -B --no-transfer-progress -pl $Module -am package -DskipTests | Out-Host
            if ($LASTEXITCODE -ne 0) {
                $failure = "the $Label example failed to build"
            } else {
                $jar = (Get-ChildItem "$Module/target/*-jar-with-dependencies.jar" | Select-Object -First 1).FullName
                $env:PORT = $Port
                foreach ($name in $ExampleEnv.Keys) {
                    Set-Item "env:$name" $ExampleEnv[$name]
                }
                $example = Start-Process java -ArgumentList '-jar', "`"$jar`"" `
                    -WorkingDirectory (Get-Location).Path -NoNewWindow -PassThru `
                    -RedirectStandardOutput $stdout -RedirectStandardError $stderr
            }
        } finally { Pop-Location }

        if (-not $failure) {
            # Wait up to three minutes for the example to answer.
            $up = $false
            for ($i = 0; $i -lt 60 -and -not $up; $i++) {
                try {
                    Invoke-WebRequest -UseBasicParsing -TimeoutSec 10 "http://localhost:$Port" | Out-Null
                    $up = $true
                } catch {
                    if ($example.HasExited) { break }
                    Start-Sleep -Seconds 3
                }
            }
            if (-not $up) {
                $failure = "the $Label example did not answer on port $Port"
            }
        }

        if (-not $failure) {
            $env:EXAMPLE_URL = "http://localhost:$Port"
            $env:EXAMPLE_LANG = 'java'
            dotnet test selenium-api-tests -c Release --filter TestCategory=Contract | Out-Host
            if ($LASTEXITCODE -ne 0) {
                $failure = "the contract tests failed against the $Label example"
            }
        }

        if ($failure) {
            $Failures.Add($failure)
            if ($example) {
                Write-Host ">>> $Label example output >>>"
                Get-Content $stdout, $stderr -ErrorAction SilentlyContinue | Out-Host
                Write-Host "<<< $Label example output <<<"
            }
        }
    } finally {
        if ($example -and -not $example.HasExited) {
            Stop-Process -Id $example.Id -Force
        }
        Remove-Item env:PORT, env:EXAMPLE_URL -ErrorAction SilentlyContinue
        foreach ($name in $ExampleEnv.Keys) {
            Remove-Item "env:$name" -ErrorAction SilentlyContinue
        }
    }
}

# Failures are collected so that every set of tests runs before the job fails.
$failures = [System.Collections.Generic.List[string]]::new()
if ($examplesStatus -ne 0) {
    $failures.Add("the example tests failed with exit code $examplesStatus")
}

Write-Host 'Running Selenium tests...'
if ($IsLinux -and [System.Runtime.InteropServices.RuntimeInformation]::OSArchitecture -eq 'Arm64') {
    # The Selenium Manager binary that the suite ships for Linux is built for
    # x64 only, so on an Arm64 Linux runner every test fails with
    # "Exec format error" before a browser is started.
    Write-Host 'Skipping Selenium tests, Selenium Manager has no Linux Arm64 build'
} else {
    # Get the shared contract tests.
    if (-not (Test-Path selenium-api-tests)) {
        git clone --depth 1 https://github.com/51Degrees/selenium-api-tests.git
        if ($LASTEXITCODE -ne 0) { throw "Failed to clone selenium-api-tests" }
    }
    $env:CLOUD_ROOT_URL = "https://cloud.51degrees.com/"
    $env:PAID_RESOURCE_KEY = $Keys.TestResourceKey

    # The cloud example.
    Invoke-ContractTests -Label 'cloud' -Module 'web/getting-started.cloud' -Port 8099 -Failures $failures -ExampleEnv @{
        TestCloudEndpoint = "https://cloud.51degrees.com/api/v4"
        TestResourceKey = $Keys.TestResourceKey
    }

    # The on-premise example, against the TAC data file copied into the
    # examples repository above. The Lite data file has neither DeviceType nor
    # the JavaScript properties the contract tests need.
    Invoke-ContractTests -Label 'on-premise' -Module 'web/getting-started.onprem' -Port 8098 -Failures $failures -ExampleEnv @{
        '51DEGREES_DD_PATH' = (Resolve-Path "device-detection-java-examples/device-detection-data/TAC-HashV41.hash").Path
    }
}

if ($failures.Count -gt 0) {
    throw "Integration tests failed: $($failures -join '; ')"
}
