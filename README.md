# turing-test-bot

## About

This simple project is created to maintain telegram-bot functionality including:
* texting with user on any language supported by Yandex translate service;

If you don't have an Yandex API key, you could:
* text with bot using only English language without misunderstanding on the both sides;
* get an Yandex API key by registration an account in Yandex (_it's totally free!_);

## Install and run bot

1) download code from ```TelegramBot``` directory and import it using Maven;
2) fill fields values in ```settings.properties```;
3) run the project!

## Notes

If you're going to run **```.jar```** file with all features of the bot replicas, you need to place in the same directory the following files:

* ```src/main/resources/bots/*``` directory;
* ```.jar``` file; 

So, the certain directory would be seen like this:
```
...
Example.jar
src
...
```

It's caused by features of the ready AIML library.
