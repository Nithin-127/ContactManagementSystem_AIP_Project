#!/bin/bash

cd ~/Desktop/AIP_Project/ContactManagementSystem || exit 1

echo "Building project..."
mvn clean package || exit 1

cp target/ContactManagementSystem.war ~/apache-tomcat-11.0.27/webapps/

if ! lsof -i :8080 >/dev/null 2>&1; then
    ~/apache-tomcat-11.0.27/bin/startup.sh
fi

echo "Opening Contact Management System..."
open http://localhost:8080/ContactManagementSystem/


