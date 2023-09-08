FROM maven:3.5.2-jdk-8 as build
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests


FROM openjdk:8 as run
WORKDIR /app
COPY --from=build ./build/target/*.jar ./application.jar
EXPOSE 8080

ENTRYPOINT java -jar application.jar
