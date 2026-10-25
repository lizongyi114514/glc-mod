# 一键构建：下载 Gradle 8.8（如未缓存）并编译 Forge 模组
# 用法：右键“使用 PowerShell 运行”，或在终端执行 .\build.ps1
$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $root

# 1. 找 JDK 17
$javaHome = $env:JAVA_HOME
if (-not $javaHome -or -not (Test-Path "$javaHome\bin\javac.exe")) {
    $candidates = @(
        "$env:APPDATA\.minecraft\runtime\java-runtime-gamma-snapshot",
        "$env:LOCALAPPDATA\Programs\Eclipse Adoptium",
        "C:\Program Files\Java",
        "C:\Program Files\Eclipse Adoptium"
    )
    foreach ($c in $candidates) {
        if (Test-Path "$c\bin\javac.exe") { $javaHome = $c; break }
        if (Test-Path $c) {
            $sub = Get-ChildItem $c -Directory -ErrorAction SilentlyContinue |
                Where-Object { Test-Path "$($_.FullName)\bin\javac.exe" } | Select-Object -First 1
            if ($sub) { $javaHome = $sub.FullName; break }
        }
    }
}
if (-not $javaHome) {
    Write-Host "找不到 JDK 17，请先安装 JDK 17 并设置 JAVA_HOME。" -ForegroundColor Red
    exit 1
}
$env:JAVA_HOME = $javaHome
Write-Host "JAVA_HOME = $javaHome"

# 2. 准备 Gradle 8.8
$gradleDir = Join-Path $root "tools\gradle-8.8"
if (-not (Test-Path "$gradleDir\bin\gradle.bat")) {
    $zip = Join-Path $root "tools\gradle-8.8-bin.zip"
    if (-not (Test-Path $zip)) {
        Write-Host "下载 Gradle 8.8 ..."
        Invoke-WebRequest -Uri "https://services.gradle.org/distributions/gradle-8.8-bin.zip" -OutFile $zip
    }
    Expand-Archive -Path $zip -DestinationPath (Join-Path $root "tools") -Force
}

# 3. 清理本机专用的代理设置（打包时带上的 127.0.0.1 代理在别的机器上不可用）
$props = Join-Path $root "gradle.properties"
if (Test-Path $props) {
    $clean = Get-Content $props | Where-Object {
        $_ -notmatch '^systemProp\..*proxy' -and $_ -notmatch 'net\.minecraftforge\.gradle\.check\.certs'
    }
    Set-Content -Path $props -Value $clean -Encoding UTF8
}

# 4. 构建（Gradle 用户目录放在项目内，避免污染全局）
$env:GRADLE_USER_HOME = Join-Path $root ".gradle-home"
& "$gradleDir\bin\gradle.bat" build --no-daemon --console=plain
if ($LASTEXITCODE -ne 0) {
    Write-Host "构建失败，请把上面的错误发我。" -ForegroundColor Red
    exit $LASTEXITCODE
}
Write-Host "完成：build\libs\glc-1.0.0.jar" -ForegroundColor Green
