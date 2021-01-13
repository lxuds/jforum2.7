#!/bin/bash

set -e
set -o
set -x

WEB_INFO_DIR=/Users/lxu/program/apache-tomcat-9.0.36/webapps/jforum/WEB-INF
INDEX_DIR=${WEB_INFO_DIR}/jforumLuceneIndex

echo "Deploy jforum"

echo "1) Shutdown webserver..."
./shutdown.sh

echo "2) Preserve jformuLuceneIndex..."
rm -rf /tmp/jforumindex
cp -r ${INDEX_DIR} /tmp/jforumindex

echo "3) Install new webserver package"
cp /Users/lxu/project/jforum2.7/target/jforum.war /Users/lxu/program/apache-tomcat-9.0.36/webapps

echo "4) Start webserver..."
./startup.sh

sleep 5

echo "5) Restore jforumLuceneIndex..."
cp -r /tmp/jforumindex ${INDEX_DIR}

echo "6) Restart webserver"
./shutdown.sh
sleep 1
./startup.sh
