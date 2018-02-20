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
import java.util.Scanner;

@Log4j2
public class Bot extends TelegramLongPollingBot {
    private static final String TOKEN_FILENAME = "/token.txth";
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

    @Override
    public String getBotUsername() {
        return "test_the_silence_bot";
    }

    @Override
    public void onUpdateReceived(Update e) {
        Message msg = e.getMessage();
        String txt = msg.getText();

        // retrieve info about user to process data and make a response
        User user = msg.getFrom();
        Integer userId = user.getId();

        if (txt.equals("/start")) {
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

    private String getFileContent(String fileName) {
        String res = null;
        try(InputStream inputStream = Bot.class.getResourceAsStream(fileName)) {
            Scanner sc = new Scanner(inputStream).useDelimiter("\\n");
            while (sc.hasNext()) {
                res = sc.next();
            }
        } catch (Exception e) {
            log.error("get token exception", e);
        }
        return res;
    }

    @Override
    public String getBotToken() {
        return getFileContent(TOKEN_FILENAME);
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