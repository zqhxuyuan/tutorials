#!/bin/bash
if [ $# != 4 ]
then
        echo -e "Usage: $0 <name> <host> <port> <type>"
	exit -1
fi

bin=`dirname "$0"`
bin=`cd "${bin}"; pwd`

. "${bin}/nimbus-env.sh"

if [ -z ${HADOOP_HOME} ]; then
	echo "JAVA_HOME variable is not set in ./bin/nimbus-env.sh"
	exit 1
fi

if [ -z ${JAVA_HOME} ]; then
	echo "JAVA_HOME variable is not set in ./bin/nimbus-env.sh"
	exit 1
fi

NAME=$1
server=$2
PORT=$3
TYPE=$4

echo "starting cache at $server on port ${PORT}"
ssh $server "sh ${NIMBUS_HOME}/bin/start-cache.sh ${NAME} ${PORT} ${TYPE}"