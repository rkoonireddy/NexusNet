#!/bin/bash

# Create a Lambda function for a Spring Boot application
awslocal --endpoint-url=http://localstack:4566 lambda create-function --function-name usermanager-lambda \
  --handler ch.nexusnet.usermanager.aws.LambdaHandler::handleRequest \
  --runtime java17 \
  --role arn:aws:iam::123456789012:role/execution_role \
  --package-type Image \
  --code ImageUri=usermanager

# URL of the website
website_url="https://app.localstack.cloud/inst/default/resources/lambda/functions"

# Message to display
message="Check your Lambda Functions here:"

# Print the message and URL to the console
echo "$message $website_url"