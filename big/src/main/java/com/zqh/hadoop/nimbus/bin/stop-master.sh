#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

. "${bin}/nimbus-env.sh"

if [ -z ${HADOOP_HOME} ]; then
	echo "JAVA_HOME variable is not set in ./bin/nimbus-env.sh"
	exit 1
fi

if [ -z ${JAVA_HOME} ]; then
	echo "JAVA_HOME variable is not set in ./bin/nimbus-env.sh"
	exit 1
fi

echo "stopping master service"
${NIMBUS_KILL} master
#for server in `cat "${HOSTLIST}"|sed  "s/#.*$//;/^$/d"`
#do
#        ssh $server "kill -9 \$(cat ${NIMBUS_HOME}/pids/$server-master.pid)"
#        ssh $server "rm ${NIMBUS_HOME}/pids/$server-master.pid"
#done
