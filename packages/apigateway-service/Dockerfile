FROM openjdk:11-jdk AS build
WORKDIR /tmp
COPY . /tmp
RUN chmod +x ./gradlew && ./gradlew bootJar

FROM openjdk:11-jdk
WORKDIR /tmp
COPY --from=build /tmp/build/libs/apigateway-service-0.0.1-SNAPSHOT.jar /tmp/ApiGatewayService.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /tmp/ApiGatewayService.jar"]