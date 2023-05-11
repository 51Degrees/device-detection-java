
param(
    [string]$ProjectDir = ".",
    [string]$Version = "0.0.0",
    [string]$Name
)

# Define the name of the project repository and path
$RepoName = "device-detection-java"
$RepoPath = [IO.Path]::Combine($pwd, $RepoName)

# Define the directory of the examples
$ExamplesDir = [IO.Path]::Combine($pwd, "device-detection-java-examples")

# Download and move necessary files, catch errors if any
try{

    if(!(Test-Path -Path $ExamplesDir)){

        Write-Output "Cloning '$ExamplesRepoName'"
        ./steps/clone-repo.ps1 -RepoName "device-detection-java-examples"

        Write-Output "Moving TAC file for examples"
        $TacFile = [IO.Path]::Combine($RepoPath, "TAC-HashV41.hash") 
        Move-Item $TacFile device-detection-java-examples/device-detection-data/TAC-HashV41.hash
        
    }
}
catch {
    Write-Output "An error occurred while downloading or moving files: $_"
}

# Run tests and copy files, catch errors if any
try {

    Write-Output "Entering '$RepoPath'"
    Push-Location $RepoPath
    # If the Version parameter is set to "0.0.0", set the Version variable to the version specified in the pom.xml file
    if ($Version -eq "0.0.0"){
        $Version = mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression="project.version" -q -DforceStdout
    }
    Pop-Location

    # Enter the Java project examples directory
    Write-Output "Entering $ExamplesDir"
    Push-Location $ExamplesDir

    Write-Output "Setting examples device-detection package dependency to version '$Version'"
    mvn versions:set-property -Dproperty="device-detection.version" "-DnewVersion=$Version"

    mvn clean test -DfailIfNoTests=false -Dtest="*Performance*" 

    # Copy the test results into the test-results folder
    Get-ChildItem -Path . -Directory -Depth 1 | 
    Where-Object { Test-Path "$($_.FullName)\pom.xml" } | 
    ForEach-Object { 
        $targetDir = "$($_.FullName)\target\surefire-reports"
        $destDir = "..\device-detection-java\test-results\performance"
        $destDirSummary = "..\device-detection-java\test-results\performance-summary"

        if(!(Test-Path $destDir)) { New-Item -ItemType Directory -Path $destDir }
        if(Test-Path $targetDir) {
            Get-ChildItem -Path $targetDir | 
            Where-Object { $_.Name -like "*Performance*" } |
            ForEach-Object {
                Copy-Item -Path $_.FullName -Destination $destDir
            }
        }
    }

    # Copy the test results into the test-results/performance-summary folder for performance comparison 
    Copy-Item -Path $destDir -Destination $destDirSummary -Recurse
}
catch {
    Write-Output "An error occurred while running tests or copying files: $_"
}
finally {
    Write-Output "Leaving '$ExamplesDir'"
    Pop-Location
}


$RepoPath = [IO.Path]::Combine($pwd, $RepoName)

Write-Output "Entering '$RepoPath'"
Push-Location $RepoPath

try {
    
    # Define the file paths for the performance results and output file
    $PerfResultsFile = [IO.Path]::Combine($RepoPath, "test-results", "performance-summary", "fiftyone.devicedetection.examples.console.PerformanceBenchmarkTest-output.txt")
    $outputFile = [IO.Path]::Combine($RepoPath, "test-results", "performance-summary","results_$Name.json")

    #TODO: DELETE BELOW - ADDED FOR DEBUGGING
    $content = Get-Content $PerfResultsFile
    Write-Output $content

    # Initilise variables for storing data
    $profileNum = 0
    $profiles = @{}
    $currentThreadNum = 1

    # Read the content of the performance results file line by line
    Get-Content $PerfResultsFile | ForEach-Object {
         # If the line matches a profile header, extract the profile properties and create a new profile.
        if($_ -match "MaxPerformance AllProperties: (true|false), performanceGraph: (true|false), predictiveGraph (true|false)") {

            $AllProperties = [string]$Matches[1]
            $PerformanceGraph = [string]$Matches[2]
            $PredictiveGraph = [string]$Matches[3]

            $profileNum++
            $currentProfile = $profiles["MaxPerformance-$AllProperties-$PerformanceGraph-$PredictiveGraph"] = @{ "Threads" = @{} }
            $currentThreadNum = 1
        }
        # If the line matches a thread data line, extract the thread data and add it to the current profile.
        elseif($_ -match "Thread:  ([\d,]+) detections, elapsed ([\d.]+) seconds, ([\d,]+) Detections per second") {
            $currentProfile["Threads"]["Thread_$currentThreadNum"] = @{
                "Detections" = [int]($Matches[1].Replace(",", ""))
                "ElapsedSeconds" = [double]$Matches[2]
                "DetectionsPerSecond" = [int]($Matches[3].Replace(",", ""))
            }
            $currentThreadNum++
        }
        # If the line matches an overall data line, extract the overall data and add it to the current profile.
        elseif($_ -match "Overall: ([\d,]+) detections, Average millisecs per detection: ([\d.]+), Detections per second: ([\d,]+)") {
            $currentProfile["Overall"] = @{
                "Detections" = [int]($Matches[1].Replace(",", ""))
                "AvgMillisecsPerDetection" = [double]$Matches[2]
                "DetectionsPerSecond" = [int]($Matches[3].Replace(",", ""))
            }
        }
    }

    # Create a JSON object with specific performance metrics and write it to the output file.
    Write-Output $profiles
    Write-Output "{
        'HigherIsBetter': {
            'DetectionsPerSecond': $($profiles['MaxPerformance-false-true-false'].Overall.DetectionsPerSecond)
        },
        'LowerIsBetter': {
            'AvgMillisecsPerDetection' : $($profiles['MaxPerformance-false-true-false'].Overall.AvgMillisecsPerDetection)
        }
    }" > $OutputFile
}
finally{
    Write-Output "Leaving '$RepoPath'"
    Pop-Location
}


exit $LASTEXITCODE
