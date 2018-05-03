#!/usr/bin/env bash
pm2 stop auth
pm2 delete auth
./gradlew clean
./gradlew stage
pm2 start pm2.config.js