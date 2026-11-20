# syntax=docker/dockerfile:1

# ---------- Étape 1 : build du JAR avec Maven ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Télécharge les dépendances d'abord, pour profiter du cache Docker
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q package -DskipTests

# ---------- Étape 2 : image d'exécution légère ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Utilisateur non-root
RUN groupadd --system app && useradd --system --gid app --no-create-home app

COPY --from=build /workspace/target/app-0.0.1-SNAPSHOT.jar /app/app.jar

USER app
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
