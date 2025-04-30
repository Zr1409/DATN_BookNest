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

# Thiết lập biến môi trường OAuth
ENV GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
ENV GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}
# Mở cổng 8080
EXPOSE 8080

# Thiết lập biến môi trường để dùng TLS 1.2
ENV JAVA_OPTS="-Djdk.tls.client.protocols=TLSv1.2"


# Chạy ứng dụng
CMD ["sh", "-c", "java -Djavax.net.ssl.trustStorePassword=changeit -Djdk.tls.client.protocols=TLSv1.2 -Dserver.port=$PORT -jar target/PS36614_DATN-0.0.1-SNAPSHOT.jar"]




