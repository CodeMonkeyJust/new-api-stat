# syntax=docker/dockerfile:1
# 多阶段构建：Node 打包前端 -> nginx 托管静态资源并反向代理后端 API。
# 构建机无需安装 Node/npm；可通过 --build-arg 覆盖 VITE_BASE_PATH / VITE_API_BASE_URL
# （默认与 frontend/nginx.conf 的 location 前缀一致）。
FROM node:22-alpine AS build
WORKDIR /app

ARG VITE_BASE_PATH=/new-api-stat/
ARG VITE_API_BASE_URL=/new-api-stat-api/api
ENV VITE_BASE_PATH=$VITE_BASE_PATH
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

FROM nginx:stable-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/new-api-stat /usr/share/nginx/html
EXPOSE 80
