

# Dummy Word Game

## Rules

The rules are simple. There are 2 players and they make moves 1 by 1.  

When a player's turn comes, he repeats all the words that have been said in the game in the order they had been said and adds 1 word. The player who can’t repeat all the words (in correct order) she looses (because it’s not always he :)) ) the game and the other one wins. If the player adds a word that had already been played, the player looses the game.

If a player adds more then one word at a time, the player looses the game. 

The game continues until one of them looses, there is no tie. 

A word is any sequence of characters except a whitespace (Yes “a\/n” is still a word, what can I say, it’s a dummy game). 

Each word should be separated by whitespace ( “sad monkey” is 2 words, “sad “ is 1 word, “ “ - nobody talks like that, so it’s not a word) 

#### Ex 1 : 

PL-1 : tree 

PL-2 : tree sa 

PL-1 : tree sa qa 

PL-2 : tree sa qa ra 

PL-1 : tree sa qa rt velo - > “rt” is incorrect, should have been “ra” so “PL-1” looses the game. 

#### Ex 2:  

PL-1 : tree 

PL-2 : tree tree repeating isn’t allowed so “PL-2” looses the game 

#### Ex 3: 

PL-1 : tree a 2 words so looses game


## Installation

Clone project to Android Studio

#### install bot

While running bot on Android Studio, you will get an error, since this app doesn't have an Activity and only runs in background

All you have to do is modify launch options:

bot -> Edit Configurations -> Launch Options -> Launch -> Nothing

#### install user

For user to work, at first, you need to install bot
