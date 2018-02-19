package ru.itmo.telegram.api; /**
 * github.com/TheSilenceOfMind
 * Copyright (c) 2018
 * All rights reserved.
 */

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;


public class Bot extends TelegramLongPollingBot {
    public static final String TOKEN_FILENAME = "src/main/resources/token.txt";
    private static HashMap<Integer, Integer> countOfGreetings;

    @SneakyThrows
    public static void main(String[] args) {
        countOfGreetings = new HashMap<>();
        ApiContextInitializer.init();
        new TelegramBotsApi().registerBot(new Bot());
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
        try {
            return new String(Files.readAllBytes(Paths.get(fileName)));
        }
        catch (IOException e) {
            return "";
        }

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
            e.printStackTrace();
        }
    }
}