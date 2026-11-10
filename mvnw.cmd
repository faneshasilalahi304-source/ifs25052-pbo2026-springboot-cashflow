@echo off
rem Maven wrapper sederhana: mengunduh Apache Maven otomatis pada pemakaian pertama.
set "BASE=%~dp0"
set "MVN_VER=3.9.11"
set "WRAP_DIR=%BASE%.mvn\wrapper"
set "MVN_HOME=%WRAP_DIR%\apache-maven-%MVN_VER%"
if not exist "%MVN_HOME%\bin\mvn.cmd" (
  echo Mengunduh Apache Maven %MVN_VER% ...
  if not exist "%WRAP_DIR%" mkdir "%WRAP_DIR%"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "[Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; $ProgressPreference='SilentlyContinue'; Invoke-WebRequest -UseBasicParsing 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MVN_VER%/apache-maven-%MVN_VER%-bin.zip' -OutFile '%WRAP_DIR%\maven.zip'; Expand-Archive -Force '%WRAP_DIR%\maven.zip' '%WRAP_DIR%'; Remove-Item '%WRAP_DIR%\maven.zip'"
  if errorlevel 1 (
    echo Gagal mengunduh Maven. Periksa koneksi internet atau gunakan Maven bawaan IntelliJ.
    exit /b 1
  )
)
"%MVN_HOME%\bin\mvn.cmd" %*
exit /b %ERRORLEVEL%
