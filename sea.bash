#!/bin/bash

./gradlew build
./gradlew run --args="-d ../test ../test/file.sea"
cat test/file.seadebug
