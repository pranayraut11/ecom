FROM ubuntu

RUN apt-get update && apt-get -y install openjdk-11-jdk

RUN mkdir /opt/source-code

COPY /target/order-0.0.1-SNAPSHOT.jar /opt/source-code

CMD ["java","-jar","/opt/source-code/order-0.0.1-SNAPSHOT.jar"]