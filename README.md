# iteraOfficeMap

The goal of the iteraOfficeMap is to reduce the bottleneck at workstations by providing the employees with a detailed view of all occupied workstations in the office. The website provides various functions, which help getting an overview of the situation in the office and placing reservations on workstations.

### Important links

 - [Deployment](https://office-map.cloudapps.iterashift.com/)
 - [Git-Repository](https://iteragit.iteratec.de/studentsFRA/iteraOfficeMap/tree/master)
 - [Issue board](https://iteragit.iteratec.de/studentsFRA/iteraOfficeMap/boards)
 - [Mattermost](https://iterachat.iteratec.de/iteratec/channels/officemap)
 - [Openshift](https://console.cloudapps.iterashift.com:8443/console/project/office-map/overview)
 - [Docker image in Artifactory](https://artifactory.iteratec.io/artifactory/webapp/#/artifacts/browse/tree/General/docker/office-map/office-map/latest)

## API-Interface

The interfaces of the app are documented with [Swagger](https://office-map.cloudapps.iterashift.de/swagger-ui.html). Each individual interface can be viewed and tested here.


## Quick Start Guide (Developer)
The following instructions will lead you trough the Set-Up required for working on iteraOfficeMap. For this you will need to install [Docker](https://www.docker.com/get-started) aswell as the Version 8 of [JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

* Start by cloning the current Version of iteraOfficeMap to yor machine from the [Git-Repository](https://iteragit.iteratec.de/studentsFRA/iteraOfficeMap/tree/master).

+ You can now open the project with your IDE.

* Here you can use the console to control docker, which will host your local database:
    * `docker-compose up -d` will start the database.
    * `docker-compose down` will shut down the database.
    * `docker-compose down -v`  will shut down the database and erase all the data within it.

+ While the Container is running you can execute iteraOfficeMap in your IDE and access it by opening  [localhost:8080](https://localhost:8080) in your browser.

### Access your local Database
For a close look at the database behind iteraOfficeMap in your container you can use
- `docker-compose exec database psql -U officemap_dev` to connect to it via postgresql (container has to be running).

From here on you can use SQL commands to navigate and eddit your database. 





