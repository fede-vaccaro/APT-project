# Appichetto
A course project concerning Test Driven Development and other useful tools. In Java. 

## Requirements

* Docker
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
* ### Swing error

    We have tested the application on various environments and on Gnome an error related to Swing is launched, while for example with the i3 window manager it is not.

    ```
    [ERROR] Unable to make visible the location of the index <0> by scrolling to the point <2,2> on javax.swing.JTextField[name='Username', text='', enabled=true, visible=true, showing=true]
    ```

    You can consider changing the desktop environment or you can run the tests on a virtual screen avoiding even making the PC useless for 5 minutes. For the second choice you can run: 
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
    java -jar aggregator-module/target/ aggregator-module-0.0.1-SNAPSHOT-jar-with-dependencies.jar
    ```
* To stop *Postgres DB* run:
    ```bash
    docker-compose down #add --volumes to delete all data
    ```
