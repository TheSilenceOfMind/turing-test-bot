package ru.itmo.telegram.api;

/**
 * github.com/TheSilenceOfMind
 * Copyright (c) 2018
 * All rights reserved.
 */

import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

@Log4j2
public class Bot extends TelegramLongPollingBot {
    private static final String settingsFilename = "/settings.properties";
    private String botUsername;
    private String token;

    private static HashMap<Integer, Integer> countOfGreetings;

    public static void main(String[] args) {
        countOfGreetings = new HashMap<>();
        ApiContextInitializer.init();
        try {
            new TelegramBotsApi().registerBot(new Bot());
        }
        catch (Exception e) {
            log.error("RegisterBot exception in main()", e);
        }
    }

    private Bot() {
        Properties prop = new Properties();
        try(InputStream inputStream = Bot.class.getResourceAsStream(settingsFilename)) {
            prop.load(inputStream);
            botUsername = prop.getProperty("bot_username");
            token = prop.getProperty("token");
            log.info("property-field initialized!");
        } catch (Exception e) {
            log.fatal("Couldn't load props file!", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update e) {
        Message msg = e.getMessage();
        String requestText = msg.getText();

        // retrieve info about user to process data and make a response
        User user = msg.getFrom();
        Integer userId = user.getId();

        if (requestText.equals("/start")) {
            if (!countOfGreetings.containsKey(userId)) {
                countOfGreetings.put(userId, 1);
            } else {
                countOfGreetings.put(userId, countOfGreetings.get(userId) + 1);
            }
            String fullUserName =
                    (user.getFirstName() == null ? "" : user.getFirstName()) + " " +
                            (user.getLastName() == null ? "" : user.getLastName());
            String greeting =
                    "Hello, ".concat(fullUserName)
                            .concat(" ! I've said you, that I'll **** you about ")
                            .concat(countOfGreetings.get(userId).toString())
                            .concat(" times!");
            sendMsg(msg, greeting);
        }
    }

    private void sendMsg(Message msg, String text) {
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId());
        s.setText(text);
        try {
            //noinspection deprecation
            sendMessage(s);
        } catch (TelegramApiException e){
            log.error("send msg exception", e);
        }
    }
}