#!/bin/bash

./gradlew jacocoTestReport
bash <(curl -Ls https://coverage.codacy.com/get.sh) report -r build/reports/jacoco/test/jacocoTestReport.xml