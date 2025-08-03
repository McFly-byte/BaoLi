FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY target/pricer-0.0.1-SNAPSHOT.jar app.jar

# 暴露服务端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s \
  CMD curl -fs http://127.0.0.1:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
