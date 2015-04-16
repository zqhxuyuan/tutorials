for i in {0..10};
do pig -p NODE=/user/huangjun/dataguru/node_$i -p OUTPUT=/user/huangjun/dataguru/node_{$i+1} lpa.pig > log/$i;
done;
