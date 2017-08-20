#!/bin/bash
java $JAVA_OPTS \
    -Dserver.port=$PORT \
    -Deureka.client.service-url.defaultZone=https://mb-discovery-service.herokuapp.com/eureka/ \
    -Deureka.instance.hostname=api.monederobingo.com \
    -Deureka.instance.prefer-ip-address=false \
    -Dspring.cloud.config.uri=https://mb-configuration-service.herokuapp.com/ \
    -Dsecurity.oauth2.resource.user-info-uri=http://prod.auth.monederobingo.com/user \
    -jar api/target/api.war \
