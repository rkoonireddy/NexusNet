#!/bin/bash

awslocal dynamodb create-table --table-name Posts --attribute-definitions AttributeName=id,AttributeType=S --key-schema AttributeName=id,KeyType=HASH --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "POST-1"},
    "authorId": {"S": "AlexTheAdvocate"},
    "type": {"S": "PROJECT"},
    "status": {"S": "NEW"},
    "title": {"S": "Enhancing Mental Health Awareness"},
    "shortDescription": {"S": "Join me to improve mental health resources for students."},
    "description": {"S": "Looking for team members to build a mental health app."},
    "likeNumber": {"N": "0"},
    "commentNumber": {"N": "0"},
    "hashtags": {"L":  [{ "S" : "mentalhealth" }, { "S" : "startup" }, { "S" : "collaboration" }]},
    "createdDateTime": {"S": "2024-04-10T12:10:10Z"},
    "edited": {"BOOL": false}
}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "COMMENT-1"},
    "postId": {"S": "POST-1"},
    "authorId": {"S": "CreativeHelper"},
    "content": {"S": "This is a great initiative, Alex! I am interested in helping."},
    "createdDateTime": {"S": "2024-04-10T13:00:00Z"},
    "likeNumber": {"N": "0"}
}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "LIKE-1"},
    "targetType": {"S": "POST"},
    "targetId": {"S": "POST-1"},
    "userId": {"S": "StarSupporter"},
    "timestamp": {"S": "2024-04-10T13:05:00Z"}
}'

awslocal dynamodb update-item --table-name Posts --key '{"id": {"S": "POST-1"}}' --update-expression "SET likeNumber = likeNumber + :val1, commentNumber = commentNumber + :val2" --expression-attribute-values '{":val1":{"N":"1"}, ":val2":{"N":"1"}}'


awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "POST-2"},
    "authorId": {"S": "PhysicsPal"},
    "type": {"S": "POST"},
    "status": {"S": "NEW"},
    "title": {"S": "Looking for Physics Study Group Members"},
    "shortDescription": {"S": "Anyone interested in forming a study group for the upcoming Physics exam?"},
    "description": {"S": "Planning to meet twice a week to discuss topics and solve problems."},
    "likeNumber": {"N": "0"},
    "commentNumber": {"N": "0"},
    "hashtags": {"L": [{"S":"physics"}, {"S":"studygroup"}, {"S":"examprep"}]},
    "createdDateTime": {"S": "2024-04-11T09:42:25Z"},
    "edited": {"BOOL": false}
}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "COMMENT-2"},
    "postId": {"S": "POST-2"},
    "authorId": {"S": "StudyBuddy"},
    "content": {"S": "Count me in! Struggling with the latest chapters."},
    "createdDateTime": {"S": "2024-04-11T10:30:00Z"},
    "likeNumber": {"N": "0"}
}'


awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "POST-3"},
    "authorId": {"S": "GreenLeader"},
    "type": {"S": "POST"},
    "status": {"S": "NEW"},
    "title": {"S": "Join Our Campus Clean-Up Drive"},
    "shortDescription": {"S": "Let us make our campus greener and cleaner together!"},
    "description": {"S": "Meeting point at the main library, supplies will be provided."},
    "likeNumber": {"N": "0"},
    "commentNumber": {"N": "0"},
    "hashtags": {"L": [{"S": "environment"}, {"S": "cleanup"}, {"S": "volunteering"}]},
    "createdDateTime": {"S": "2024-04-12T08:30:10Z"},
    "edited": {"BOOL": false}
}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "COMMENT-3"},
    "postId": {"S": "POST-3"},
    "authorId": {"S": "EcoWarrior"},
    "content": {"S": "Great initiative! I will be there and bring a couple of friends."},
    "createdDateTime": {"S": "2024-04-12T09:15:00Z"},
    "likeNumber": {"N": "0"}
}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "LIKE-2"},
    "targetType": {"S": "post"},
    "targetId": {"S": "POST2"},
    "userId": {"S": "user8"},
    "timestamp": {"S": "2024-04-11T11:00:00Z"}
}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "LIKE-3"},
    "targetType": {"S": "comment"},
    "targetId": {"S": "COMMENT-3"},
    "userId": {"S": "CampusHero"},
    "timestamp": {"S": "2024-04-12T09:30:00Z"}
}'

