package stateMachine;

import bot.BotContext;
import handlers.MainCommandsHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import utils.SendHelper;

public class NotificationChoice implements AbstractBotState{
    BotContext context;

    SendMessage sm;

    public NotificationChoice(BotContext context) {
        this.context = context;
    }

    @Override
    public void handleMessage() {
        MainCommandsHandler handler = new MainCommandsHandler(context);
        if ((sm = handler.handleTimeChoice()) != null) {
            question();
        }
    }

    @Override
    public void question() {
        SendHelper.sendMessage(sm, context);
    }
}
