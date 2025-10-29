# --- 1단계: 빌드(Build) 환경 ---
FROM node:18-alpine AS builder

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install

COPY . .
RUN npm run build

# --- 2단계: 서빙(Serve) 환경 ---
FROM nginx:alpine

# Nginx 기본 설정 파일 삭제
RUN rm /etc/nginx/conf.d/default.conf

# 1단계에서 빌드된 결과물을 Nginx 웹 루트로 복사
COPY --from=builder /app/build /usr/share/nginx/html

# 우리가 만든 Nginx 설정을 복사
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
