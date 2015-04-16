#!/bin/bash

# REQUIRED: Path to nimbus home directory
#export NIMBUS_HOME=

# REQUIRED: This is the path to where your Hadoop installation is.
#export HADOOP_HOME=
# TODO

# REQUIRED: This is the path to where your Java installation is.
#JAVA_HOME=

CONFIG_FILE=${NIMBUS_HOME}/conf/nimbus-default.xml

LOG_DIR={$NIMBUS_HOME}/logs
HOSTLIST=${NIMBUS_HOME}/conf/servers
NIMBUS_JAR_FILE="$( ls ${NIMBUS_HOME}/bin/nimbus*.jar )"
NIMBUS_MAIN_CLASS="Nimbus"
NIMBUS_MASTER_CLASS="nimbus.client.MasterClient"

mkdir -p ${LOG_DIR}

NIMBUS_CLASSPATH="${NIMBUS_HOME}/lib:${NIMBUS_HOME}/conf:${HADOOP_HOME}/conf"

NIMBUS_JAVA_OPTS="-Xmx2048m -Xms1024m"

JAVA=`which java`

NIMBUS_EXEC="${JAVA} ${NIMBUS_JAVA_OPTS} -Djava.library.path=${NIMBUS_HOME}/bin/native -classpath ${NIMBUS_CLASSPATH} -jar ${NIMBUS_JAR_FILE} -start"
NIMBUS_KILL="${JAVA} ${NIMBUS_JAVA_OPTS} -Djava.library.path=${NIMBUS_HOME}/bin/native -classpath ${NIMBUS_CLASSPATH} -jar ${NIMBUS_JAR_FILE} -kill"
