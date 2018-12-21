FROM openjdk:8
LABEL maintainer="marc.reichelt@iteratec.de"

# limit memory for Java process
ENV JAVA_TOOL_OPTIONS="-Xmx128m -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap"

RUN groupadd -g 999 officemap && \
    useradd -r -u 999 -g officemap officemap && \
    mkdir -p /var/app

COPY build/libs/*.jar /var/app/app.jar

RUN chown -R officemap:officemap /var/app

USER officemap

EXPOSE 8080

WORKDIR "/var/app"

ENTRYPOINT ["java", "-jar", "app.jar"]
