package stateMachine;

import bot.BotContext;
import com.google.inject.internal.cglib.core.$ClassInfo;
import handlers.MainCommandsHandler;
import hibernate.access.ClientDao;
import hibernate.access.NotificationDao;
import hibernate.access.ReportDaysDao;
import hibernate.entities.Client;
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
            sm.setText(Utils.generateResultMessage(Message.FINISH, Message.MENU));
            SendHelper.setInlineKeyboard(sm, Message.actionsMenu, null);
            ClientDao.updateDescription(context.getClient(), State.MENU_CHOICE.ordinal(), context.getMessage());
            ReportDaysDao.saveOrUpdate(context.getClient());
            NotificationDao.updateFireTime(context.getClient().getUid());
            ClientDao.clearClient(context.getClient());
            question();
        }
    }

    @Override
    public void question() {
        SendHelper.sendMessage(sm, context);
    }
}
