FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy the Gradle wrapper and build files to the container
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts ./
COPY settings.gradle.kts ./
COPY gradle.properties ./

# Download and cache the project dependencies
RUN ./gradlew build --no-daemon || return 0

# Copy the application source code to the container
COPY src ./src

# Build the application
RUN ./gradlew build --no-daemon

# Expose the application port
EXPOSE 8080

# Run the application
CMD ["./gradlew", "bootRun"]