# Fin-SHMAX Finacial Streaming High Frequency

## Modules

In the following folders, we can find these modules:

1. `aggregator` Aggregation module that takes all RAW prices and generates the tradeable
    internal price.
1. `core` Core basic elements used in all modules. Not a module.
1. `fixvenue` Module to connect to FIX Acceptors and introduce RAW prices into the BUS
1. `lifecycle` Set of modules that react to market and external events, maintaining a
    kind of lifecycle of specific products
    1. `fxtrade` maintain and tries to hedge FX-SPOT trades
    1. `fxsprods` maintain the life of Structured Products
    2. `fxoptfwd` maintain the lifecycle of FX Option Forwards
 

## Main architecture

Modules are connected to a Bus.
Used BUS is Aeron. Aeron does not need a server, as the clients connect between them autonomously.

Previously, Kafka was used. But at this moment Kafka is deprecated.

### Multicast IP and Stream Ids
In Aeron, connections are made using Multicast Address.
In each *Multicast Address*, clients connect in Aeron bus using *Stream Ids*. 

In a *Multicast Address* and *Stream Id*, the same of messages is sent and received. (Senders and receivers should share the same values.)

This is the list of assigned address and ids:

* `224.0.1.1:40124 streamId=1` for RAW prices from Venues (reliable=false, linger=0)
* `224.0.1.1:40124 streamId=2` for Aggregated Tradeable prices (reliable=false, linger=0)
* `224.0.0.1:40124 streamId=1000` for Hedge Requests and `streamId=1001` for Hedge Results (reliable=true, linger=0)
* `224.0.1.:40124 streamId=2000` for Trade Requests (reliable=true, linger=0)

## Install Kafka
If you want to use Kafka bus, installation is needed.
The instructions are in `Kafka-Mac.md` file.
