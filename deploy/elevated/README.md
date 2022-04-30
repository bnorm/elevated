# Elevated Deployment Instructions

## Deployment

Deployment is automatic when a new tag is pushed to the repository via GitHub actions. The latest deployment files -
like the Nginx configuration or docker-compose file - can be manually copied with the follow:

```shell
$ scp -r deploy/* root@elevated.bnorm.dev:~/
```

## Docker

Make sure to pull the latest version of the Elevated service

```shell
$ docker compose pull service
```

Start all required services

```shell
$ docker compose up -d
```

## Nginx

### Setup

[Instructions for securing container via docker compose.][letsencypt-docker-compose]

### Reload

To reload the nginx configuration

```shell
$ docker compose exec -it nginx nginx -s reload
```

[letsencypt-docker-compose]: https://www.digitalocean.com/community/tutorials/how-to-secure-a-containerized-node-js-application-with-nginx-let-s-encrypt-and-docker-compose
