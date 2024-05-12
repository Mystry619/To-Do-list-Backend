#
# Build stage
#
FROM gradle:7.3.3-jdk11 AS build
WORKDIR /app
COPY . /app/
RUN gradle build

#
# Package stage
#
FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
EXPOSE 10000
ENTRYPOINT ["java", "-Dserver.port=10000", "-jar", "app.jar"]
