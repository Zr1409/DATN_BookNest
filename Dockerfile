# Sử dụng JDK 11
FROM eclipse-temurin:11-jdk

# Thư mục làm việc
WORKDIR /app

# Copy toàn bộ mã nguồn vào container
COPY . .

# Cấp quyền thực thi cho mvnw
RUN chmod +x mvnw

# Build project
RUN ./mvnw clean package -DskipTests -X


# Mở cổng 8080
EXPOSE 8080

# Chạy ứng dụng
CMD ["java", "-jar", "target/PS36614_DATN-0.0.1-SNAPSHOT.jar"]


