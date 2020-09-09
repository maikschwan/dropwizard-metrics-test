FROM openjdk:11.0.5-jre

# Get the latests updates
RUN set -x \
    \
    echo "===> Get latest system updates..."                                                            && \
    apt-get update -yq                                                                                  && \
    apt-get upgrade -yq                                                                                 && \
    \
    \
    apt-get clean                                                                                       && \
    rm -rf /var/lib/apt/lists/*

COPY target/testmetrics.jar /
COPY config.yml /
COPY run.sh /
RUN chmod +x /run.sh

EXPOSE 8080 8081

WORKDIR /
# Set the user to 'nobody' with a minimal set of permissions
USER 65534
ENTRYPOINT ["./run.sh"]
