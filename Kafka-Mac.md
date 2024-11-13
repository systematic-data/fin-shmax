# Install Kafka on your Max

## Getting the binary directly.

1. Go to https://kafka.apache.org/downloads
2. Take the "Binary download" `tgz`
3. Unzip it
    
    	tar -xvz kafka_*.tgz

4. Move the created folder (`kafka...`) to `/usr/local/`
5. Voilà!! To start it up:

    	./bin/zookeeper-server-start.sh config/zookeeper.properties
        ./bin/kafka-server-start.sh config/server.properties


## Configuration
Configure Kafka for HFT.

Generally speaking, set the following values into `config/server.properties`

        log.flush.interval=10
        log.default.flush.interval.ms=100
        log.default.flush.scheduler.interval.ms=100
        log.retention.ms = 1
        log.cleanup.policy = delete
        num.replica.fetchers=1
        unclean.leader.election.enable=true
        min.insync.replicas=1
        acks=0
        retention.ms=1
        cleanup.policy=delete
