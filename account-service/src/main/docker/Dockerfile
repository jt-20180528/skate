FROM anapsix/alpine-java:8
VOLUME /tmp
ADD account-service-master-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Xmx50m -Xms50m","/app.jar"]