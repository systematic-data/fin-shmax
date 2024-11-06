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

        log.retention.ms = 10
        log.cleanup.policy = delete
