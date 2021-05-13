# Timecobot

Telegram bot provides time-based currency. The accumulated time can be used to block another user.
There are no discriminatory rules, administrators and moderators.

1. Each features user accumulates time. Minute by minute, hour by hour.
1. Time can be passed on to other people.
1. The accumulated time can be used to block another user.
1. Time can be used to ransom the user from the ban.

## Usage

To start using the system, just add [`@Timecobot`](https://t.me/timecobot) to the group with admin
rights. Bot understands spoken language (english and russian). Experiment!

### Check yourself status

![](examples/status.jpg "My status")

### Transfer time to user

![](examples/transfer.jpg "Transfer time to user")

### Ban user

![](examples/ban.jpg "Ban user")

### User ransom

![](examples/ransom.jpg "User ransom")

## Build

```sh
./gradlew clean test shadowJar
```

Self-executable jar will be located in `build/libs`. To start long polling execute command

```sh
TOKEN=... java -jar build/libs/*.jar
```

## Deploy

[![Deploy to DigitalOcean](https://www.deploytodo.com/do-btn-blue-ghost.svg)](https://cloud.digitalocean.com/apps/new?repo=https://github.com/demidko/timecobot/tree/main)

You can use a Redis cluster to store the time: specify the Redis connection string on the target
machine via the `DATABASE_URL` environment variable.

