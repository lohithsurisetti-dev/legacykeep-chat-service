#!/bin/bash

# Chat Service Database Setup Script

echo "Setting up Chat Service Database..."

# Create PostgreSQL database
psql -U postgres -c "CREATE DATABASE chat_db;" 2>/dev/null || echo "Database chat_db already exists"

# Create MongoDB database (will be created automatically when first document is inserted)
echo "MongoDB database will be created automatically"

echo "Database setup completed!"
echo "PostgreSQL: chat_db"
echo "MongoDB: chat_messages"
