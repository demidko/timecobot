# Timecobot

*TODO: blockchain (time)*

Telegram bot provides time-based currency. The accumulated time can be used to block another user.
There are no discriminatory rules, administrators and moderators. Bot also can be used to pin
messages.

1. Each user accumulates time.
2. Time can be passed on to other people.
3. The accumulated time can be used to block another user.  
   User will remain in the chat, but user will not be able to write anything.
4. Time can be used to ransom the user from the ban.
5. Time can be used to pin messages.

## Usage

To start using the system, just add [`@timecobot`](https://t.me/timecobot) to the group with admin
rights. Bot understands spoken language (english and russian). Experiment!

## Examples

![](img/status.jpg "My status")  
![](img/transfer.jpg "Transfer time to user")  
User will remain in the chat, but he will not be able to write anything:  
![](img/ban.jpg "Block user")  
![](img/ransom.jpg "Unblock user")

## Build

```sh
./gradlew clean build
```

Self-executable jar will be located in `build/libs`. To start long polling execute command

```sh
BACKUP_CHANNEL=mybackup_channel TOKEN=4760:zGTAaKGo java -jar build/libs/*-all.jar
```

## Deploy

[![Deploy to DigitalOcean](https://www.deploytodo.com/do-btn-blue-ghost.svg)](https://cloud.digitalocean.com/apps/new?repo=https://github.com/demidko/timecobot/tree/main)

