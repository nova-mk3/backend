# Liberica JDK 21 이미지 사용
FROM bellsoft/liberica-openjdk-alpine:21

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 파일 복사
COPY build/libs/backend-0.0.1-SNAPSHOT.jar backend.jar
COPY build/libs/.env .env

# 애플리케이션 실행 포트
EXPOSE 4001



# 실행 명령
ENTRYPOINT ["java", "-jar", "backend.jar"]