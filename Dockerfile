FROM eclipse-temurin:17.0.13_11-jre-noble
LABEL org.opencontainers.image.authors="Maksym Bryzhko <maxim.bryzhko@gmail.com>"

ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/vento-remote/vento-remote.jar

ENTRYPOINT ["java", "-jar", "/usr/share/vento-remote/vento-remote.jar"]
