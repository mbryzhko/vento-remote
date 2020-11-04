FROM lpicanco/java11-alpine
MAINTAINER Maksym Bryzhko <maxim.bryzhko@gmail.com>

# Add the service itself
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/vento-remote/vento-remote.jar

ENTRYPOINT ["java", "-jar", "/usr/share/vento-remote/vento-remote.jar"]