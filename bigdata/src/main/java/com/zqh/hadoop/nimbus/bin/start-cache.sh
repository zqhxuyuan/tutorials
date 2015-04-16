#!/bin/bash
if [ $# != 3 ]
then
        echo -e "Usage: $0 <name> <port> <type>"
	exit -1
fi

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

echo $bin

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
PORT=$2
TYPE=$3
LOG_FILE=${LOG_DIR}/nimbus-${NAME}-${USER}-${TYPE}.log
{
echo "Starting cache ${NAME} ${PORT} ${TYPE}"
${NIMBUS_EXEC} -n ${NAME} -p ${PORT} -t ${TYPE} &
} > ${LOG_FILE} 2>&1
mkdir -p ${NIMBUS_HOME}/pids
echo $! > ${NIMBUS_HOME}/pids/`hostname`-${NAME}.pid

