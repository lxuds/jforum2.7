#!/bin/bash

set -e
set -o
set -x

WEB_INFO_DIR=/Users/lxu/program/apache-tomcat-9.0.36/webapps/jforum/WEB-INF
INDEX_DIR=${WEB_INFO_DIR}/jforumLuceneIndex
APACHE_BIN=/Users/lxu/program/apache-tomcat-9.0.36/bin

echo "Deploy jforum"

echo "1) Shutdown webserver..."
${APACHE_BIN}/shutdown.sh

echo "2) Preserve jformuLuceneIndex..."
rm -rf /tmp/jforumindex
cp -r ${INDEX_DIR} /tmp/jforumindex

echo "3) Install new webserver package"
cp /Users/lxu/project/jforum2.7/target/jforum.war /Users/lxu/program/apache-tomcat-9.0.36/webapps


echo "4) Start webserver..."
${APACHE_BIN}/startup.sh

sleep 8

echo "5) Restore jforumLuceneIndex..."
cp -r /tmp/jforumindex ${INDEX_DIR}

sleep 8
echo "6) Restart webserver"
${APACHE_BIN}/shutdown.sh
sleep 3
${APACHE_BIN}/startup.sh

sleep 10
echo "7) Restart webserver"
${APACHE_BIN}/shutdown.sh
sleep 3
${APACHE_BIN}/startup.sh


