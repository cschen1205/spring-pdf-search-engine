#!/usr/bin/env bash

mvn -f pom.xml clean package -Plocal -U

cp /target/pdf-search-engine.jar pdf-search-engine.jar
