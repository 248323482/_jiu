#!/usr/bin/env sh

PRG="$0"
PRGDIR=`dirname "$PRG"`
cd "$PRGDIR/../"

locate_str=`pwd`
signal_id=`kill -l USR2`

node_pid=`jps -v |grep "porter-node-boot-.*.jar"|grep "${locate_str}/"|awk '{printf  " "$1}'`

if [ -n "$node_pid" ]
then
    kill -${signal_id} ${node_pid}
	echo "[${node_pid}]datas-node shutdown Successfully"
fi