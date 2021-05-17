# Timecobot

Telegram bot provides time-based currency. The accumulated time can be used to block another user.
There are no discriminatory rules, administrators and moderators.

1. Each user accumulates time. Minute by minute, hour by hour.
1. Time can be passed on to other people.
1. The accumulated time can be used to block another user.  
   User will remain in the chat, but user will not be able to write anything.
1. Time can be used to ransom the user from the ban.
1. Time can be used to pin messages.

## Usage

To start using the system, just add [`@timecobot`](https://t.me/timecobot) to the group with admin
rights. Bot understands spoken language (english and russian). Experiment!

### Check yourself status

![](img/status.jpg "My status")

### Transfer time to user

![](img/transfer.jpg "Transfer time to user")

### Block user

User will remain in the chat, but he will not be able to write anything.

![](img/ban.jpg "Block user")

### Unblock user

![](img/ransom.jpg "Unblock user")

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

You can use a Redis cluster to store the time: specify the connection string via the `DATABASE_URL`
environment variable.

