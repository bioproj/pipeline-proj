#/bin/bash
export JAVA_HOME=/home/wy/software/jdk-11.0.20
export PATH=$JAVA_HOME/bin:$PATH
#export PATH=/application/software/maven/apache-maven-3.9.5/bin:$PATH
#git pull

mvn clean
mvn install  -DskipTests=true

echo "docker镜像打包......"
docker build  -t registry.cn-hangzhou.aliyuncs.com/wybioinfo/service-pipeline-boot-1.0.jar .
echo "推送docker镜像到阿里云......"
docker push registry.cn-hangzhou.aliyuncs.com/wybioinfo/service-pipeline-boot-1.0.jar



echo "创建网络......"
network_name="pipeline"
if docker network inspect "$network_name" >/dev/null 2>&1; then
  echo "Network '$network_name' exists."
else
  echo "Network '$network_name' does not exist."
  docker network create pipeline
fi

echo "运行 mongo......"
if [[ -n $(docker ps -q -f "name=mongo") ]];then
	echo "has run  mongo"
else
	echo "not run mongo"
	docker run --rm -d \
    --name mongo \
    --network pipeline \
    --hostname mongo \
    -p 27018:27017 \
    registry.cn-hangzhou.aliyuncs.com/wybioinfo/mongo
fi

echo "运行 mongo express......"
if [[ -n $(docker ps -q -f "name=mongo-express") ]];then
	echo "has run  mongo express"
else
	echo "not run mongo express"
	docker run --rm  -d\
    --name mongo-express \
    --network pipeline \
    -p 8082:8081 \
    -e ME_CONFIG_MONGODB_URL=mongodb://mongo:27017/ \
    mongo-express
fi





#echo "停止容器......"
#docker stop service-pipeline
echo "运行容器......"
docker run --rm \
  --name service-pipeline \
  --network pipeline \
  -e MONGO_HOST=mongo \
  -e MONGO_PORT=27017 \
  -e MONGO_DB=pipeline_db \
  -p 30001:30001 \
  registry.cn-hangzhou.aliyuncs.com/wybioinfo/service-pipeline-boot-1.0.jar

docker logs -f  mbiolance-service-pipeline



