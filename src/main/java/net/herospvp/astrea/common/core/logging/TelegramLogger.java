package net.herospvp.astrea.common.core.logging;

import lombok.SneakyThrows;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

public class TelegramLogger {

    private final String chatId;
    private final String botToken;

    public TelegramLogger(String chatId, String botToken) {
        this.chatId = chatId;
        this.botToken = botToken;
    }

    @SneakyThrows
    public void sendMessage(String args) {
        String telegramUrl = "https://api.telegram.org/bot";
        String site = telegramUrl + botToken + "/sendMessage?chat_id=" + chatId + "&text=" + args;
        HttpsURLConnection httpsURLConnection = null;
        try {
            URL url = new URL(site);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setRequestMethod("POST");
            httpsURLConnection.setUseCaches(false);
            httpsURLConnection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream (httpsURLConnection.getOutputStream());
            wr.writeBytes(site);
            wr.close();

            InputStream is = httpsURLConnection.getInputStream();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
        }
    }

}
