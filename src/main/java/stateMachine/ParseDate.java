package stateMachine;

import bot.BotContext;
import handlers.MainCommandsHandler;
import messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import utils.SendHelper;

public class ParseDate implements AbstractBotState {

    BotContext context;

    SendMessage sm;

    public ParseDate(BotContext context) {
        this.context = context;
    }

    @Override
    public void handleMessage() {
        MainCommandsHandler handler = new MainCommandsHandler(context,
                State.CHOOSE_DAY, Message.CHOOSE_REPORT_TYPE);
        if ((sm = handler.handleBackButton()) != null) {
            question();
        } else if ((sm = handler.parseDate()) != null) {
            question();
        }
    }

    @Override
    public void question() {
        SendHelper.sendMessage(sm, context);
    }
}
