FROM openjdk:17-jdk-slim

# 애플리케이션 디렉토리 생성
WORKDIR /app

# Gradle Wrapper 및 build 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# 소스 코드 복사
COPY src src

# 권한 설정 및 빌드
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# JAR 파일을 app.jar로 복사
RUN cp build/libs/*.jar app.jar

# 로그 디렉토리 생성
RUN mkdir -p /app/logs

# 8080 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]