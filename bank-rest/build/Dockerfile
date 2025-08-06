FROM maven:3.9.9-eclipse-temurin-24-alpine AS build

COPY . .

RUN mvn clean package -pl bank-rest -am -DskipTests

FROM eclipse-temurin:24-jre-alpine

COPY --from=build /bank-rest/target/bank-rest-0.0.1-SNAPSHOT.jar bank-rest.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "bank-rest.jar"]