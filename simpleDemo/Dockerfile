# java jkd 17
FROM openjdk:21-jdk

# Set port number
EXPOSE 8080

# Set the working directory in the container
WORKDIR /app

# Copy the packaged JAR file into the container
COPY target/simpleDemo-1.0-SNAPSHOT.jar app.jar

# Specify the command to run on container startup
CMD ["java", "-jar", "app.jar"]
