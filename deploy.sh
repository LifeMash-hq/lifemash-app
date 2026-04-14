#!/usr/bin/env bash
set -e

REMOTE_USER="lifemash"
REMOTE_HOST="192.168.35.116"
REMOTE_SSH="ssh -i ~/.ssh/id_ed25519 ${REMOTE_USER}@${REMOTE_HOST}"
IMAGE_NAME="lifemash-server"

# 사용법: ./deploy.sh [prod|dev]  (기본값: prod)
ENV="${1:-prod}"

if [ "$ENV" = "dev" ]; then
  CONTAINER_NAME="lifemash-server-dev"
  HOST_PORT=8081
  DOMAIN="dev.lifemash.app"
else
  CONTAINER_NAME="lifemash-server"
  HOST_PORT=8080
  DOMAIN="lifemash.app"
fi

echo "▶ 배포 대상: ${DOMAIN} (${CONTAINER_NAME})"

echo "▶ 1/3  이미지 빌드 중..."
docker build \
  --platform linux/arm64 \
  -t ${IMAGE_NAME} \
  -f core/ktor/Dockerfile \
  .

echo "▶ 2/3  맥미니로 이미지 전송 중..."
docker save ${IMAGE_NAME} \
  | ssh -i ~/.ssh/id_ed25519 ${REMOTE_USER}@${REMOTE_HOST} '/usr/local/bin/docker load'

echo "▶ 3/3  맥미니에서 컨테이너 재시작 중..."
${REMOTE_SSH} "
  /usr/local/bin/docker stop ${CONTAINER_NAME} 2>/dev/null || true
  /usr/local/bin/docker rm   ${CONTAINER_NAME} 2>/dev/null || true
  /usr/local/bin/docker run -d \
    --name ${CONTAINER_NAME} \
    --restart always \
    --env-file ~/lifemash.env \
    --add-host host.docker.internal:host-gateway \
    -p ${HOST_PORT}:8080 \
    ${IMAGE_NAME}
  /usr/local/bin/docker ps --filter name=${CONTAINER_NAME}
"

echo "✓ 배포 완료 → https://${DOMAIN}"
