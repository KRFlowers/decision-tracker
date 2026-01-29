@echo off
cd /d "%~dp0"
if not defined JAVA_HOME set JAVA_HOME=C:\Program Files\Java\jdk-20
call "%~dp0mvnw.cmd" compile
