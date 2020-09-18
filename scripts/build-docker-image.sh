#!/usr/bin/env sh

morpherSystemPath=${1:-./data/morpher-system.pb}
morpherApiVersion=$(grep "morpherApiVersion" gradle.properties | cut -d"=" -f2)
dockerImageVersion=${2:-${morpherApiVersion}}

docker build \
    --build-arg morpherSystemPath=$morpherSystemPath \
    --build-arg morpherApiVersion=$morpherApiVersion \
    -t morpher-api \
    .
docker tag morpher-api szgabsz91/morpher-api:$dockerImageVersion

echo "$DOCKERHUB_PASSWORD" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin
docker push szgabsz91/morpher-api:$dockerImageVersion
