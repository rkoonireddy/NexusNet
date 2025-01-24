# NexusNet
This repo makes it easier to run the full NexusNet application. 

## How to run (Linux and MacOS)

### 1. Install dependencies

You need to install `git`, `docker`, `docker compose` and `java` .

### 2. Download and compile projects using `initialize` script

```
bash initialize
```

Right now the script fetches the `main` branch. If you like to fetch another branch **from all the projects** just write the branch name as the first argument. If you want to fetch develop branches use the following command: 

```
bash initialize develop
```

If you want to test your **local version**, copy your project folder inside the root of this project.

### 3. Run the project

```
docker compose up --build
```

You will now be able to use the app at [localhost:3000](http://localhost:3000).

### Reset (if needed)

If you like to update the projects and you feel too lazy to pull them, you can use the `reset` script and then go back to STEP2. **Do not execute this script while in another directory.**

```
bash reset
```

## How to run (Windows)

## Option 1:

Follow the instructions for Linux but use `initialize.bat` instead of `initialize` and `reset.bat` instead of `reset`.

## Option 2:

The best option right now is to run it in an [emulated bash shell](https://itsfoss.com/install-bash-on-windows/).
