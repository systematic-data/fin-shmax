# Aeron Server

Java code to startup an Aeron Server


## Build and run
Use Maven to create a single jar with all depdencies

        mvn clean compile assembly:single


To run just with

        java --add-opens java.base/sun.nio.ch=ALL-UNNAMED --enable-preview
            -XX:ZCollectionInterval=20 -XX:MaxGCPauseMillis=1 -XX:+UseZGC  
            -jar aeron-server-VERSION-jar-with-dependencies.jar 
            AERON-SERVER-NAME AERON-SERVER-CONFIG

`AERON-SERVER-NAME` should be, at the very present moment, always `bridge`

`AERON-SERVER-CONFIG` should be present in configuration file

        resources/application.properties

in this way:

        aeron.server.AERON-SERVER-NAME.configFile=specific-file-for-this-server.properties



## Troubleshoting

If you see an error like:

        Caused by: java.lang.reflect.InaccessibleObjectException: Unable to make field private final java.util.Set sun.nio.ch.SelectorImpl.selectedKeys accessible: module java.base does not "opens sun.nio.ch" to unnamed module @XXXXXX

Maybe you need to add this options to the JVM

        --add-opens java.base/sun.nio.ch=ALL-UNNAMED


