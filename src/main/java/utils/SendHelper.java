package utils;

import bot.BotContext;
import messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import stateMachine.RegisterName;

import java.util.logging.Logger;

public class SendHelper {

    private static final Logger logger = Logger.getLogger(RegisterName.class.getName());

    public static void sendMessage(String text, BotContext context){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(context.getClient().getUid());
        sendMessage.setText(text);
        try {
            context.getBot().execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.severe(e.getCause() != null ? e.getCause().getMessage() : Message.ERROR_SEND_MESSAGE);
        }
    }

}
