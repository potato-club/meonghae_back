FROM openjdk:11-jdk AS build
WORKDIR /tmp
COPY . /tmp
RUN chmod +x ./gradlew && ./gradlew bootJar

FROM openjdk:11-jdk
WORKDIR /tmp
COPY --from=build /tmp/build/libs/meonghae-back-0.0.1-SNAPSHOT.jar /tmp/Eureka.jar
ENTRYPOINT ["java","-jar","/tmp/Eureka.jar"]