package stateMachine;

import bot.BotContext;
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
        ClientDao.updateState(context.getClient().getUid(), State.REGISTER_DEPARTMENT);
        question(Message.REGISTER_NAME);
    }

    @Override
    public void question(String message) {
        SendHelper.sendMessage(message, context);
    }

}
