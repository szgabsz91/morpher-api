#!/usr/bin/env sh

# Variables
imageName=morpher-api

# Stop, delete container and image
docker rm $(docker stop $(docker ps -a -q --filter="ancestor=$imageName"))
docker rmi $imageName
