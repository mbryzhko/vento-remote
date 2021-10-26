ARG IMG
ARG JAR_FILE

FROM ${IMG}
MAINTAINER Maksym Bryzhko <maxim.bryzhko@gmail.com>

ADD target/${JAR_FILE} /usr/share/vento-remote/vento-remote.jar

ENTRYPOINT ["java", "-jar", "/usr/share/vento-remote/vento-remote.jar"]
