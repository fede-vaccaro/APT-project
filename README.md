# Appichetto
A course project concerning Test Driven Development and other useful tools. In Java. 

## Requirements

* Docker
* Docker Compose
* Maven
* Java 8

## Run the tests


1. ### Unit, IT, E2E
    Start the test with:
    ```
    mvn clean test verify
    ```
2. ### PIT
    To include the PIT:
    ```
    mvn clean test verify -P run-PIT
    ```

3. ### SonarQube 
    If you want run the tests in SonarQube, exec the second *docker-compose*:
    ```
    docker-compose -f docker-compose-sonar.yml up
    ```
    and then:

    ```
    mvn clean test verify sonar:sonar
    ```
* ### Run on virtual screen:

    You can run the tests on a virtual screen avoiding making the PC useless for 5 minutes: 
    ```
    sudo apt-get install -y tightvncserver
    ```

    and then:

    ```
    ./onVirtualScreen.sh mvn clean test verify
    ```


## Start AppIchetto

* Run the command:

    ```bash
    docker-compose up
    ```

    Will be started *Postgres DB* and a volumes attached to it, to make the data persistence.

* Now you can launch **AppIchetto** with:
    ```bash
    java -jar aggregator-module/target/aggregator-module-0.0.1-SNAPSHOT-jar-with-dependencies.jar
    ```
* To stop *Postgres DB* run:
    ```bash
    docker-compose down #add --volumes to delete all data
    ```
