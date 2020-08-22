JAVA_OPT="{{opt}}";
JAVA_OPT=$JAVA_OPT" -Dspring.profiles.active={{profiles}}";
echo ">>> $JAVA_OPT";
pid=`ps -ef | grep java | grep {{RUN_JAR}} | grep -v grep | awk '{print $2}'`;
count=`ps -ef | grep java | grep {{RUN_JAR}} | grep -v grep | wc -l`;
if [ $count != 0 ];then  kill $pid;   count=`ps -ef | grep java | grep {{RUN_JAR}} | grep -v grep | wc -l`;  pid=`ps -ef | grep java | grep {{RUN_JAR}} | grep -v grep | awk '{print $2}'`;  kill -9 $pid;  echo ">>> {{RUN_JAR}} is stop!"; fi;
cd {{remoteDir}};
nohup java -jar {{RUN_JAR}} $JAVA_OPT {{RUN_JAR}} > /dev/null 2>&1 &
echo ">>> {{RUN_JAR}} is starting...";
sleep 5
echo ">>> {{RUN_JAR}} is start!";
count_=`ps -ef | grep java | grep {{RUN_JAR}} | grep -v grep | wc -l`;
if [ $count_ != 0 ];then  echo ">>> {{RUN_JAR}} is running..."; else echo ">>> {{RUN_JAR}} is not running..."; fi;