#!/bin/bash

# ================================
# CrudApp Medical - Database Restore Script
# ================================

set -e

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if backup file is provided
if [ -z "$1" ]; then
    print_error "Usage: ./restore.sh <backup_file.sql.gz>"
    print_info "Available backups:"
    ls -lh ./docker/mysql/backup/crudapp_backup_*.sql.gz 2>/dev/null || echo "No backups found"
    exit 1
fi

BACKUP_FILE=$1

# Check if file exists
if [ ! -f "$BACKUP_FILE" ]; then
    print_error "Backup file not found: $BACKUP_FILE"
    exit 1
fi

# Load environment variables
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

MYSQL_CONTAINER=${MYSQL_CONTAINER:-crudapp-mysql}
MYSQL_DATABASE=${MYSQL_DATABASE:-crudapp_db}
MYSQL_USER=${MYSQL_USER:-crudapp_user}
MYSQL_PASSWORD=${MYSQL_PASSWORD:-crudapp_password}

print_warning "This will restore the database from: $BACKUP_FILE"
print_warning "All current data will be lost!"
read -p "Are you sure? (yes/no): " CONFIRM

if [ "$CONFIRM" != "yes" ]; then
    print_info "Restore cancelled."
    exit 0
fi

print_info "Starting database restore..."

# Decompress if needed
if [[ $BACKUP_FILE == *.gz ]]; then
    print_info "Decompressing backup..."
    gunzip -c $BACKUP_FILE | docker exec -i $MYSQL_CONTAINER mysql \
        -u $MYSQL_USER \
        -p$MYSQL_PASSWORD \
        $MYSQL_DATABASE
else
    docker exec -i $MYSQL_CONTAINER mysql \
        -u $MYSQL_USER \
        -p$MYSQL_PASSWORD \
        $MYSQL_DATABASE < $BACKUP_FILE
fi

if [ $? -eq 0 ]; then
    print_info "Database restored successfully!"
else
    print_error "Restore failed!"
    exit 1
fi
