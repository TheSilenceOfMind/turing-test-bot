package ru.itmo.telegram.api;

/**
 * github.com/TheSilenceOfMind
 * Copyright (c) 2018
 * All rights reserved.
 */

import java.io.File;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.History;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.utils.IOUtils;

@Log4j2
class TextProcessor {
    private static final boolean TRACE_MODE = false;
    static String botName = "super";
    private Bot bot;
    private Chat chatSession;

    TextProcessor() {
        String resourcesPath = getResourcesPath();
        MagicBooleans.trace_mode = TRACE_MODE;
        bot = new Bot("super", resourcesPath);  // ALICE bot, not mine!
        chatSession = new Chat(bot);
        bot.brain.nodeStats();
    }

    /**
     * This function is
     *
     * @param requestText - user's input
     * @return - response to user input
     */
    public String generateAnswer(String requestText) {
        String response = "";
        if ((requestText == null) || (requestText.length() < 1))
            requestText = MagicStrings.null_input;
        if (requestText.equals("wq"))
            bot.writeQuit();
        else {
            if (MagicBooleans.trace_mode)
                log.info(
                        "STATE=" + requestText
                                + ":THAT=" + chatSession.thatHistory.get(0).get(0)
                                + ":TOPIC=" + chatSession.predicates.get("topic")
                );
            response = chatSession.multisentenceRespond(requestText);
            while (response.contains("&lt;"))
                response = response.replace("&lt;", "<");
            while (response.contains("&gt;"))
                response = response.replace("&gt;", ">");
        }
        return response;
    }

    /**
     * The func used to return absolute path of project resource folder
     *
     * @return absolute path to resource dir in the project (/src/main/resources)
     */
    private static String getResourcesPath() {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        path = path.substring(0, path.length() - 2);
        return path + File.separator + "src" + File.separator + "main" + File.separator + "resources";
    }
}