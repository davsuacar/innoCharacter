FROM java:8

# Install sbt
RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823
RUN wget https://launchpad.net/ubuntu/+source/apt/0.9.7.7ubuntu6/+build/5072659/+files/apt-transport-https_0.9.7.7ubuntu6_amd64.deb && dpkg -i apt-transport-https_0.9.7.7ubuntu6_amd64.deb
RUN apt-get -y update
RUN apt-get install sbt
RUN apt-get install git


WORKDIR /code

ADD build.sbt /code/build.sbt
ADD src /code/src
ADD project /code/project

RUN ["sbt", "assembly"]

RUN wget http://d3kbcqa49mib13.cloudfront.net/spark-1.6.0-bin-hadoop2.6.tgz && tar xvf spark-1.6.0-bin-hadoop2.6.tgz

RUN spark-1.6.0-bin-hadoop2.6/bin/spark-submit --class AuthTwitter.AuthTwitter --master local[2] target/scala-2.10/innoCharacter-assembly-1.0.jar
