@echo off
SET SCRIPT_PATH=%~dp0
java -jar %SCRIPT_PATH%/lib/edmm-cli.jar %*
