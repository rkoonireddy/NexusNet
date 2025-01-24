#!/bin/sh
awslocal dynamodb create-table \
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

awslocal dynamodb create-table \
    --table-name Follow \
    --key-schema AttributeName=id,KeyType=HASH \
    --attribute-definitions AttributeName=id,AttributeType=S \
    --billing-mode PAY_PER_REQUEST

awslocal dynamodb list-tables

# URL of the website
website_url="https://app.localstack.cloud/inst/default/resources/dynamodb"

# Message to display
message="Check your DynamoDB resources here:"

# Print the message and URL to the console
echo "$message $website_url"

awslocal dynamodb put-item --table-name UserInfo --item '{
    "id": {"S": "8abed1fa-f9dc-4219-afd6-48fb8207c871"},
    "username": {"S": "TestUser"},
    "firstName": {"S": "Test"},
    "lastName": {"S": "User"},
    "birthday": {"S": "1995-06-01"},
    "bio": {"S": "Test user to demonstrate the functionalites of the app."},
    "university": {"S": "UZH"},
    "degreeProgram": {"S": "Informatics"}
}'

awslocal dynamodb put-item --table-name UserInfo --item '{
    "id": {"S": "199588ed-5be5-4738-a55d-bd6771c40893"},
    "username": {"S": "CreativeHelper"},
    "firstName": {"S": "Jamie"},
    "lastName": {"S": "Wallace"},
    "birthday": {"S": "1995-08-15"},
    "bio": {"S": "Enthusiastic about community service and support."},
    "university": {"S": "Tech University"},
    "degreeProgram": {"S": "Social Work"}
}'

awslocal dynamodb put-item --table-name UserInfo --item '{
    "id": {"S": "65cd4311-4375-4882-9c18-6e7064cfda52"},
    "username": {"S": "StarSupporter"},
    "firstName": {"S": "Chris"},
    "lastName": {"S": "Fernandez"},
    "birthday": {"S": "1990-03-09"},
    "bio": {"S": "Always looking to help out where I can with a positive spirit."},
    "university": {"S": "Greenfield College"},
    "degreeProgram": {"S": "Business Administration"}
}'

awslocal dynamodb put-item --table-name UserInfo --item '{
    "id": {"S": "fd2fda32-3ba4-41d9-aac1-3f0f81b23a1"},
    "username": {"S": "PhysicsPal"},
    "firstName": {"S": "Morgan"},
    "lastName": {"S": "Taylor"},
    "birthday": {"S": "1992-11-17"},
    "bio": {"S": "Physics enthusiast and aspiring astrophysicist."},
    "university": {"S": "State University"},
    "degreeProgram": {"S": "Physics"}
}'

awslocal dynamodb put-item --table-name UserInfo --item '{
    "id": {"S": "ea903c75-f6a2-4497-8cd7-f82bd7147998"},
    "username": {"S": "GreenLeader"},
    "firstName": {"S": "Taylor"},
    "lastName": {"S": "Brooks"},
    "birthday": {"S": "1994-07-20"},
    "bio": {"S": "Committed to making the world a cleaner, better place."},
    "university": {"S": "Global University"},
    "degreeProgram": {"S": "Environmental Science"}
}'

awslocal dynamodb put-item --table-name UserInfo --item '{
    "id": {"S": "006c6621-22fe-4556-ac3d-6cbcade5a474"},
    "username": {"S": "StudyBuddy"},
    "firstName": {"S": "Pat"},
    "lastName": {"S": "Lee"},
    "birthday": {"S": "1991-09-28"},
    "bio": {"S": "Dedicated to learning and helping others achieve their academic goals."},
    "university": {"S": "University of Excellence"},
    "degreeProgram": {"S": "Engineering"}
}'

awslocal dynamodb put-item --table-name UserInfo --item '{
    "id": {"S": "d3f0e8ee-a486-467a-8385-7c27966a37a"},
    "username": {"S": "EcoWarrior"},
    "firstName": {"S": "Casey"},
    "lastName": {"S": "Smith"},
    "birthday": {"S": "1989-02-14"},
    "bio": {"S": "Activist ready to fight for our planet every day."},
    "university": {"S": "Planet Earth University"},
    "degreeProgram": {"S": "Environmental Policy"}
}'

awslocal dynamodb put-item --table-name UserInfo --item '{
    "id": {"S": "67e60439-9c76-4320-93a1-4fa04fb125e9"},
    "username": {"S": "PhysicsFan"},
    "firstName": {"S": "Robin"},
    "lastName": {"S": "Peterson"},
    "birthday": {"S": "1996-10-05"},
    "bio": {"S": "Physics lover and curious mind exploring the mysteries of the universe."},
    "university": {"S": "Institute of Science"},
    "degreeProgram": {"S": "Applied Physics"}
}'

