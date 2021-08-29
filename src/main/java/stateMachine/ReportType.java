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
    }

    @Override
    public void handleMessage() {
        // после успешной регистрации старт всегда начинается отсюда
        MainCommandsHandler handler = new MainCommandsHandler(context,
                State.REGISTER_POSITION, Message.REGISTER_DEPARTMENT);
        if ((sm = handler.handleBackButton()) != null) {
            question();
        } else {
            sm = new SendMessage();
            if (!context.getClient().isRegistered()) {
                sm.setText(Utils.generateResultMessage(Message.REGISTER_IS_FINISHED, Message.CHOOSE_REPORT_TYPE));
                ClientDao.updatePosition(context.getClient(), State.CHOOSE_DAY.ordinal(),
                         context.getMessage(), true);
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
