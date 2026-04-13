#!/bin/bash
echo "Введіть число клієнтів"
while true; do
  read num

  if [[ "$num" =~ ^[1-9][0-9]*$ ]]; then
    break
  else
    echo "Введіть ціле додатнє число"
  fi
done

IMAGE_NAME="messenger"
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

docker compose -f ./container/docker-compose.yaml up --scale frontend=$num -d

echo "Для завершення роботи введіть 1:"
while true; do
    read ans

    if [[ "$ans" == 1 ]]; then
      docker compose -f ./container/docker-compose.yaml down
      break
    else
      echo "Незрозумілий символ, повторіть ще раз"
    fi
done