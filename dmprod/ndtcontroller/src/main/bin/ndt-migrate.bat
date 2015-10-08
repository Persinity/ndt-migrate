::
:: Copyright (c) 2015 Persinity Inc.
::

@ECHO OFF

SET SCRIPT_DIR=%~dp0

:: start in main direcory to keep log dir there
cd %SCRIPT_DIR%..

java -cp %SCRIPT_DIR%..\config;%SCRIPT_DIR%..\lib\* -Xms1024m -Xmx4096m com.persinity.ndt.controller.NdtController %*
