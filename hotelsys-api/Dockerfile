# ===============================
# STAGE 1: BUILD (Maven + Java)
# ===============================
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ===============================
# STAGE 2: RUNTIME (solo Java)
# ===============================
FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
