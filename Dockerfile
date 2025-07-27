FROM eclipse-temurin:21-jdk as builder

WORKDIR /app

COPY . .

# Make mvnw executable
RUN chmod +x ./mvnw

# Build the jar skipping tests
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the jar from the builder stage

COPY --from=builder /app/target/moneybench-0.0.1-SNAPSHOT.jar moneybench-v1.0.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "moneymanager-v1.0.jar"]
