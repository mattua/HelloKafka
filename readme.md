This is my project for running Kafka

This is a good tutorial on the subject
https://sookocheff.com/post/kafka/kafka-in-a-nutshell/

Current instructions are

-------------------------1) checkout git project - note this now includes the kafka installation

1.5) for best results use the terminal inside the IntelliJ window

------------------------2) in the folder inside the project - run the following commands in separate windows

DO NOT RUN FROM THE BIN FOLDER DIRECTLY


--------START ZOOKEEPER----------------------------------------
IN A NEW Terminal Tab in IntelliJ
kafka_2.11-1.1.0/bin/zookeeper-server-start.sh kafka_2.11-1.1.0/config/zookeeper.properties

NOTE: In kafka, each node in the cluster must have its OWN LOG DIR



--------START KAFKA BROKER 0----------------------------------------
IN A NEW Terminal Tab in IntelliJ

kafka_2.11-1.1.0/bin/kafka-server-start.sh kafka_2.11-1.1.0/config/server0.properties

--------START KAFKA BROKER 1----------------------------------------
IN A NEW Terminal Tab in IntelliJ

kafka_2.11-1.1.0/bin/kafka-server-start.sh kafka_2.11-1.1.0/config/server1.properties

--------START KAFKA BROKER 2----------------------------------------
IN A NEW Terminal Tab in IntelliJ
kafka_2.11-1.1.0/bin/kafka-server-start.sh kafka_2.11-1.1.0/config/server2.properties


----------------------3) create a replicated topic in a new Tab in IntelliJ
IN A NEW Terminal Tab in IntelliJ
kafka_2.11-1.1.0/bin/kafka-topics.sh --create --topic dharshini --zookeeper localhost:2181 --replication-factor 3 --partitions 3

The partition is basically segragating the topic into different streams each of which are consumed independently


-------------------2) Describe topic 
kafka_2.11-1.1.0/bin/kafka-topics.sh --describe --zookeeper localhost:2181 --topic dharshini

The partitions are stored on disk like this
/tmp/kafka-logs/dharshini-0$ ls
00000000000000000000.index      00000000000000000000.timeindex  00000000000000000495.snapshot   00000000000000000621.snapshot
00000000000000000000.log        00000000000000000437.snapshot   00000000000000000515.snapshot   leader-epoch-checkpoint
/tmp/kafka-logs/dharshini-0$ 



-------------------3) publish to topic

Run kakfaproducerexample main class making sure you change the topic name string constant

------------------4) see the data being logged on disk

There is an individual log folder per kafka broker, set as the log.dir in server.properties
/tmp/kafka-logs
/tmp/kafka-logs-1
/tmp/kafka-logs-2

-------------------5) consume messages 

Run the main method in the consumerloop class making sure to set the right topic name

____________________________________________________


------List the consumer groups--------------------------
kafka_2.11-1.1.0/bin/kafka-consumer-groups.sh  --list --bootstrap-server localhost:9092
consumer-tutorial-group
consumer-tutorial-group1535187950398
consumer-tutorial-group1535188012572

-----describe a consumer group--------------------------
kafka_2.11-1.1.0/bin/kafka-consumer-groups.sh --describe --group mygroup --bootstrap-server localhost:9092

TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                     HOST            CLIENT-ID
dharshini       0          724             724             0               consumer-1-20f1bf0e-da3a-4632-aad8-9ca297c8f732 /127.0.0.1      consumer-1
dharshini       1          781             782             1               consumer-1-20f1bf0e-da3a-4632-aad8-9ca297c8f732 /127.0.0.1      consumer-1
dharshini       2          777             777             0               consumer-2-bc63fced-c604-4fe4-81f1-4cf4374ed0b8 /127.0.0.1      consumer-2


here there are 2 consumers in the group - and each partition is assigned to one of the consumers

If there are more consumers in the group than partitions, then one of the consumers will be idle
TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                     HOST            CLIENT-ID
dharshini       2          793             793             0               consumer-3-e2d6b794-9edc-4a7d-b0ca-e99edc198229 /127.0.0.1      consumer-3
dharshini       1          796             796             0               consumer-2-4525f613-456e-41b7-9e63-5a3c73c0fa2e /127.0.0.1      consumer-2
dharshini       0          736             737             1               consumer-1-17a14147-28ce-4a8b-93b7-06c655087c6a /127.0.0.1      consumer-1

