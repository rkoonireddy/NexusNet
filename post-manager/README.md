# post-manager

This app handles post management

# Running the app with Docker

1. Build the image
   `docker build . -t nexusnet-postmanager:latest`
2. Run the image
   `docker run -p 8081:8081 nexusnet-postmanager:latest`
3. The app will be served on [localhost:8081](http://localhost:8081)

### Starting the app with Localstack

1. Start the `docker-compose.yml` file with

   ```
   docker compose up
   ```

2. The app will be served on [localhost:8081](http://localhost:8081)

### Running Integration Tests

1. Start the `docker-compose.yml` file with

   ```
   docker compose up
   ```

2. Run the tests

# Documentation

Once app is running in the documentation can be accessed via: http://localhost:8081/swagger-ui/index.html
