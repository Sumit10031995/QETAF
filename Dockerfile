# Use an official Maven image with OpenJDK 8
FROM maven:3.5.2-jdk-8

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project files into the container
COPY . .

# Install Maven dependencies and build the project
RUN mvn install -DskipTests

# Run Maven tests
CMD ["mvn", "test"]