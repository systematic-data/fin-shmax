## Startup

### Use the Aeron mode
To startup the aggregator module to use Aeron

        mvn spring-boot:run -Dspring-boot.run.arguments="aeron" -Dspring-boot.run.jvmArguments="--add-opens java.base/sun.nio.ch=ALL-UNNAMED --enable-preview"


### Use Kafka as bus
To startup the aggregator module to use Kafka


        mvn spring-boot:run -Dspring-boot.run.arguments="kafka" -Dspring-boot.run.jvmArguments="--add-opens java.base/sun.nio.ch=ALL-UNNAMED"
