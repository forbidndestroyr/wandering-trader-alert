@echo off
echo Setting JAVA_HOME to JDK 21...
set JAVA_HOME=C:\Program Files\Java\jdk-21
set PATH=%JAVA_HOME%\bin;%PATH%

echo Using Java version:
"%JAVA_HOME%\bin\java" -version
echo.

echo Building the mod...
.\gradlew.bat build

echo.
echo Build complete! Check build\libs for the JAR file.
pause