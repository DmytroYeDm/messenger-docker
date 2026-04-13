#!/bin/bash
IMAGE_NAME="messenger"
SERVER_CONTAINER_NAME="messenger-server"

docker stop $SERVER_CONTAINER_NAME 2>/dev/null
docker rm $SERVER_CONTAINER_NAME 2>/dev/null

echo "Чи потрібно перезбирати образ (Yes(Y)/No(N))"
while true; do
  read res

  case "$res" in
    [Yy]* )
      docker rmi $IMAGE_NAME 2>/dev/null
      docker build -t $IMAGE_NAME ./container
      break
      ;;
    [Nn]* )
      break
      ;;
    * )
      echo "Будь ласка, введіть Yes(Y) або No(N)."
      ;;
  esac
done

docker run -d --name $SERVER_CONTAINER_NAME --env-file .env \
  -e DISPLAY=172.31.80.1:0.0 -p 8080:8080 $IMAGE_NAME
