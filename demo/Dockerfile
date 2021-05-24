FROM adoptopenjdk:11-jre-hotspot
VOLUME [ "/tmp" ]
ADD target/demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT exec java -jar app.jar