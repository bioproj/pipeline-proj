
#依赖的父镜像
FROM adoptopenjdk/openjdk11:jre-nightly


WORKDIR /opt

COPY service-pipeline-admin/target/service-pipeline-admin-1.0.jar service-pipeline-admin-1.0.jar
COPY docker_config/* /opt/

CMD ["java","-jar","service-pipeline-admin-1.0.jar"]
