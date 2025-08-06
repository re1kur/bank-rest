FROM rabbitmq:4.1.2-alpine

WORKDIR /etc/rabbitmq

COPY ./build/rabbit/init/definitions.json ./definitions.json
COPY ./build/rabbit/init/enabled_plugins ./enabled_plugins

ENV RABBITMQ_SERVER_ADDITIONAL_ERL_ARGS="-rabbitmq_management load_definitions '/etc/rabbitmq/definitions.json'"