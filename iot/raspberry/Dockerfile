# build contianer
FROM gcc:14 as pigpio_builder

RUN apt update && \
    apt install -y python3-setuptools

# Follow the install guide from creator of pigpio, http://abyz.me.uk/rpi/pigpio/download.html
RUN wget https://github.com/joan2937/pigpio/archive/master.zip \
    && unzip master.zip \
    && cd pigpio-master \
    && make \
    && make install

# actual container
FROM eclipse-temurin:21-jre

# Install pigpio
COPY --from=pigpio_builder /usr/local /usr/local
RUN ldconfig

ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

ADD build/install/raspberry-jvm/ .

EXPOSE 8080
ENTRYPOINT ["bin/raspberry"]