awslocal dynamodb update-item --table-name Posts --key '{"id": {"S": "POST2"}}' --update-expression "SET likeNumber = likeNumber + :val1, commentNumber = commentNumber + :val1" --expression-attribute-values '{":val1":{"N":"1"}}'

awslocal dynamodb update-item --table-name Posts --key '{"id": {"S": "POST3"}}' --update-expression "SET likeNumber = likeNumber + :val1, commentNumber = commentNumber + :val1" --expression-attribute-values '{":val1":{"N":"1"}}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "LIKE-4"},
    "targetType": {"S": "comment"},
    "targetId": {"S": "COMMENT-1"},
    "userId": {"S": "TechEnthusiast"},
    "timestamp": {"S": "2024-04-10T14:00:00Z"}
}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "LIKE-5"},
    "targetType": {"S": "comment"},
    "targetId": {"S": "COMMENT-2"},
    "userId": {"S": "QuantumLearner"},
    "timestamp": {"S": "2024-04-11T12:00:00Z"}
}'

awslocal dynamodb update-item --table-name Posts --key '{"id": {"S": "COMMENT-1"}}' --update-expression "SET likeNumber = likeNumber + :val" --expression-attribute-values '{":val":{"N":"1"}}'
awslocal dynamodb update-item --table-name Posts --key '{"id": {"S": "COMMENT-2"}}' --update-expression "SET likeNumber = likeNumber + :val" --expression-attribute-values '{":val":{"N":"1"}}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "comment4"},
    "postId": {"S": "POST3"},
    "authorId": {"S": "user13"},
    "content": {"S": "Super excited about this event! I have always been passionate about environmental causes."},
    "createdDateTime": {"S": "2024-04-12T15:00:00Z"},
    "likeNumber": {"N": "0"}
}'

awslocal dynamodb update-item --table-name Posts --key '{"id": {"S": "POST3"}}' --update-expression "SET commentNumber = commentNumber + :val" --expression-attribute-values '{":val":{"N":"1"}}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "POST-7"},
    "authorId": {"S": "DataDemystifier"},
    "type": {"S": "POST"},
    "status": {"S": "NEW"},
    "title": {"S": "Workshop on Data Science Basics"},
    "shortDescription": {"S": "Learn the basics of data science and machine learning."},
    "description": {"S": "This workshop is intended for beginners and will cover fundamental concepts and hands-on activities."},
    "likeNumber": {"N": "0"},
    "commentNumber": {"N": "0"},
    "hashtags": {"L": [{"S": "datascience"}, {"S": "education"}, {"S": "workshop"}]},
    "createdDateTime": {"S": "2024-04-17T14:50:14Z"},
    "edited": {"BOOL": false}
}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "LIKE-6"},
    "targetType": {"S": "post"},
    "targetId": {"S": "POST-7"},
    "userId": {"S": "AnalyticalMind"},
    "timestamp": {"S": "2024-04-11T11:00:00Z"}
}'

awslocal dynamodb put-item --table-name Posts --item '{
    "id": {"S": "LIKE-7"},
    "targetType": {"S": "post"},
    "targetId": {"S": "POST-7"},
    "userId": {"S": "LogicLover"},
    "timestamp": {"S": "2024-04-11T11:00:00Z"}
}'

awslocal dynamodb update-item --table-name Posts --key '{"id": {"S": "POST-7"}}' --update-expression "SET likeNumber = likeNumber + :val" --expression-attribute-values '{":val":{"N":"1"}}'
awslocal dynamodb update-item --table-name Posts --key '{"id": {"S": "POST-7"}}' --update-expression "SET likeNumber = likeNumber + :val" --expression-attribute-values '{":val":{"N":"1"}}'