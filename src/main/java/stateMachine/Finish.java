package stateMachine;

import bot.BotContext;
import com.google.inject.internal.cglib.core.$ClassInfo;
import handlers.MainCommandsHandler;
import hibernate.access.ClientDao;
import hibernate.access.ProjectsDao;
import hibernate.access.ReportDaysDao;
import hibernate.entities.ReportDay;
import messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import utils.SendHelper;
import utils.Utils;

public class Finish implements AbstractBotState {

    BotContext context;

    SendMessage sm;

    public Finish(BotContext context) {
        this.context = context;
    }

    @Override
    public void handleMessage() {
        MainCommandsHandler handler = new MainCommandsHandler(context,
                State.SELECT_PROJECT, Message.SELECT_PROJECT);
        if ((sm = handler.handleBackButton()) != null) {
            question();
        } else {
            sm = new SendMessage();
            sm.setText(Utils.generateResultMessage(Message.FINISH, Message.CHOOSE_REPORT_TYPE));
            SendHelper.setInlineKeyboard(sm, Message.days, null);
            ClientDao.updateDescription(context.getClient(), State.CHOOSE_DAY.ordinal(), context.getMessage());
            ReportDaysDao.saveOrUpdate(context.getClient());
            ClientDao.clearClient(context.getClient());
            question();
        }
    }

    @Override
    public void question() {
        SendHelper.sendMessage(sm, context);
    }
}
