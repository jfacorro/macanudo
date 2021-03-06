@ECHO OFF
SET JAVA_FLAGS=-Dhttp.proxyHost=localhost -Dhttp.proxyPort=5000
SET CUR_PATH=%~dp0
SET TARGET_DIR=%CUR_PATH%..\log\
ECHO Retrieving today's comic...
java %JAVA_FLAGS% -cp %CUR_PATH%..\target\macanudo-0.1.0-SNAPSHOT-standalone.jar macanudo %TARGET_DIR%
%~d0
cd %CUR_PATH%..
git pull
ECHO Adding all files...
git add %TARGET_DIR%\.
ECHO Committing changes...
git commit -m "Added commit."
ECHO Pushing changes...
git push
