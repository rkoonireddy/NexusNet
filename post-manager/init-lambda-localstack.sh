#!/bin/bash

# Create a Lambda function for a Spring Boot application
awslocal --endpoint-url=http://localhost:4566 lambda create-function --function-name postmanager-lambda \
  --handler ch.nexusnet.postmanager.aws.LambdaHandler::handleRequest \
  --runtime java17 \
  --role arn:aws:iam::123456789012:role/execution_role \
  --package-type Image \
  --code ImageUri=postmanager
