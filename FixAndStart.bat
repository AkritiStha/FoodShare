@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-21"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo.
echo ========================================================
echo   FoodShare REPAIR ^& STARTUP SCRIPT
echo ========================================================
echo.

echo [1/3] Cleaning up stale processes on port 8081...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8081 ^| findstr LISTENING') do (
    echo killing process ID %%a...
    taskkill /f /pid %%a >nul 2>&1
)

echo [2/3] Building application...
"%JAVA_HOME%\bin\java.exe" -classpath ".mvn\wrapper\maven-wrapper.jar" "-Dmaven.multiModuleProjectDirectory=%CD%" org.apache.maven.wrapper.MavenWrapperMain clean install -DskipTests

echo [3/3] Starting server on http://localhost:8081...
echo (This window must stay open for the server to run)
echo.

"%JAVA_HOME%\bin\java.exe" -classpath ".mvn\wrapper\maven-wrapper.jar" "-Dmaven.multiModuleProjectDirectory=%CD%" org.apache.maven.wrapper.MavenWrapperMain cargo:run
