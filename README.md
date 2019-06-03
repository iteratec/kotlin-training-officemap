# Office Map (for Kotlin Training)

**The purpose of this repository is to show a legacy Java application, which then can be migrated to Kotlin. It is not meant to be used in production.**

The goal of the office map is to reduce the bottleneck at workstations by providing the employees with a detailed view of all occupied workstations in the office. The website provides various functions, which help getting an overview of the situation in the office and placing reservations on workstations.

## API-Interface

The interfaces of the app are documented with [Swagger](http://localhost:8080/swagger-ui.html). Each individual interface can be viewed and tested here.

## Quick Start

The following instructions will get you started. For this you will need to install [Docker](https://www.docker.com/get-started) as well as version 8 of the [JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or a similar OpenJDK.

- Start by cloning the current version to yor machine (this repo).
- You can now open the project with your IDE. We recommend using [IntelliJ IDEA](https://www.jetbrains.com/idea/).
- You can use the shell to control docker, which will host your local database:
  - `docker-compose up -d` will start the database in the background.
  - `docker-compose down` will shut down the database.
  - `docker-compose down -v`  will shut down the database and erase all data within it.
- While the database is running you can start the office map in your IDE and access it by opening  [localhost:8080](https://localhost:8080) in your browser.

## Access your local database

For a close look at the database in your container you can use `docker-compose exec database psql -U officemap_dev` to connect to it via postgresql (container has to be up and running).

From here on you can use SQL commands to navigate and edit your database.
