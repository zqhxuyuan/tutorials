#!/bin/bash

JAVA_HOME=/usr/lib/jvm/java-1.6.0-openjdk

rm *.o

g++ -Wno-deprecated -fPIC -I$JAVA_HOME/include/ -I$JAVA_HOME/include/linux/ -c *.cpp

if [ $? -ne 0 ]; then
	echo "Build error"
	exit 1
fi

g++ -shared *.o -o libNativeNimbus.so

if [ $? -ne 0 ]; then
        echo "Link error"
        exit 1
fi

ldconfig -v -n .

mkdir -p ../../../bin/native
cp libNativeNimbus.so ../../../bin/native

echo "Install complete"
exit 0
