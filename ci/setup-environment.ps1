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
    curl -sSL --fail-with-body 'https://github.com/51Degrees/common-ci/releases/download/zig/zig-linux-x86_64-0.13.0.tar.xz' | tar -xJ --strip-components=1 -C ~/.local/opt/zig
    New-Item -ItemType SymbolicLink -Path ~/.local/bin/zig -Target ~/.local/opt/zig/zig | Out-Null
}

./java/setup-enviroment.ps1 -RepoName $RepoName -ProjectDir $ProjectDir -JavaSDKEnvVar $JavaSDKEnvVar

exit $LASTEXITCODE
