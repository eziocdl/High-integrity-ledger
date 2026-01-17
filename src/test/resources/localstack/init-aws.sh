#!/bin/bash
echo " Initializing LocalStack services..."

# 1. Secrets Manager
echo " Creating secrets in Secrets Manager..."
awslocal secretsmanager create-secret \
    --name ledger_db_secret \
    --description "Database Credentials" \
    --secret-string '{"username":"admin","password":"secret","host":"postgres","port":"5432","dbname":"ledger_db"}'

# 2. S3 (Simple Storage Service)
echo " Creating S3 bucket..."
awslocal s3 mb s3://ledger-artifacts

# 3. SQS (Simple Queue Service)
echo " Creating SQS queue..."
awslocal sqs create-queue --queue-name ledger-events

echo " All infrastructure components initialized successfully!"