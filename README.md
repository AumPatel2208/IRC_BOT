# IRC BOT

## Build/Run
> Java 11 required as used the java 11 HTTP libraries
- Inside project directory
- `javac IrcMain.java`
- `java IrcMain`
- enter the correct IP address, port number, and channel name to join
- Enjoy.

## Commands that the bot will accept 
> to be prefixed by the trigger or nickname of the bot
- `hello`: replies by saying `hello <username>`
- `play`: play an adventure game
- `news`: get the current top 5 news articles
- `joke`: tells you a joke (safe for work ofc)
- `inspire`/`knowledge`/`insight`/etc...: Will give you inspiration
- `hack`: fun little hacking display
- `attack` <username>: it will kick that user out of the channel if the bot has operator privileges
- `leave`/`part`: Bot leaves/parts the channel, if the bot is only in one channel, then the bot will tell the user to use exit if they want to quit the bot
- `exit`: Bot Closes its connection and exits the server (if in game, then exits game and makes other commands active again.) ";

## IRC Protocol commands used
-  `NICK` - used to set the bot's nick name
-  `USER` - used to set the bot's username when first joining the server, to make it uniquely identifiable.
-  `JOIN` - used to make the bot join the original channel and used to make the bot join another channel when `bb join <channel>`
-  `LIST` - used when user enters `bb list` to list all the servers out.
-  `NOTICE` - used when the bot enters the channel to notify everyone he is in.
-  `PRIVMSG` - used to send messages into the channel the bot is being used in.
-  `PONG` - used when the server sends a `PING` message to the bot.
-  `KICK` - used when the user enters `bb attack <user>`.
-  `PART` - used by the bot to leave the channel
-  `QUIT` - used when user enters `bb exit`.