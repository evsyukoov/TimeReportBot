package stateMachine;

import bot.BotContext;
import handlers.MainCommandsHandler;
import hibernate.access.ClientDao;
import messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import utils.SendHelper;

import java.util.Collections;

public class RegisterPosition implements AbstractBotState{
    BotContext context;

    SendMessage sm;

    public RegisterPosition(BotContext context) {
        this.context = context;
        sm = new SendMessage();
    }

    @Override
    public void handleMessage() {
        MainCommandsHandler handler = new MainCommandsHandler(context, State.REGISTER_NAME);
        if (handler.handle()) {
            question(handler.getResultToClient());
        } else {
            ClientDao.updatePosition(context.getClient().getUid(), State.CHOOSE_DAY.ordinal(),
                    State.REGISTER_DEPARTMENT.ordinal(),
                    context.getMessage());
            SendHelper.setInlineKeyboard(sm, Collections.emptyList(), Message.BACK);
            question(Message.CHOOSE_DAY);
        }
    }

    @Override
    public void question(String message) {
        SendHelper.sendMessage(sm, message, context);
    }
}
