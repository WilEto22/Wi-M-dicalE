#!/bin/bash

echo "=========================================="
echo "Docker Diagnostics for CrudApp"
echo "=========================================="
echo ""

echo "1. Checking running containers..."
docker ps -a
echo ""

echo "2. Checking Spring Boot container logs..."
docker logs crudapp-backend --tail 100
echo ""

echo "3. Checking MySQL container logs..."
docker logs crudapp-mysql --tail 50
echo ""

echo "4. Checking container health..."
docker inspect --format='{{.State.Health.Status}}' crudapp-backend
echo ""

echo "5. Checking if Spring Boot is responding..."
curl -v http://localhost:8080/actuator/health || echo "Spring Boot not responding"
echo ""

echo "6. Checking MySQL connection..."
docker exec crudapp-mysql mysql -u crudapp_user -pcrudapp_password -e "SELECT 1" crudapp_db || echo "MySQL connection failed"
echo ""

echo "=========================================="
echo "Diagnostics complete"
echo "=========================================="
