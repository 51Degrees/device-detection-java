param(
    [Parameter(Mandatory=$true)]
    [string]$RepoName,
    [Parameter(Mandatory=$true)]
    [string]$JavaSDKEnvVar,
    [string]$ProjectDir = "."
)
$ErrorActionPreference = "Stop"
$PSNativeCommandUseErrorActionPreference = $true

if ($IsLinux -and -not (Get-Command zig -ErrorAction SilentlyContinue)) {
    Write-Host "Installing zig for glibc cross-compilation..."
    New-Item -ItemType Directory -Force ~/.local/bin, ~/.local/opt/zig | Out-Null
    $url = switch ([System.Runtime.InteropServices.RuntimeInformation]::OSArchitecture) {
        'X64' {'https://github.com/51Degrees/common-ci/releases/download/zig/zig-linux-x86_64.tar.xz'}
        'Arm64' {'https://github.com/51Degrees/common-ci/releases/download/zig/zig-linux-aarch64.tar.xz'}
        default {Write-Error "Unimplemented architecture: $_"}
    }
    curl -sSL --fail-with-body $url | tar -xJ --strip-components=1 -C ~/.local/opt/zig
    # Don't try to use ~ instead of $HOME here
    New-Item -ItemType SymbolicLink -Path $HOME/.local/bin/zig -Target $HOME/.local/opt/zig/zig | Out-Null
    zig version
}

./java/setup-enviroment.ps1 -RepoName $RepoName -ProjectDir $ProjectDir -JavaSDKEnvVar $JavaSDKEnvVar

exit $LASTEXITCODE
