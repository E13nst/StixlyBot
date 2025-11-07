# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the application's jar file to the container
COPY build/libs/stixly-bot-0.0.1-SNAPSHOT.jar /app/stixly-bot.jar

# Copy the external application.yaml file into the Docker container
COPY src/main/resources/application.yaml /app/config/application.yaml

# Copy the additional file to the container
# COPY prompt.txt /app/prompt.txt

# Command to run the jar file
CMD ["java", "-jar", "/app/stixly-bot.jar", "--spring.config.location=file:/app/config/application.yaml"]