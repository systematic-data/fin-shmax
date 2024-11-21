#!/bin/bash

basedir=$(dirname $0)/..

CLASSPATH=.
for a in ${basedir}/lib/*.jar; do
    CLASSPATH=${CLASSPATH}:${a}
done

java --enable-preview  \
    --add-opens java.base/sun.nio.ch=ALL-UNNAMED \
    -cp ${CLASSPATH} com.systematicdata.shmax.modules.venues.fix.example.QuickFixVenueApplication
