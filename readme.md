This is my project for running Kafka

____________________________________________________

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
