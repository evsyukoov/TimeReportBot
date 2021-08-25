package stateMachine;

import bot.BotContext;
import exceptions.ValidationException;
import handlers.MainCommandsHandler;
import hibernate.access.ClientDao;
import messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import utils.SendHelper;
import utils.Utils;

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
            sm.setText(handler.getResultToClient());
            question();
        } else {
            try {
                Utils.validateDepartment(context.getMessage());
            } catch (ValidationException e) {
                String msg = Utils.generateResultMessage(e.getMessage(), Message.REGISTER_DEPARTMENT);
                sm.setText(msg);
                SendHelper.setInlineKeyboard(sm, Message.departments, Message.BACK);
                question();
                return;
            }
            ClientDao.updateDepartment(context.getClient(), State.REPORT_TYPE.ordinal(),
                    State.REGISTER_DEPARTMENT.ordinal(),
                    context.getMessage());
            sm.setText(Message.REGISTER_POSITION);
            SendHelper.setInlineKeyboard(sm, Collections.emptyList(), Message.BACK);
            question();
        }
    }

    @Override
    public void question() {
        SendHelper.sendMessage(sm, context);
    }
}
