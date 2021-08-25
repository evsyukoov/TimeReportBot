package stateMachine;

import bot.BotContext;
import handlers.MainCommandsHandler;
import hibernate.access.ClientDao;
import messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import utils.SendHelper;
import utils.Utils;

public class ReportType implements AbstractBotState{
    BotContext context;

    SendMessage sm;

    public ReportType(BotContext context) {
        this.context = context;
        sm = new SendMessage();
    }

    @Override
    public void handleMessage() {
        // после успешной регистрации старт всегда начинается отсюда
        MainCommandsHandler handler = new MainCommandsHandler(context,
                context.getClient().isRegistered() ? State.REPORT_TYPE : State.REGISTER_NAME);
        if (handler.handle()) {
            sm.setText(handler.getResultToClient());
            if (handler.getResultToClient().equals(Message.REGISTER_DEPARTMENT)) {
                SendHelper.setInlineKeyboard(sm, Message.departments, Message.BACK);
            }
            question();
        } else {
            if (!context.getClient().isRegistered()) {
                sm.setText(Utils.generateResultMessage(Message.REGISTER_IS_FINISHED, Message.CHOOSE_REPORT_TYPE));
                ClientDao.updatePosition(context.getClient(), State.CHOOSE_DAY.ordinal(),
                        State.REPORT_TYPE.ordinal(), context.getMessage(), true);
            } else {
                sm.setText(Message.CHOOSE_REPORT_TYPE);
            }
            SendHelper.setInlineKeyboard(sm, Message.days, null);
            question();
        }
    }

    @Override
    public void question() {
        SendHelper.sendMessage(sm,  context);
    }
}
