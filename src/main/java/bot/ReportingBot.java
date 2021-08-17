package bot;

import handlers.NewMessageHandler;
import hibernate.access.ClientDao;
import hibernate.entities.Client;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import stateMachine.AbstractBotState;

import java.io.FileInputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ReportingBot extends TelegramLongPollingBot {

    final String token = "";

    private final String botName = "";

    private static final Logger logger = Logger.getLogger(ReportingBot.class.getName());

    @Override
    public void onUpdateReceived(Update update) {
        if (update != null && (update.getMessage() != null || update.getInlineQuery() != null)) {
            NewMessageHandler handler = new NewMessageHandler(update, this);
            AbstractBotState botState = handler.getBotState();
            botState.handleMessage();
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public static void main(String[] args) throws Exception {
        LogManager.getLogManager().readConfiguration(new FileInputStream("./src/main/resources/logging.properties"));
        TelegramBotsApi botsApi = new TelegramBotsApi();
        ApiContextInitializer.init();
        try {
            botsApi.registerBot(new ReportingBot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
