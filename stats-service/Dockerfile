FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml pom.xml
COPY core-registry-service/pom.xml core-registry-service/pom.xml
COPY live-engine-service/pom.xml live-engine-service/pom.xml
COPY stats-service/pom.xml stats-service/pom.xml
RUN mvn -q -pl stats-service -am dependency:go-offline
COPY . .
RUN mvn -q -pl stats-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/stats-service/target/stats-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
