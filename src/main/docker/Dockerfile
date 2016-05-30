FROM java:openjdk-8-jre-alpine

ENV JHIPSTER_SLEEP 0

# add directly the war
ADD *.war /app.war


ADD myapp_keyAndCertBundle.jks /myapp_keyAndCertBundle.jks
ADD userpass.properties /userpass.properties

RUN sh -c 'touch /app.war'
RUN sh -c 'touch /myapp_keyAndCertBundle.jks'
RUN sh -c 'touch /userpass.properties'

ENV  APP_KEYSTORE=myapp_keyAndCertBundle.jks
ENV APP_APIUSERPASS=userpass.properties

VOLUME /tmp
EXPOSE 8080
CMD echo "The application will start in ${JHIPSTER_SLEEP}s..." && \
    sleep ${JHIPSTER_SLEEP} && \
    java -Djava.security.egd=file:/dev/./urandom -jar /app.war
