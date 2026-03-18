# ---------- STAGE 1: Build ----------
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies (Efficient Caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the fat JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- STAGE 2: Run ----------
# Using the JRE version to keep the image slim (~200MB vs ~800MB)
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built jar from the builder stage
# Replace 'ai-smart-ecommerce' if your artifact name is different in pom.xml
COPY --from=builder /app/target/*.jar app.jar

# Run as a non-root user for better security (Optional but recommended)
RUN useradd -m myuser
USER myuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]