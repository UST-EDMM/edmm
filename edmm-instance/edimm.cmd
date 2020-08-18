@echo off
SET SCRIPT_PATH=%~dp0
java -jar %SCRIPT_PATH%/lib/edmm-instance-1.0.0-SNAPSHOT.jar %*
