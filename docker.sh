#/bin/bash
cd /application/springCloud/app/mbiolance-service-pipeline
export JAVA_HOME=/application/software/jdk-11.0.16.1
export PATH=$JAVA_HOME/bin:$PATH
export PATH=/application/software/maven/apache-maven-3.9.5/bin:$PATH
git pull

mvn clean
mvn install  -DskipTests=true

echo "docker镜像打包......"
docker build -t master:5000/spring-cloud/mbiolance-service-pipeline-1.0.0.jar .
echo "推送docker镜像到本地私服......"
docker push master:5000/spring-cloud/mbiolance-service-pipeline-1.0.0.jar


echo "停止运行的容器......"
docker stop  mbiolance-service-pipeline

echo "运行容器......"
docker run --rm \
    -d \
    --name mbiolance-service-pipeline \
    -v /home/shanjun/.kube:/root/.kube  \
    --add-host shanjun_kafka:192.168.10.177 \
    --add-host master:192.168.10.177 \
    -e NACOS_HOST=192.168.10.177 \
    -e MYSQL_HOST=192.168.10.177 \
    -e KAFKA_HOST=192.168.10.177  \
    -e MONGO_HOST=192.168.10.177 \
    -e PIPELINE_MYSQL_DATABASE=mbiolance_cloud_pipeline_dev  \
    -e PIPELINE_MONGO_DATABASE=test-api \
    master:5000/spring-cloud/mbiolance-service-pipeline-1.0.0.jar

docker logs  mbiolance-service-pipeline