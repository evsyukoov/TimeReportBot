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
            sm.setText(handler.getResultToClient());
            question();
        } else {
            try {
                Utils.validateFio(context.getMessage());
            } catch (ValidationException e) {
                String msg = Utils.generateResultMessage(e.getMessage(), Message.REGISTER_NAME);
                sm.setText(msg);
                question();
                return;
            }
            ClientDao.updateName(context.getClient(), State.REGISTER_POSITION.ordinal(),
                    State.REGISTER_NAME.ordinal(), context.getMessage());
            SendHelper.setInlineKeyboard(sm, Message.departments, Message.BACK);
            sm.setText(Message.REGISTER_DEPARTMENT);
            question();
        }
    }

    @Override
    public void question() {
        SendHelper.sendMessage(sm, context);
    }
}
