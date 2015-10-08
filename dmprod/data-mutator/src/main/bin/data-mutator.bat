::
:: Copyright (c) 2015 Persinity Inc.
::

@ECHO OFF

SET SCRIPT_DIR=%~dp0

:: start in main directory to keep log dir there
cd %SCRIPT_DIR%..

java -cp %SCRIPT_DIR%..\config;%SCRIPT_DIR%..\lib\* com.persinity.ndt.datamutator.DataMutator %*
