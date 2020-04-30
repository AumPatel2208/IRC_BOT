# IRC BOT

## Setup

## Commands

- CMD_EXIT = "exit";
  - bot exits the server
  - if the bot is inside of game, it will exit game.
- CMD_HELLO = "hello";
  - will reply hello
- CMD_HACK = "hack";
  - will fake hack channel and display the active users
- CMD_ATTACK = "attack" <name>;
  - banish the named person
- CMD_PLAY = "play";
  - play an adventure game.

## TODO

- [x] improve showing game commands
  - [x] cheat sheet for all commands
- [ ] add more functions
  - [x] JOKE API (`https://sv443.net/jokeapi/v2`)
    - [x] Any joke but not nsfw, racist, or sexist, given in plain text`https://sv443.net/jokeapi/v2/joke/Any?blacklistFlags=nsfw,racist,sexist&format=txt`
  - [x] Motivation API (`https://www.affirmations.dev/`)
  - [x] NEWS API 
    - KEY : `dfdd63d38d604e4584f6392d8f8c053e`
    - URL :  `https://newsapi.org/v2/top-headlines?country=gb&apiKey=dfdd63d38d604e4584f6392d8f8c053e`