package stateMachine;

import bot.BotContext;
import exceptions.ValidationException;
import utils.SendHelper;
import hibernate.access.ClientDao;
import messages.Message;
import utils.Utils;

public class RegisterDepartment implements AbstractBotState{

    BotContext context;

    public RegisterDepartment(BotContext context) {
        this.context = context;
    }

    @Override
    public void handleMessage() {
        String receive = context.getUpdate().getMessage().getText();
        if (receive.equals(Message.START) || receive.equals(Message.STOP)) {
            ClientDao.updateState(context.getClient().getUid(), State.REGISTER_NAME);
            question(Message.REGISTER_NAME);
        } else {
            try {
                Utils.validateFio(receive);
            } catch (ValidationException e) {
                question(Utils.generateResultMessage(e.getMessage(),Message.REGISTER_NAME));
                return;
            }
            ClientDao.updateName(context.getClient().getUid(), State.REGISTER_POSITION, receive);
            question(Message.REGISTER_DEPARTMENT);
        }
    }

    @Override
    public void question(String quest) {
        SendHelper.sendMessage(quest, context);
    }
}
