FROM openjdk:11-jdk AS build
WORKDIR /tmp
COPY . /tmp
RUN chmod +x ./gradlew && ./gradlew build

FROM openjdk:11-jdk
WORKDIR /tmp
COPY --from=build /tmp/build/libs/meonghae-0.0.1-SNAPSHOT.jar Eureka.jar
ENTRYPOINT ["java","-jar","Eureka.jar"]