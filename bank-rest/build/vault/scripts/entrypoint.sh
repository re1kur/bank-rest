#!/bin/sh
vault server -dev &
sleep 2
export VAULT_ADDR=http://127.0.0.1:8200
export VAULT_TOKEN=${VAULT_DEV_ROOT_TOKEN_ID}
vault secrets enable transit
vault write -f transit/keys/${JWT_PATH_SIGN_KEY} type=rsa-2048 exportable=true allow_signing=true
tail -f /dev/null