FROM debian:12-slim

RUN apt update && \
    apt install -y libcurl4-gnutls-dev && \
    apt install -y libgpiod2 libi2c0

ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

ADD build/bin/linuxArm64/releaseExecutable/ .

EXPOSE 8080
ENTRYPOINT ["./raspberry.kexe"]
