FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/todo.jar /todo/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/todo/app.jar"]
