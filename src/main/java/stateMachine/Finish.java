package stateMachine;

import bot.BotContext;
import handlers.MainCommandsHandler;
import hibernate.access.ClientDao;
import hibernate.access.NotificationDao;
import hibernate.access.ReportDaysDao;
import messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import utils.SendHelper;
import utils.Utils;

public class Finish extends AbstractBotState {

    public Finish(BotContext context) {
        super(context);
    }

    @Override
    public void handleMessage() {
        MainCommandsHandler handler = new MainCommandsHandler(context,
                State.SELECT_PROJECT, Message.SELECT_PROJECT);
        if ((sm = handler.handleBackButton()) != null) {
            question();
        } else if (!context.isCallBackQuery()) {
            sm = new SendMessage();
            sm.setText(Utils.generateResultMessage(Message.FINISH, Message.MENU));
            SendHelper.setInlineKeyboard(sm, Message.actionsMenu, null);
            ClientDao.updateState(context.getClient(), State.MENU_CHOICE.ordinal());
            ReportDaysDao.saveOrUpdate(context.getClient(), context.getMessage());
            NotificationDao.updateFireTime(context.getClient().getUid());
            ClientDao.clearClient(context.getClient());
            question();
        }
    }
}
