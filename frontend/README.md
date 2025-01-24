# frontend

NexusNet is a React application developed with TypeScript and JavaScript. It provides a platform for users to interact and share posts. **To run the entire application refer to the [fullapp repo](https://github.com/ASE-FS24/fullapp).**

## Running the app with Docker

1. Build the image
   `docker build . -t nexusnet-frontend:latest`
2. Run the image
   `docker run -p 3000:3000 nexusnet-frontend:latest`
3. The app will be served on [localhost:3000](http://localhost:3000)

## Running without Docker

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

***Note:*** *To successfully run the application, the three backend services (post-manager, user-manager, & chat-manager) need to be running. Update the links in the [.env](./.env) file.*


### Prerequisites

- Node.js
- npm

### Installation

- Clone the repository
```bash
git clone https://github.com/ASE-FS24/frontend.git
```

- Navigate to the project directory
```bash
cd nexusnet
```

- Install dependencies
```bash
npm install
```

- Start the development server
```bash
npm start
```

Open [http://localhost:3000](http://localhost:3000) to view it in the browser. The page will reload if you make edits.


## Deployment

To create a production build, use:

```bash
npm run build
```

## Technologies used
The frontend of the application is built with React, a JavaScript library for building user interfaces. React's 
component-based architecture makes it easy to create complex UIs from small, isolated and reusable pieces of code. 
It also optimizes rendering to the DOM, making applications faster and more efficient.  

### Package manager
The project uses npm (Node Package Manager) as a package manager, which handles the management of dependencies for the 
application. It allows developers to install, update, and use JavaScript packages in their projects.

### Styling
The application's styling is done using styled-components, a CSS-in-JS library that allows developers to write actual 
CSS code to style their components. It also removes the mapping between components and styles, which means that when 
you're defining your styles, you're actually creating a normal React component.  

### Routing
The project also uses React Router for routing, which is a standard library for routing in React. It enables the 
navigation among views of various components in a React Application, allowing the developer to define multiple routes 
for the user.  

### State management
The application state management is handled by Redux, a predictable state container for JavaScript apps. It helps you 
write applications that behave consistently, run in different environments (client, server, and native), and are easy 
to test.  

## Source Code
The source code for the frontend of the application can be found in the [src](./src) directory. The main components
are located in the [components](./src/app/Pages) directory, while the Redux store configuration is in the
[store](./src/app/store.ts) file. The [App.tsx](./src/App.tsx) file is the entry point for the application. The 
[Pages](./src/app/Pages) directory contains the main components of the application, such as the Home, Profile, and
Chat components. The different folders like `chat`, `Comment`, `Post`, and `User` contain the components, services and 
redux slices related to the respective entities. The [static](./src/static/images) directory contains the images used in
the application.
