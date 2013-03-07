@ECHO OFF
SET JAVA_FLAGS=-Dhttp.proxyHost=localhost -Dhttp.proxyPort=5000
SET CUR_PATH=%~dp0
SET TARGET_DIR=%CUR_PATH%..\log\
ECHO Retrieving today's comic.
java %JAVA_FLAGS% -cp %CUR_PATH%macanudo-0.1.0-SNAPSHOT-standalone.jar clojure.main -m macanudo %TARGET_DIR%
