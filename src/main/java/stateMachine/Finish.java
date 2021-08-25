package stateMachine;

import bot.BotContext;
import handlers.MainCommandsHandler;
import hibernate.access.ClientDao;
import hibernate.access.ProjectsDao;
import messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import utils.SendHelper;
import utils.Utils;

public class Finish implements AbstractBotState {

    BotContext context;

    SendMessage sm;

    public Finish(BotContext context) {
        this.context = context;
        sm = new SendMessage();
    }

    @Override
    public void handleMessage() {
        MainCommandsHandler handler = new MainCommandsHandler(context, State.REPORT_TYPE);
        if (handler.handle()) {
            sm.setText(handler.getResultToClient());
            if (handler.getResultToClient().equals(Message.SELECT_PROJECT)) {
                SendHelper.setInlineKeyboard(sm, ProjectsDao.getAllProjectsNames(), Message.BACK);
            }
            question();
        } else {
            sm.setText(Utils.generateResultMessage(Message.FINISH, Message.CHOOSE_REPORT_TYPE));
            SendHelper.setInlineKeyboard(sm, Message.days, null);
            ClientDao.updateDescription(context.getClient(), State.REPORT_TYPE.ordinal(),
                    State.REPORT_TYPE.ordinal(), context.getMessage());
            question();
        }
    }

    @Override
    public void question() {
        SendHelper.sendMessage(sm, context);
    }
}
