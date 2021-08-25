package utils;

import bot.BotContext;
import messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import stateMachine.RegisterName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class SendHelper {

    private static final Logger logger = Logger.getLogger(RegisterName.class.getName());

    public static void sendMessage(SendMessage sendMessage, BotContext context){
        sendMessage.setChatId(context.getClient().getUid());
        try {
            context.getBot().execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.severe(e.getCause() != null ? e.getCause().getMessage() : Message.ERROR_SEND_MESSAGE);
        }
    }

    public static synchronized void setInlineKeyboard(SendMessage sm, List<String> buttons, String message) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = null;
        for (int i = 0; i < buttons.size(); i++) {
            if (i % 2 == 0) {
                row = new ArrayList<>();
                rows.add(row);
            }
            row.add(new InlineKeyboardButton().setText(buttons.get(i))
                    .setCallbackData(buttons.get(i)));
        }
        if (message != null) {
            row = new ArrayList<>();
            rows.add(row);
            row.add(new InlineKeyboardButton().setText(message)
                    .setCallbackData(message));
        }
        inlineKeyboard.setKeyboard(rows);
        sm.setReplyMarkup(inlineKeyboard);
    }

}
