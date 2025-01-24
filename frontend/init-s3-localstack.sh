#!/bin/sh

# Define the build folder path
build_folder="./build"

# Define the S3 bucket name
bucket_name="nexus-net-frontend"

# Create the S3 bucket
awslocal --endpoint-url=http://localstack:4566 s3 mb s3://$bucket_name

# Sync contents of the build folder to the S3 bucket
awslocal --endpoint-url=http://localstack:4566 s3 sync "$build_folder" s3://$bucket_name

# List the contents of the S3 bucket
awslocal --endpoint-url=http://localstack:4566 s3 ls s3://$bucket_name

# URL of the website for S3 bucket
website_url="https://app.localstack.cloud/inst/default/resources/s3"

# Message to display
message="Check your S3 bucket resources here:"

# Print the message and URL to the console
echo "$message $website_url"
