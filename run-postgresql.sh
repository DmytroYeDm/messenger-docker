#!/bin/bash

DB_CONTAINER_NAME="messenger-db"

docker stop $DB_CONTAINER_NAME 2>/dev/null
docker rm $DB_CONTAINER_NAME 2>/dev/null

docker run -d --name $DB_CONTAINER_NAME -e POSTGRES_DB=messengerDB \
  -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=whyNot_enot17 \
  -e TZ=Europe/Kyiv -v messenger_db_data:/var/lib/postgresql -p 5433:5432 \
  postgres:latest