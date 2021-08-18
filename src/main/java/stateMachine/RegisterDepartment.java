package stateMachine;

import bot.BotContext;
import exceptions.ValidationException;
import handlers.MainCommandsHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import utils.SendHelper;
import hibernate.access.ClientDao;
import messages.Message;
import utils.Utils;

import java.util.Collection;
import java.util.Collections;

public class RegisterDepartment implements AbstractBotState{

    BotContext context;

    SendMessage sm;

    public RegisterDepartment(BotContext context) {
        this.context = context;
        sm = new SendMessage();
    }

    @Override
    public void handleMessage() {
        MainCommandsHandler handler = new MainCommandsHandler(context, State.REGISTER_NAME);
        if (handler.handle()) {
            question(handler.getResultToClient());
        } else {
            try {
                Utils.validateFio(context.getMessage());
            } catch (ValidationException e) {
                String msg = Utils.generateResultMessage(e.getMessage(), Message.REGISTER_NAME);
                question(msg);
                return;
            }
            ClientDao.updateName(context.getClient().getUid(), State.REGISTER_POSITION.ordinal(),
                    State.REGISTER_NAME.ordinal(), context.getMessage());
            SendHelper.setInlineKeyboard(sm, Collections.emptyList(), Message.BACK);
            question(Message.REGISTER_DEPARTMENT);
        }
    }

    @Override
    public void question(String quest) {
        SendHelper.sendMessage(sm, quest, context);
    }
}
