# [@Timecobot](https://t.me/timecobot)

Telegram bot provides a time-based currency-gun, `timecoins`.

* _Each active user accumulates time spent on Telegram_.
* Time can be passed on to other people.
* The accumulated time can be used to block another user.
* Time can be used to redeem the user from the ban.

To start using the bot, just add it to the group with admin rights.

## Time accumulation example

## Time transfer example

## Ban example

## Redeem example

## Build

```sh
./gradlew clean test shadowJar
```

Self-executable jar will be located in `build/libs`. Launch
example: `TOKEN=... java -jar build/libs/*.jar`

## Deploy

[![Deploy to DigitalOcean](https://www.deploytodo.com/do-btn-blue-ghost.svg)](https://cloud.digitalocean.com/apps/new?repo=https://github.com/demidko/timecobot/tree/main)

