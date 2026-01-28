#!/bin/bash

# ================================
# CrudApp Medical - Database Backup Script
# ================================

set -e

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Configuration
BACKUP_DIR="./docker/mysql/backup"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="crudapp_backup_${TIMESTAMP}.sql"

# Load environment variables
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

MYSQL_CONTAINER=${MYSQL_CONTAINER:-crudapp-mysql}
MYSQL_DATABASE=${MYSQL_DATABASE:-crudapp_db}
MYSQL_USER=${MYSQL_USER:-crudapp_user}
MYSQL_PASSWORD=${MYSQL_PASSWORD:-crudapp_password}

# Create backup directory if it doesn't exist
mkdir -p $BACKUP_DIR

print_info "Starting database backup..."

# Perform backup
docker exec $MYSQL_CONTAINER mysqldump \
    -u $MYSQL_USER \
    -p$MYSQL_PASSWORD \
    $MYSQL_DATABASE > "$BACKUP_DIR/$BACKUP_FILE"

if [ $? -eq 0 ]; then
    print_info "Backup completed successfully: $BACKUP_FILE"

    # Compress backup
    gzip "$BACKUP_DIR/$BACKUP_FILE"
    print_info "Backup compressed: ${BACKUP_FILE}.gz"

    # Keep only last 7 backups
    print_info "Cleaning old backups (keeping last 7)..."
    cd $BACKUP_DIR
    ls -t crudapp_backup_*.sql.gz | tail -n +8 | xargs -r rm

    print_info "Backup process completed!"
else
    print_error "Backup failed!"
    exit 1
fi
