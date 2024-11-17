FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY ./pom.xml ./pom.xml
COPY ./src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package && \
    java -Djarmode=tools \
    -jar target/*.jar \
    extract --layers --launcher --destination target/extracted

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build /build/target/extracted/application .
COPY --from=build /build/target/extracted/dependencies .
COPY --from=build /build/target/extracted/snapshot-dependencies .
COPY --from=build /build/target/extracted/spring-boot-loader .
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]