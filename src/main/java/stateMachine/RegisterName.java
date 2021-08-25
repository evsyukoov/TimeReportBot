package stateMachine;

import bot.BotContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import utils.SendHelper;
import hibernate.access.ClientDao;
import messages.Message;

public class RegisterName implements AbstractBotState {

    private BotContext context;

    SendMessage sm;

    public RegisterName(BotContext context) {
        this.context = context;
        sm = new SendMessage();
    }

    @Override
    public void handleMessage() {
        ClientDao.updateState(context.getClient(), State.REGISTER_DEPARTMENT.ordinal());
        sm.setText(Message.REGISTER_NAME);
        question();
    }

    @Override
    public void question() {
        SendHelper.sendMessage(sm, context);
    }

}
