
#依赖的父镜像
FROM adoptopenjdk/openjdk11:jre-nightly
WORKDIR /opt
COPY service-pipeline-boot/target/service-pipeline-boot-1.0.jar service-pipeline-boot-1.0.jar
COPY service-pipeline-boot/src/main/resources/* /opt/
CMD ["java","-jar","service-pipeline-boot-1.0.jar"]

# docker build . -t registry.cn-hangzhou.aliyuncs.com/wybioinfo/service-pipeline-boot-1.0.jar