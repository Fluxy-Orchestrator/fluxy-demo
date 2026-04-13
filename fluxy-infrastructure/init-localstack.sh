#!/bin/bash
set -euo pipefail

echo "==> Creando cola SQS: fluxy-events"
awslocal sqs create-queue --queue-name fluxy-events --region us-east-1
echo "==> Cola fluxy-events creada correctamente"

