#!/bin/bash

if [ $# != 1 ]
then
	echo "Usage: $0 <cachename>"
	exit -1
fi

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

echo "stopping cache $1 "

for server in `cat "${HOSTLIST}"|sed  "s/#.*$//;/^$/d"`
do
	ssh $server "kill -9 \$(cat ${NIMBUS_HOME}/pids/$server-$1.pid)"
	ssh $server "rm ${NIMBUS_HOME}/pids/$server-$1.pid"
done
