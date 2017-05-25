#!/usr/bin/env bash

echo "Downloading Language Identifcation Model. 161.7MB From Columbia University"
wget -O src/main/resources/completeModel3.gm http://www.cs.columbia.edu/~gm2597/completeModel3.gm
cp src/main/resources/completeModel3.gm src/test/resources/completeModel3.gm

echo "Installing Maven Dependencies and Building JAR"


mvn install:install-file -Dfile=lib/azure-bing-search-java-0.12.0.jar -DgroupId=net.billylieurance.azuresearch -DartifactId=azure-bing-search-java -Dversion=0.12.0 -Dpackaging=jar


mvn install:install-file -Dfile=lib/diffbot-java-1.0-SNAPSHOT.jar -DgroupId=diffbot -DartifactId=diffbot-java -Dversion=1.0-SNAPSHOT -Dpackaging=jar

mvn install:install-file -Dfile=lib/feed4j.jar -DgroupId=feed4j -DartifactId=feed4j -Dversion=1.0 -Dpackaging=jar

mvn clean install 
