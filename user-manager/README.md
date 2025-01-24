# user-manager
This app handles User Authentication and Profile Management

# Running the app with Docker

1. Build the image
   `docker build . -t nexusnet-usermanager:latest`
2. Run the image
   `docker run -p 8080:8080 nexusnet-usermanager:latest`
3. The app will be served on [localhost:8080](http://localhost:8080)

# Starting the app in dev mode

1. Start up docker-compose-localstack.yml
2. Publish the ports using:
   docker run -p 4566:4566 localstack/localstack
3. Run init-dynamodb-localstack.sh
4. Start the application by running UserManagerApplication with the dev profile

### Starting the app with Localstack

1. Start the app with the `Dockerfile` or through `docker-compose.yml`  with

   ```
   docker compose up
   ```

2. The app will be served on [localhost:8080](http://localhost:8080)

### Running Integration Tests

1. Start the `docker-compose.yml` file with

   ```
   docker compose up
   ```

2. Run the tests

# Documentation

You can find the `index.html` in the `docs` directory. Open it in a browser to view the documentation.
You can update the documentation as needed by editing the `users.yaml` file in the `src/main/resources` directory and recompiling the project.