awslocal dynamodb put-item --table-name UserInfo --item '{
    "id": {"S": "ef432683-6757-4f31-97ae-5df8a75ee00e"},
    "username": {"S": "CampusHero"},
    "firstName": {"S": "Jordan"},
    "lastName": {"S": "Baker"},
    "birthday": {"S": "1993-12-23"},
    "bio": {"S": "Committed to campus welfare and student life improvements."},
    "university": {"S": "Central University"},
    "degreeProgram": {"S": "Public Relations"}
}'

awslocal dynamodb put-item --table-name UserInfo --item '{
    "id": {"S": "6716a6a9-ec36-4c8a-bb53-3147f514ed33"},
    "username": {"S": "TechEnthusiast"},
    "firstName": {"S": "Sam"},
    "lastName": {"S": "Kim"},
    "birthday": {"S": "1998-04-01"},
    "bio": {"S": "Tech geek exploring the future of technology and its impact on society."},
    "university": {"S": "Tech Hub University"},
    "degreeProgram": {"S": "Computer Science"}
}'

awslocal dynamodb put-item --table-name Follow --item '{
    "userId": {"S": "8abed1fa-f9dc-4219-afd6-48fb8207c87"},
    "followsUserId": {"S": "199588ed-5be5-4738-a55d-bd6771c40893"}
}'

awslocal dynamodb put-item --table-name Follow --item '{
    "userId": {"S": "8abed1fa-f9dc-4219-afd6-48fb8207c87"},
    "followsUserId": {"S": "65cd4311-4375-4882-9c18-6e7064cfda52"}
}'

awslocal dynamodb put-item --table-name Follow --item '{
    "userId": {"S": "199588ed-5be5-4738-a55d-bd6771c40893"},
    "followsUserId": {"S": "8abed1fa-f9dc-4219-afd6-48fb8207c87"}
}'

awslocal dynamodb put-item --table-name Follow --item '{
    "userId": {"S": "65cd4311-4375-4882-9c18-6e7064cfda52"},
    "followsUserId": {"S": "fd2fda32-3ba4-41d9-aac1-3f0f81b23a1e"}
}'

awslocal dynamodb put-item --table-name Follow --item '{
    "userId": {"S": "ea903c75-f6a2-4497-8cd7-f82bd714799"},
    "followsUserId": {"S": "006c6621-22fe-4556-ac3d-6cbcade5a474"}
}'

awslocal dynamodb put-item --table-name Follow --item '{
    "userId": {"S": "fd2fda32-3ba4-41d9-aac1-3f0f81b23a1e"},
    "followsUserId": {"S": "8abed1fa-f9dc-4219-afd6-48fb8207c87"}
}'

awslocal dynamodb put-item --table-name Follow --item '{
    "userId": {"S": "fd2fda32-3ba4-41d9-aac1-3f0f81b23a1e"},
    "followsUserId": {"S": "d3f0e8ee-a486-467a-8385-7c27966a37a2"}
}'

awslocal dynamodb put-item --table-name Follow --item '{
    "userId": {"S": "006c6621-22fe-4556-ac3d-6cbcade5a474"},
    "followsUserId": {"S": "ea903c75-f6a2-4497-8cd7-f82bd714799"}
}'

awslocal dynamodb put-item --table-name Follow --item '{
    "userId": {"S": "d3f0e8ee-a486-467a-8385-7c27966a37a2"},
    "followsUserId": {"S": "fd2fda32-3ba4-41d9-aac1-3f0f81b23a1e"}
}'

awslocal dynamodb put-item --table-name Follow --item '{
    "userId": {"S": "67e60439-9c76-4320-93a1-4fa04fb125e9"},
    "followsUserId": {"S": "ea903c75-f6a2-4497-8cd7-f82bd714799"}
}'

awslocal dynamodb put-item --table-name Follow --item '{
    "userId": {"S": "ef432683-6757-4f31-97ae-5df8a75ee00e"},
    "followsUserId": {"S": "65cd4311-4375-4882-9c18-6e7064cfda52"}
}'

awslocal dynamodb put-item --table-name Follow --item '{
    "userId": {"S": "6716a6a9-ec36-4c8a-bb53-3147f514ed33"},
    "followsUserId": {"S": "67e60439-9c76-4320-93a1-4fa04fb125e9"}
}'
