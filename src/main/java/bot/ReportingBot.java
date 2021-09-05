package bot;

import handlers.NewMessageHandler;
import hibernate.access.NotificationDao;
import notifications.MessageNotificator;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import stateMachine.AbstractBotState;
import utils.SendHelper;

import java.io.FileInputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ReportingBot extends TelegramLongPollingBot {

    final String token = "";

    private final String botName = "";

    public ReportingBot() {
        MessageNotificator notificator = new MessageNotificator(this);
        notificator.notificate();
        notificator.updateMessage();
    }

    private static final Logger logger = Logger.getLogger(ReportingBot.class.getName());

    @Override
    public void onUpdateReceived(Update update) {
        if (update != null && (update.getMessage() != null || update.getCallbackQuery() != null)) {
            NewMessageHandler handler = new NewMessageHandler(update, this);
            AbstractBotState botState = handler.getBotState();
            if (botState == null) {
                SendHelper.sendMessage(handler.getSendMessage(), handler.getContext());
                return;
            }
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
        LogManager.getLogManager().readConfiguration
                (new FileInputStream("./src/main/resources/logging.properties"));
        TelegramBotsApi botsApi = new TelegramBotsApi();
        ApiContextInitializer.init();
        try {
            botsApi.registerBot(new ReportingBot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }

    }
}
