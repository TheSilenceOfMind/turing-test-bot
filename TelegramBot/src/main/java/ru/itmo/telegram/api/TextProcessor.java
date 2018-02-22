package ru.itmo.telegram.api;

/**
 * github.com/TheSilenceOfMind
 * Copyright (c) 2018
 * All rights reserved.
 */

import java.io.File;

import com.github.kevinsawicki.http.HttpRequest;
import lombok.extern.log4j.Log4j2;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.json.JSONObject;

/**
 * The TextProcessor class uses simultaneously 2 external APIs:
 * 1) AIML to generate response to user request text
 * https://howtodoinjava.com/ai/java-aiml-chatbot-example/
 * <p>
 * 2) Yandex "API" using GET-request with specified params
 * https://tech.yandex.com/translate/doc/dg/concepts/About-docpage/
 */
@Log4j2
class TextProcessor {
    private static final boolean TRACE_MODE = false;
    private static final String URL_YANDEX_TRANSLATE = "https://translate.yandex.net/api/v1.5/tr.json/translate";
    private static final String URL_YANDEX_DETECT_LANG = "https://translate.yandex.net/api/v1.5/tr.json/detect";
    private Bot bot;
    private Chat chatSession;
    private static String apiKey;

    TextProcessor(String botName, String apiKey) {
        TextProcessor.apiKey = apiKey;
        String resourcesPath = getResourcesPath();
        MagicBooleans.trace_mode = TRACE_MODE;
        bot = new Bot(botName, resourcesPath);  // ALICE bot, not mine!
        chatSession = new Chat(bot);
        bot.brain.nodeStats();
    }

    /**
     * take user's text, detect it's original language,
     * translate text to English language using Yandex API
     * (because AIML API works only with English tokens),
     * generate response in English, translate it to user's language
     * and return it
     *
     * @param requestText - user's input in original language
     * @return - response to user in user's language
     */
    public String generateAnswer(String requestText) {
        String userLangCode = detectLanguageCode(requestText);
        String response;
        requestText = getTranslation(requestText, "en");
        if ((requestText == null) || (requestText.length() < 1))
            requestText = MagicStrings.null_input;
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
        log.info(response);
        response = getTranslation(response, userLangCode);
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

    /**
     * The function get the text and text in needed language code format.
     * It translates it using request yandex translate and returns translated text
     *
     * @param text         original text
     * @param languageCode to which language is needed to translate
     * @return translated text
     */
    private static String getTranslation(String text, String languageCode) {
        String response = HttpRequest.get(
                URL_YANDEX_TRANSLATE, true,
                "key", apiKey,
                "text", text,
                "lang", languageCode
        ).body(); // get URL_YANDEX_TRANSLATE?key=...&text=...&lang=...

        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.getInt("code") == 200) {
            response = jsonObject.getJSONArray("text").getString(0);
            log.info(response);
        } else {
            log.error(response);
        }
        return response;
    }

    /**
     * The function detects language of the text and returns its code
     *
     * @param text original text
     * @return langCode of the text ("en", "ru", etc.)
     */
    private static String detectLanguageCode(String text) {
        String response = HttpRequest.get(
                URL_YANDEX_DETECT_LANG, true,
                "key", apiKey,
                "text", text
        ).body(); // get URL_YANDEX_DETECT_LANG?key=...&text=...

        JSONObject jsonObject = new JSONObject(response);
        if (jsonObject.getInt("code") == 200) {
            response = jsonObject.getString("lang");
            log.info(response);
        } else {
            log.error(response);
        }
        return response;
    }
}