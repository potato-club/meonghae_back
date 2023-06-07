FROM adoptopenjdk:11-jdk-hotspot
VOLUME /tmp
COPY build/libs/s3fileservice-0.0.1-SNAPSHOT.jar s3.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /s3.jar"]