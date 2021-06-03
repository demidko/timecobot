FROM gradle:jdk16 as builder
WORKDIR /project
COPY src ./src
COPY build.gradle.kts ./build.gradle.kts
RUN gradle clean test shadowJar

FROM openjdk:17-buster as backend
WORKDIR /root
COPY --from=builder /project/build/libs/*-all.jar ./app
ENTRYPOINT ["java", "-jar", "/root/app"]
