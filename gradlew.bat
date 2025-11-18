@ECHO OFF
SETLOCAL

SET BASEDIR=%~dp0
SET WRAPPER_DIR=%BASEDIR%\gradle\wrapper
SET WRAPPER_JAR=%WRAPPER_DIR%\gradle-wrapper.jar
SET WRAPPER_BIN=%WRAPPER_DIR%\gradle-wrapper.exe

IF EXIST "%WRAPPER_BIN%" (
  "%WRAPPER_BIN%" %*
  GOTO :EOF
)

IF EXIST "%WRAPPER_JAR%" (
  java -Xmx64m -Xms64m -classpath "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
  GOTO :EOF
)

WHERE gradle >NUL 2>&1
IF %ERRORLEVEL% EQU 0 (
  gradle %*
  GOTO :EOF
)

ECHO Gradle wrapper executable not found. Please generate the wrapper (^./gradlew wrapper^) or install Gradle.
EXIT /B 1