Here we had 4 consumers but only 3 are needed

If we shut down the consumer group and then describe the group
-it shows no active members
-the current offset is saved per partition - not NOT per consumer, we can start the consumer group
with a different number of consumers
-the log end offset is where the partitions are currently up to on the producer side


TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     HOST            CLIENT-ID
dharshini       1          810             820             10              -               -               -
dharshini       0          750             765             15              -               -               -
dharshini       2          817             828             11              -               -               -

Start again with only 1 consumer in the group (there were 3 before)

Now we see that all the partitions are being consumed by the ONE consumer - and the consumer catches up 
and the current offset once again = the log end offset

TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                     HOST            CLIENT-ID
dharshini       0          793             793             0               consumer-1-e0e17122-efc5-494c-99b5-fd05b3cbdc69 /127.0.0.1      consumer-1
dharshini       1          852             853             1               consumer-1-e0e17122-efc5-494c-99b5-fd05b3cbdc69 /127.0.0.1      consumer-1
dharshini       2          855             856             1               consumer-1-e0e17122-efc5-494c-99b5-fd05b3cbdc69 /127.0.0.1      consumer-1

the consumer started at the current offset and went through each partition ONE BY ONE until it caught up

     Processing: buy bread at time 1535189014795
Consumer 0: {partition=0, offset=750, value=bread at time 1535189014795 to buy}
     Processing: buy bread at time 1535189017799
Consumer 0: {partition=0, offset=751, value=bread at time 1535189017799 to buy}
     Processing: buy bread at time 1535189047858
Consumer 0: {partition=0, offset=752, value=bread at time 1535189047858 to buy}
     Processing: buy bread at time 1535189050864
Consumer 0: {partition=0, offset=753, value=bread at time 1535189050864 to buy}


NEW CONSUMER GROUPS READING FROM THE BEGINNING

     // this is cool - so if you set this propery (by default it is latest)
            // the consumption will start at the earliest possible message
            // so a new consumer group will start from 0 in each parition and catchup
            // after that if the consumers all go down in the group and then restart
            // the commit offset per partition will be loaded and resumed from there
            
            props.put("auto.offset.reset", "earliest");

The important point is that consumer groups are registered with the kafka cluster
along with the latest committed offset per partition

_________________________________________
_________________________________________
DOCKER

    -havent got console to work yet so interacting with the shell is not working
    -but the following does take care of stopping and starting, and configuring the whole container
______________________________________________________

Run this command to get going...you need to be careful when moving directories
around etc - you can get clashes of container names etc so this command will
stop and remove all containers and launch again

docker-compose stop; docker stop $(docker ps -a -q);docker rm $(docker ps -a -q);docker-compose -f docker-compose-single-broker.yml up -d

0. you need to add your host IP address to the docker-compose.yml file, to run multiple nodes

eg.
  KAFKA_ADVERTISED_HOST_NAME: 192.168.0.12

1. if you change the config to clear everything down each time:

docker-compose stop; docker stop $(docker ps -a -q);docker rm $(docker ps -a -q)

2. run the cluster with 5 kafka nodes...maybe wait a while for them to synch

docker-compose up --scale kafka=5 -d

3. check the ports the nodes are running on

docker ps
    0.0.0.0:32789->9092/tcp  ... in this case <32789> is the port you connect to
    note that this is because the ports are allocated each time you start the service

4. connect to kafka on localhost:<32789>


5. Connect to shell so you can create topics etc:
        <hostip> <zk ip:zk port>

./start-kafka-shell.sh 192.168.0.12 192.168.0.12:2181

NOTE...all the env variables are already set

create new topic
$KAFKA_HOME/bin/kafka-topics.sh --create --topic mytopic --partitions 4 --zookeeper $ZK --replication-factor 2

list topics
$KAFKA_HOME/bin/kafka-topics.sh --list --zookeeper $ZK

delete topic
$KAFKA_HOME/bin/kafka-topics.sh --delete -topic mytopic --zookeeper $ZK
____________________________________________________

NATIVE

    -brokers,config files etc have to be set up manaully
______________________________________________________
