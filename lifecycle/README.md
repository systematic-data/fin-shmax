# Modules for Lifecycle of products.

## Architecture
There is a *main* module that contains the starter up of the application, reads
the configuration and loads the specific modules according to the configuration.

In each of the other folders, the different logic for the lifecycle of different products.

The folder *impl* contains the implementation of the desired modules of lifecycle
management for the desider products.

At the the end, an "implementation" is just only:

        - A `jar` file of the *main* module
        - One or several `jar`files with the desired logic
        - A configuration file in resources with the list of class mdules to load (contained in the previous `jars`
        - A script to start up, invoking the launcher in *main* module with the *config file*with the list of logics
