#!/bin/bash

basedir=$(dirname $0)/..

CLASSPATH=.
for a in ${basedir}/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:${a}
done

java -cp ${CLASSPATH} com.systematicdata.shmax.testtools.fixacceptor.MarketDataAcceptor
