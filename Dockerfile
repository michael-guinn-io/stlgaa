FROM quay.io/wildfly/wildfly
COPY . .
RUN mvn clean install
RUN /opt/jboss/wildfly/bin/add-user.sh admin 1Ju9AI7j --silent
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "-Djboss.socket.binding.port-offset = 5 "]