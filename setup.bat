@echo off
echo Setting JAVA_HOME to JDK 21...
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%

echo Downloading Gradle wrapper JAR...
powershell -Command "& {Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar'}"
echo Gradle wrapper JAR downloaded successfully!
echo.
echo You can now build the mod using: build-mod.bat
pause
