FROM openjdk:17
VOLUME /tmp
EXPOSE 8080
ARG WAR_FILE=target/todo-0.0.1-SNAPSHOT.war
ADD ${WAR_FILE} app.war
ENTRYPOINT ["java","-jar","/app.war"]