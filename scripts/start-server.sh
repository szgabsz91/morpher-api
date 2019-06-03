#!/usr/bin/env sh

# Variables
imageName=morpher-api
port=${1:-8080}

# Build container
docker build -t "$imageName" .

# Run container
docker run -p $port:8080 -t $imageName
