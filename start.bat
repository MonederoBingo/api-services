@echo off
cd %~dp0
call mvn clean install -DskipTests
call java -jar api/target/api.war
