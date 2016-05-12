## Submitting Topology to the Storm Cluster using Apache Maven

* Start Zookeeper and all the Storm components.

* Install **Apache Maven** on the client machine.
```bash
sudo apt-get install maven
```
* Build project using **maven**
```bash
mvn assembly:assembly
```
NOTE: Exclude storm dependency from pom.xml while building jar file.

* On the client machine, navigate to Apache Storm directory.
```bash
./bin/storm jar '/path/to/your/jar/file.jar' TopologyMain
```
where TopologyMain is the name of main class.

##### Now we can monitor the submitted Topology by visting the UI URL<br/>
http://``<IP ADDRESS of the machine running storm UI>:8080`` 
