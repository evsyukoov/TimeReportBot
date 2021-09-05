package stateMachine;

import bot.BotContext;
import hibernate.access.ClientDao;
import hibernate.access.EmployeeDao;
import messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import utils.SendHelper;
import utils.Utils;

import java.util.List;

public class Menu implements AbstractBotState {

    SendMessage sm;

    BotContext context;

    public Menu(BotContext context) {
        this.context = context;
        sm = new SendMessage();
    }

    @Override
    public void handleMessage() {
        if (!context.isCallBackQuery()) {
            return;
        }
        List<String> expected = EmployeeDao.getEmployeeNames();
        String receive = context.getMessage().replace(Message.EMPTY_SYMBOL, "");
        if (!expected.contains(receive)) {
            return;
        }
        if (!context.getClient().isRegistered()) {
            sm.setText(Utils.generateResultMessage(Message.REGISTER_IS_FINISHED, Message.MENU));
        } else {
            sm.setText(Message.MENU);
        }
        SendHelper.refreshInlineKeyboard(context);
        SendHelper.setInlineKeyboard(sm, Message.actionsMenu, null);
        question();
        ClientDao.updateName(context.getClient(), State.MENU_CHOICE.ordinal(), receive);
    }

    @Override
    public void question() {
        SendHelper.sendMessage(sm, context);
    }
}
