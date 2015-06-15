# Working with Apache Storm

## Introduction

Apache Storm is a free and open source distributed realtime computation system. Storm makes it easy to reliably process unbounded streams of data, doing for realtime processing what Hadoop did for batch processing.

## Installation

*   Install java and python on each and every system which are to be used in the complete project.

    sudo apt-get install openjdk-7-jre
    sudo apt-get install python

### Setting up Zookeeper

*   Install Zookeeper on one of the available machines.

    wget http://apache.mirrors.ionfish.org/zookeeper/stable/zookeeper-3.4.6.tar.gz
    tar -xvzf zookeeper-3.4.6.tar.gz

*   Make a new file **zoo.cfg** (configuration file) under the conf directory inside the zookeeper-3.4.6 folder.
*   Copy the default configurations from **zoo_sample.cfg** present in the same directory into your **zoo.cfg** file.  
     _Zookeeper has been set up successfully ..........yesssss_ :stuck_out_tongue_winking_eye:

### Setting up Nimbus

*   Install Apache Storm on another machine.

    wget http://apache.mirrors.ionfish.org/storm/apache-storm-0.9.5/apache-storm-0.9.5.tar.gz
    tar -xvzf apache-storm-0.9.5.tar.gz

*   Edit the **storm.yaml** under the conf directory inside apache-storm-0.9.5 folder

    storm.zookeeper.servers:
         - "192.168.1.77" #put the ip address of zookeeper here
    storm.local.dir: "/mnt/storm" #create storm folder inside mnt folder under root directory
    nimbus.host: "192.168.1.72" #put the ip address of nimbus here (i.e. current machine ip)

_Nimbus has been set up successfully ..........well done_ :thumbsup:

### Setting up Supervisor (slave)

*   Install Apache Storm on another machine.

    wget http://apache.mirrors.ionfish.org/storm/apache-storm-0.9.5/apache-storm-0.9.5.tar.gz
    tar -xvzf apache-storm-0.9.5.tar.gz

*   Edit the **storm.yaml** under the conf directory inside apache-storm-0.9.5 folder

    storm.zookeeper.servers:
         - "192.168.1.77" #put the ip address of zookeeper here
    storm.local.dir: "/mnt/storm" #create storm folder inside mnt folder under root directory
    nimbus.host: "192.168.1.72" #put the ip address of nimbus here

_Supervisor has been set up successfully ..........at last_ :sweat_smile:  

## Initializing Storm Components and Zookeeper

1.  Start Zookeeper.  
     Navigate to the Zookeeper root directory on the zookeeper machine.

    sudo java -cp ./zookeeper-3.4.6.jar:./lib/slf4j-api-1.6.1.jar:./lib/slf4j-log4j12-1.6.1.jar:./lib/log4j-1.2.16.jar:conf  org.apache.zookeeper.server.quorum.QuorumPeerMain ./conf/zoo.cfg

1.  Start Nimbus.  
     Navigate to the Apache Storm root directory on the nimbus machine.

    sudo ./bin/storm nimbus

1.  Start Supervisors/Slaves.  
     Navigate to the Apache Storm root directory on the supervisor/slave machine.

    sudo ./bin/storm supervisor

1.  Start Storm web UI (to monitor the submitted topology).  
     Navigate to the Apache Storm root directory on the nimbus or supervisor/slave machine.

    sudo ./bin/storm ui

#### Congratulations! We are ready now. :v:

## Submitting Topologies to the Storm Cluster

1.  Package all your code and dependencies related to your topology into a single jar.
2.  On the client machine, navigate to Apache Storm directory.

    ./bin/storm jar '/path/to/your/jar/file.jar' TopologyMain

where TopologyMain is the name of main class.

#### Now we can monitor the submitted Topology by visting the UI URL ( http://`<IP ADDRESS of the machine running storm UI>:8080` )
