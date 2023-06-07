FROM adoptopenjdk:11-jdk-hotspot
VOLUME /tmp
COPY build/libs/profile-service-0.0.1-SNAPSHOT.jar ProfileService.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar ProfileService.jar"]