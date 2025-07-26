FROM eclipse-temurin:21-jdk as builder
WORKDIR /app
COPY . .
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/moneymanager-0.0.1-SNAPSHOT.jar moneymanager-v1.0.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "moneymanager-v1.0.jar"]
