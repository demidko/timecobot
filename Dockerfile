# syntax = docker/dockerfile:experimental
FROM gradle:jdk16 as builder
WORKDIR /project
COPY src ./src
COPY build.gradle.kts ./build.gradle.kts
RUN gradle clean test shadowJar

FROM openjdk:16-buster as backend
WORKDIR /root
COPY --from=builder /project/build/libs/*.jar ./app
ENTRYPOINT ["java", "-jar", "/root/app"]
