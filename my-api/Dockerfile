# --- 1단계: 빌드(Build) 환경 ---
# Java 11 (Temurin) JDK 이미지를 사용합니다.
FROM eclipse-temurin:17-jdk-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /workspace/app

# Gradle 래퍼 파일 복사 (스크린샷에 gradlew, gradle 폴더 확인됨)
COPY gradlew .
COPY gradle ./gradle

# build.gradle, settings.gradle 파일 복사 (의존성을 먼저 받기 위함)
COPY build.gradle settings.gradle ./
# 의존성 다운로드 (빌드 실패 시 dependencies만 실행)
RUN ./gradlew build -x test --parallel --build-cache || ./gradlew dependencies

# 소스 코드 복사 (src 폴더 확인됨)
COPY src ./src

# Gradle로 프로젝트 빌드 (테스트는 스킵)
# gradlew에 실행 권한 부여
RUN chmod +x ./gradlew
RUN ./gradlew build -x test

# --- 2단계: 서빙(Serve) 환경 ---
# Java 11 JRE(실행 환경) 이미지를 사용합니다. (JDK보다 훨씬 가벼움)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 1단계(builder)에서 빌드된 .jar 파일을 복사해옵니다.
# /build/libs/ 안에 있는 .jar 파일을 app.jar 라는 이름으로 복사합니다.
COPY --from=builder /workspace/app/build/libs/*.jar app.jar

# 스프링 부트의 기본 포트 8080 노출
EXPOSE 8080

# 컨테이너 시작 시 .jar 파일 실행
ENTRYPOINT ["java","-jar","/app/app.jar"]

