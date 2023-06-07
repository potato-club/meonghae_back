FROM adoptopenjdk:11-jdk-hotspot
VOLUME /tmp
COPY build/libs/apigateway-service-0.0.1-SNAPSHOT.jar ApiGatewayService.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /ApiGatewayService.jar"]