SET JAVA_FLAGS=-Dhttp.proxyHost=localhost -Dhttp.proxyPort=5000
SET CUR_PATH=%~dp0
SET TARGET_DIR=%CUR_PATH%log\
java %JAVA_FLAGS% -cp %CUR_PATH%.;%CUR_PATH%..\bin\clojure-1.5.0-RC16.jar clojure.main -m macanudo %TARGET_DIR%
PAUSE