FROM openjdk:10.0.1-slim AS builder

# Env variables
ENV SBT_VERSION 1.3.10
# Install curl
RUN \
  apt-get update && \
  apt-get -y install curl
# Install sbt
RUN \
  curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt
WORKDIR /opt
ADD ./build.sbt /opt/build.sbt
ADD ./project /opt/project
RUN sbt reload
RUN sbt update
ADD ./src /opt/src
RUN sbt pack

FROM openjdk:10.0.1-slim
 # Install cassandra-driver
RUN apt-get update && apt-get install -y python3-pip --fix-missing
RUN pip install cassandra-driver
RUN \
  apt-get update && \
  apt-get -y install curl
# Install cqlsh
RUN \
  curl -O https://files.pythonhosted.org/packages/10/6e/a98135372c9b51e396f6f3d94f8d45027c888ce5f23ee269d2dd577af292/cqlsh-5.0.4.tar.gz && \
  tar xvf cqlsh-5.0.4.tar.gz && \
  mv cqlsh-5.0.4/* /opt/ && \
  chmod 777 /opt/cqlsh
COPY --from=builder /opt/target/ /opt/target
