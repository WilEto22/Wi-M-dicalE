#!/bin/bash

# ================================
# CrudApp Medical - Deployment Script
# ================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Functions
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if environment is provided
if [ -z "$1" ]; then
    print_error "Usage: ./deploy.sh [dev|staging|prod]"
    exit 1
fi

ENVIRONMENT=$1

print_info "Starting deployment for environment: $ENVIRONMENT"

# Validate environment
case $ENVIRONMENT in
    dev)
        COMPOSE_FILE="docker-compose.yml"
        ;;
    staging)
        COMPOSE_FILE="docker-compose.yml"
        export SPRING_PROFILES_ACTIVE=docker
        ;;
    prod)
        COMPOSE_FILE="docker-compose.prod.yml"
        export SPRING_PROFILES_ACTIVE=prod
        ;;
    *)
        print_error "Invalid environment. Use: dev, staging, or prod"
        exit 1
        ;;
esac

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker Desktop."
    exit 1
fi

# Load environment variables
if [ -f .env ]; then
    print_info "Loading environment variables from .env"
    export $(cat .env | grep -v '^#' | xargs)
else
    print_warning ".env file not found. Using default values."
fi

# Pull latest images (for production)
if [ "$ENVIRONMENT" == "prod" ]; then
    print_info "Pulling latest Docker images..."
    docker-compose -f $COMPOSE_FILE pull
fi

# Stop existing containers
print_info "Stopping existing containers..."
docker-compose -f $COMPOSE_FILE down

# Build and start containers
print_info "Building and starting containers..."
if [ "$ENVIRONMENT" == "dev" ]; then
    docker-compose -f $COMPOSE_FILE up --build -d
else
    docker-compose -f $COMPOSE_FILE up -d
fi

# Wait for services to be healthy
print_info "Waiting for services to be healthy..."
sleep 10

# Check health
print_info "Checking service health..."
docker-compose -f $COMPOSE_FILE ps

# Show logs
print_info "Showing recent logs..."
docker-compose -f $COMPOSE_FILE logs --tail=50

print_info "Deployment completed successfully!"
print_info "Application URL: http://localhost:8080"
print_info "Swagger UI: http://localhost:8080/swagger-ui.html"
if [ "$ENVIRONMENT" == "dev" ]; then
    print_info "MailHog UI: http://localhost:8025"
fi
print_info "Actuator Health: http://localhost:8080/actuator/health"
