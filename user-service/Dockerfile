FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY pom.xml ./pom.xml
COPY common/pom.xml ./common/pom.xml
COPY user-service/pom.xml ./user-service/pom.xml

RUN apk add --no-cache maven

COPY common/src ./common/src
COPY user-service/src ./user-service/src

RUN mvn -f pom.xml install -N -DskipTests -B \
    && mvn -f common/pom.xml install -DskipTests -B \
    && mvn -f user-service/pom.xml package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/user-service/target/*.jar app.jar

EXPOSE 9001

ENTRYPOINT ["java", "-jar", "app.jar"]
