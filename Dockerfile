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
FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
