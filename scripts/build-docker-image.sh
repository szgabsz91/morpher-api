#!/usr/bin/env sh

morpherApiVersion=$(grep "morpherApiVersion" gradle.properties | cut -d"=" -f2)
dockerImageVersion=${1:-${morpherApiVersion}}

docker build --build-arg morpherApiVersion=$morpherApiVersion -t morpher-api .
docker tag morpher-api szgabsz91/morpher-api:$dockerImageVersion

echo "$DOCKERHUB_PASSWORD" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin
docker push szgabsz91/morpher-api:$dockerImageVersion
