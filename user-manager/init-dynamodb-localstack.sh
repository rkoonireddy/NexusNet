#!/bin/sh
awslocal --endpoint-url=http://localstack:4566 dynamodb create-table \
    --table-name UserInfo \
    --key-schema 'AttributeName=id,KeyType=HASH' \
    --attribute-definitions 'AttributeName=id,AttributeType=S' 'AttributeName=username,AttributeType=S' \
    --billing-mode PAY_PER_REQUEST \
    --global-secondary-indexes '[
        {
            "IndexName": "UsernameIndex",
            "KeySchema": [{"AttributeName": "username", "KeyType": "HASH"}],
            "Projection": {"ProjectionType": "ALL"}
        }
    ]'

awslocal --endpoint-url=http://localstack:4566 dynamodb create-table \
    --table-name Follow \
    --key-schema 'AttributeName=id,KeyType=HASH' \
    --attribute-definitions 'AttributeName=id,AttributeType=S' \
    --billing-mode PAY_PER_REQUEST

awslocal --endpoint-url=http://localstack:4566 dynamodb list-tables

# URL of the website
website_url="https://app.localstack.cloud/inst/default/resources/dynamodb"

# Message to display
message="Check your DynamoDB resources here:"

# Print the message and URL to the console
echo "$message $website_url"




