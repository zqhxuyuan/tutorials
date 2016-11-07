#!/bin/bash
if [ $# != 0 ]
then
        echo -e "Usage: $0"
	exit 1
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

# Get the port from the config file
linenum=`cat ${CONFIG_FILE} | grep -n nimbus.master.port | awk '{print $1}' | sed s/://`
linenum=$((${linenum}+1))
portline="$( head -n ${linenum} ${CONFIG_FILE} | tail -n 1 )"
PORT="$( echo ${portline} | sed 's/[^0-9]*//g' )"

# Set the name and type of cache to create
NAME=master
TYPE=MASTER

for server in `cat "${HOSTLIST}"|sed  "s/#.*$//;/^$/d"`
do
    echo "starting cache at $server on port ${PORT}"
	ssh $server "sh ${NIMBUS_HOME}/bin/start-cache.sh ${NAME} ${PORT} ${TYPE}"
done
