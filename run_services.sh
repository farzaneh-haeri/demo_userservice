#!/bin/bash

cd "$(dirname "$0")" || exit
docker-compose build
docker-compose up