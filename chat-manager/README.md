# chat-manager
This app handles chat management

# Running the app with Docker
1. Build the image
   `docker build . -t nexusnet-chatmanager:latest`
2. Run the image
   `docker run -p 8082:8082 nexusnet-chatmanager:latest`
3. The app will be served on [localhost:8082](http://localhost:8082)

### Starting the app with Localstack

1. Start the `docker-compose.yml` file with
   ```
   docker compose up
   ```

2. The app will be served on [localhost:8082](http://localhost:8082)

### Running Integration Tests

1. Start the `docker-compose.yml` file with

   ```
   docker compose up
   ```

2. Run the tests

# Documentation

Once app is running in the documentation can be accessed via: http://localhost:8082/swagger-ui/index.html
