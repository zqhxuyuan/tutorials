#!/bin/bash

if [ $# != 3 ]
then
	echo "Usage $0 <filename> <strlen> <num strings>"
	exit -1
fi
export LEN=$2
rm -f $1
for i in `seq 1 $3`
do
	line=`./random-string.sh`
	echo $line >> $1
done
