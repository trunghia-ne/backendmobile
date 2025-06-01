FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy toàn bộ source vào container
COPY . .

# Build file .war
RUN mvn clean package -DskipTests

# Giai đoạn runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy file WAR từ stage build
COPY --from=build /app/target/*.war app.war

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.war"]
