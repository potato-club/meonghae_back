FROM adoptopenjdk:11-jdk-hotspot
VOLUME /tmp
COPY build/libs/community-service-0.0.1-SNAPSHOT.jar CommunityService.jar
ENTRYPOINT ["java","-jar","CommunityService.jar"]