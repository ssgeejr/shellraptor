FROM mysql:5.7
MAINTAINER Steve Gee <ioexcept@gmail.com>

ADD tools.tar /opt

ENV MYSQL_USER="raptor" \
MYSQL_DATABASE="raptor" \
MYSQL_CONTAINER_NAME="raptormysql" \
MYSQL_ROOT_PASSWORD="raptor" \
JAVA_HOME="/opt/jdk1.8.0_60" \
M2_HOME="/opt/maven-3.3.3" 

ENV PATH "$JAVA_HOME/bin:$M2_HOME/bin:$PATH"

COPY seed.data.sql /docker-entrypoint-initdb.d/
