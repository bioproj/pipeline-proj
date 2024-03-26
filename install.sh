#!/bin/bash
git pull

dir=`pwd`
app="service-pipeline-admin-1.0.jar"
jar="${dir}/service-pipeline-admin/target/${app}"


pid=$(jps | grep $app | awk '{print $1}')

if [ $pid ]
then
  echo "kill ${pid}"
  kill -9 ${pid}
fi

mvn clean
mvn install -DskipTests=true

if [ ! -f $jar ];then
  echo "build failure！！！"
  exit 8
fi

echo $jar
nohup java -jar $jar 2>&1 > app.log &
tail -f app.log