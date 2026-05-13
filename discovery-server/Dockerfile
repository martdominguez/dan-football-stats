FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY discovery-server/pom.xml pom.xml
RUN mvn -q dependency:go-offline
COPY discovery-server/src src
RUN mvn -q clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/discovery-server-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
