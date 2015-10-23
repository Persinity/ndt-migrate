#!/bin/bash

HAKA_HOME="$(cd "$(cd "$(dirname "$0")"; pwd -P)"/..; pwd)"

[ -n "$JAVA_OPTS" ] || JAVA_OPTS="-Xms1024M -Xmx1024M -Xss1M -XX:MaxPermSize=256M -XX:+UseParallelGC"

[ -n "$HAKA_CLASSPATH" ] || HAKA_CLASSPATH="$HAKA_HOME/lib/*:$HAKA_HOME/config:$HAKA_HOME/lib/akka/*"

java $JAVA_OPTS -cp "$HAKA_CLASSPATH" -Dakka.home="$HAKA_HOME" com.persinity.haka.impl.actor.HakaExecJobNode "$@"

