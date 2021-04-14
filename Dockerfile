FROM lpicanco/java11-alpine
MAINTAINER Maksym Bryzhko <maxim.bryzhko@gmail.com>

## It should be build with --platform=linux/arm
#RUN apk update && \
#    apk add --no-cache tzdata

# Add the service itself
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/vento-remote/vento-remote.jar

ENTRYPOINT ["java", "-jar", "/usr/share/vento-remote/vento-remote.jar"]