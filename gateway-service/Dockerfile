FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY pom.xml ./pom.xml
COPY gateway-service/pom.xml ./gateway-service/pom.xml

RUN apk add --no-cache maven

COPY gateway-service/src ./gateway-service/src

RUN mvn -f pom.xml install -N -DskipTests -B \
    && mvn -f gateway-service/pom.xml package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/gateway-service/target/*.jar app.jar

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "app.jar"]
