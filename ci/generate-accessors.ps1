
./tools/ci/generate-dd-accessors.ps1

$ToolsPath = [IO.Path]::Combine($pwd, "tools")
$DdPath = [IO.Path]::Combine($pwd, "device-detection-java")

Copy-Item "$ToolsPath/Java/DeviceDataBase.java" "$DdPath/device-detection.shared/src/main/java/fiftyone/devicedetection/shared/"
Copy-Item "$ToolsPath/Java/DeviceData.java" "$DdPath/device-detection.shared/src/main/java/fiftyone/devicedetection/shared/"
