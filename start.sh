#!/bin/bash
mvn clean install -DskipTests
java -jar api/target/api.war
