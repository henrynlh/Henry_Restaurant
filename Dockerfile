FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:17-jre-jammy
RUN apt-get update && apt-get install -y --no-install-recommends fonts-dejavu-core && rm -rf /var/lib/apt/lists/* && useradd -m restaurant
WORKDIR /app
COPY --from=build /app/target/HK_Restaurant-1.0.0.jar app.jar
COPY images images
RUN chown -R restaurant:restaurant /app
USER restaurant
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
