@ECHO OFF

SETLOCAL enabledelayedexpansion

ECHO %1
ECHO %2

IF [%1]==[hash] (SET API=Hash)
IF [%2]==[hash] (SET API=Hash)

IF [%API%]==[Hash] (
  SET SRCOUT=%~dp0\device-detection.hash.engine.on-premise\src\main\java\fiftyone\devicedetection\hash\engine\onpremise
) ELSE (
  ECHO No API name supplied. Use pattern or hash as an argument.
  GOTO end
)

FOR %%X IN (swig.exe) DO (SET SWIG_EXE=%%~$PATH:X)
IF DEFINED SWIG_EXE (
  ECHO SWIG auto generated code being rebuilt.
  IF [%API%]==[Hash] (
    swig -c++ -java -package fiftyone.devicedetection.hash.engine.onpremise.interop.swig -outdir %SRCOUT%\interop\swig -o %~dp0\Java_Hash_Engine.cpp %~dp0\device-detection.hash.engine.on-premise\hash_java.i
  )
) ELSE (
  ECHO SWIG not found. SWIG auto generated code will not be rebuilt.
)

:doneswig
IF [%1]==[swig-only] GOTO end

IF EXIST "%ProgramFiles(x86)%\Microsoft Visual Studio\Installer\vswhere.exe" (
  FOR /f "usebackq tokens=*" %%i in (`"%ProgramFiles(x86)%\Microsoft Visual Studio\Installer\vswhere.exe" -latest -products * -requires Microsoft.VisualStudio.Component.VC.Tools.x86.x64 -property installationPath`) DO (
    SET InstallDir=%%i
  )

  IF EXIST "!InstallDir!\Common7\Tools\vsdevcmd.bat" (
    @ECHO Using VS15
    CALL "!InstallDir!\Common7\Tools\vsdevcmd.bat"
  )
) ELSE (
  IF NOT DEFINED VCINSTALLDIR (
    IF DEFINED VS140COMNTOOLS (
      @ECHO Using VS14
      CALL "%VS140COMNTOOLS%VsDevCmd.bat"
    )
    IF DEFINED VS120COMNTOOLS (
      IF NOT DEFINED VCINSTALLDIR (
        @ECHO Using VS12
        CALL "%VS120COMNTOOLS%VsDevCmd.bat"
      )
    )
    IF DEFINED VS110COMNTOOLS (
      IF NOT DEFINED VCINSTALLDIR (
        @ECHO Using VS11
        CALL "%VS110COMNTOOLS%VsDevCmd.bat"
      )
    )
  )
)


FOR %%X IN (msbuild.exe) DO (SET MSBUILD_EXE=%%~$PATH:X)

IF DEFINED MSBUILD_EXE (
  FOR %%P IN (x64, x86) DO (
    msbuild %~dp0/VisualStudio/DeviceDetectionEngines.sln /t:FiftyOne_DeviceDetection_%API%_Java /p:Configuration=Release /p:"Platform=%%P"
  )
) ELSE (
  @ECHO MSBUILD not found. Native windows libraries can not be built.
)

:end
