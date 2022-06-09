FROM ubuntu

RUN apt-get update && apt-get -y install  openjdk-11-jdk

RUN mkdir /opt/source-code

COPY ./target/cart-0.0.1-SNAPSHOT.jar /opt/source-code/

CMD ["java","-jar","/opt/source-code/cart-0.0.1-SNAPSHOT.jar"]