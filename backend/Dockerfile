# This image expects backend/app.jar in the build context.
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY app.jar /app/app.jar

ENV TZ=Asia/Shanghai
ENV JAVA_OPTS=""

EXPOSE 8082

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
