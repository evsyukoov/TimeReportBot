package stateMachine;

import bot.BotContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import utils.SendHelper;
import hibernate.access.ClientDao;
import messages.Message;

public class RegisterName implements AbstractBotState {

    private BotContext context;


    public RegisterName(BotContext context) {
        this.context = context;
    }

    @Override
    public void handleMessage() {

        ClientDao.updateState(context.getClient().getUid(), State.REGISTER_DEPARTMENT.ordinal());
        question(Message.REGISTER_NAME);
    }

    @Override
    public void question(String message) {
        SendMessage sm = new SendMessage();
        SendHelper.sendMessage(sm, message, context);
    }

}
