FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# copy maven wrapper first and make it executable
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src

RUN chmod +x ./mvnw && ./mvnw -q -B -DskipTests package

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/pos-backend-0.0.1-SNAPSHOT.jar"]
