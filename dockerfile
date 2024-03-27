
#依赖的父镜像
FROM ubuntu:jammy 
WORKDIR /opt

RUN sed -i 's/http:\/\/archive.ubuntu.com/http:\/\/mirrors.aliyun.com/g' /etc/apt/sources.list
RUN sed -i 's/http:\/\/security.ubuntu.com/http:\/\/mirrors.aliyun.com/g' /etc/apt/sources.list
RUN apt-get update
RUN apt-get install openjdk-11-jre  gnupg curl -y
RUN apt-get install -y systemd
RUN curl -fsSL https://www.mongodb.org/static/pgp/server-7.0.asc | \
    gpg -o /usr/share/keyrings/mongodb-server-7.0.gpg \
   --dearmor
RUN echo "deb [ arch=amd64,arm64 signed-by=/usr/share/keyrings/mongodb-server-7.0.gpg ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" |  tee /etc/apt/sources.list.d/mongodb-org-7.0.list
RUN apt-get update
RUN apt-get install -y mongodb-org
RUN apt-get install -y net-tools
# RUN passw  root 
USER root
RUN echo "root:123456" | chpasswd
CMD ["/sbin/init"]
 
# docker run --rm -it  --privileged  -u root \
#     -p 27018:27017 \
#     -v /var/run/docker.sock:/var/run/docker.sock \
#     -v  /usr/bin/docker:/usr/bin/docker \
#     -v /etc/docker/daemon.json:/etc/docker/daemon.json \
#     -v /var/lib/docker:/var/lib/docker \
#     -v $PWD:$PWD \
#     pipeline-proj  