package stateMachine;

import bot.BotContext;
import handlers.MainCommandsHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import utils.SendHelper;

public class MenuChoice implements AbstractBotState{

    BotContext context;

    SendMessage sendMessage;

    public MenuChoice(BotContext context) {
        this.context = context;
    }

    @Override
    public void handleMessage() {
        MainCommandsHandler handler = new MainCommandsHandler(context);
        if ((sendMessage = handler.handleMenuChoice()) != null) {
            SendHelper.refreshInlineKeyboard(context);
            question();
        }
    }

    @Override
    public void question() {
        SendHelper.sendMessage(sendMessage, context);
    }
}
