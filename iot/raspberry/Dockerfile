# build contianer
FROM gcc as pigpio_builder
# Follow the install guide from creator of pigpio, http://abyz.me.uk/rpi/pigpio/download.html
RUN wget https://github.com/joan2937/pigpio/archive/master.zip \
    && unzip master.zip \
    && cd pigpio-master \
    && make \
    && make install

# actual container
FROM adoptopenjdk:11-jre-hotspot

# Install pigpio
COPY --from=pigpio_builder /usr/local /usr/local
RUN ldconfig

ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

ADD build/distributions/ .
RUN tar -xvf *.tar --strip 1

EXPOSE 8080
ENTRYPOINT ["bin/raspberry"]
