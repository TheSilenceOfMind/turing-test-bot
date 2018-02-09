/**
 * github.com/TheSilenceOfMind
 * Copyright (c) 2018
 * All rights reserved.
 */

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.HashMap;


public class Bot extends TelegramLongPollingBot {
    private static HashMap<Integer, Integer> countOfGreetings;
    public static void main(String[] args) {
        countOfGreetings = new HashMap<>();
        ApiContextInitializer.init();
        TelegramBotsApi botapi = new TelegramBotsApi();
        try {
            botapi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
//            System.out.println(userId); // log info about user
            String greeting =
                    "Hello, ".concat(fullUserName)
                            .concat(" ! I've said you, that I'll **** you about ")
                            .concat(countOfGreetings.get(userId).toString())
                            .concat(" times!");
            sendMsg(msg, greeting);
        }
    }

    @Override
    public String getBotToken() {
        return "508094284:AAFQHS3WYVIFt_HIE5O3UvvEcrD-IlTEgXQ";
    }

    @SuppressWarnings("deprecation")
    private void sendMsg(Message msg, String text) {
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId());
        s.setText(text);
        try {
            sendMessage(s);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}