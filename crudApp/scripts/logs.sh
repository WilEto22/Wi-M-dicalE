#!/bin/bash

# ================================
# CrudApp Medical - Logs Viewer Script
# ================================

# Colors
GREEN='\033[0;32m'
NC='\033[0m'

print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

# Default values
SERVICE=${1:-app}
LINES=${2:-100}

print_info "Showing logs for service: $SERVICE (last $LINES lines)"
print_info "Press Ctrl+C to exit"
echo ""

docker-compose logs -f --tail=$LINES $SERVICE
