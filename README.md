# Twibot

Simple Twitter bot using ALICE AIML and Twitter4j. Mention to chat with demo bot: [@medi4sosial](https://twitter.com/medi4sosial)

## Build

** Dependencies **

Make sure all dependencies are installed:
* Java JDK 8
* Gradle

** Steps **

Follow these steps to activate the bot:
* Rename `src/main/resources/twitter4j.properties.example` to `src/main/resources/twitter4j.properties`
* Set all required Twitter keys and secrets in `src/main/resources/twitter4j.properties`
* Run `gradle build` inside directory

## Deployment

** Dependencies **

Make sure the server has Java JRE 8

** Steps **

* Build from source (refer to build guide)
* Copy `build/distributions/twibot-1.0.zip` to your server
* Extract zip
* Create cron job to execute bot eg. `5 * * * * /some/path/twibot-1.0/bin/twibot /some/path/twibot-1.0` (unix) or create scheduler to run `Drive:\some\path\twibot-1.0\bin\twibot.bat \some\path\twibot-1.0` (windows) 
