FROM gradle:jdk17 as builder
WORKDIR /project
COPY src ./src
COPY build.gradle.kts ./build.gradle.kts
RUN gradle clean build

FROM openjdk:18 as backend
WORKDIR /root
COPY --from=builder /project/build/libs/*-all.jar ./app
ENTRYPOINT ["java", "-jar", "/root/app"]
