@echo off

%~d0
cd %~dp0

set dir =%~dp0

rd /s/q .settings
del /s/q .project

call mvn clean:clean eclipse:clean eclipse:myeclipse-clean eclipse:rad-clean

