FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY pom.xml ./pom.xml
COPY common/pom.xml ./common/pom.xml
COPY product-service/pom.xml ./product-service/pom.xml

RUN apk add --no-cache maven

COPY common/src ./common/src
COPY product-service/src ./product-service/src

RUN mvn -f pom.xml install -N -DskipTests -B \
    && mvn -f common/pom.xml install -DskipTests -B \
    && mvn -f product-service/pom.xml package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/product-service/target/*.jar app.jar

EXPOSE 9002

ENTRYPOINT ["java", "-jar", "app.jar"]
