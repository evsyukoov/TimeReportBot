package stateMachine;

import bot.BotContext;
import handlers.MainCommandsHandler;
import hibernate.access.ClientDao;
import hibernate.access.ProjectsDao;
import messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import utils.SendHelper;

import java.util.Collections;

public class SelectProject implements AbstractBotState {

    SendMessage sm;

    BotContext context;

    public SelectProject(BotContext context) {
        this.sm = new SendMessage();
        this.context = context;
    }

    @Override
    public void handleMessage() {
        MainCommandsHandler handler = new MainCommandsHandler(context, State.REPORT_TYPE);
        if (handler.handle()) {
            sm.setText(handler.getResultToClient());
            if (handler.getResultToClient().equals(Message.CHOOSE_REPORT_TYPE)) {
                SendHelper.setInlineKeyboard(sm, Message.days, null);
            }
            question();
        } else {
            String command = context.getMessage();
            if (ProjectsDao.getAllProjectsNames().contains(command)) {
                ClientDao.updateProject(context.getClient(),
                        State.FINISH.ordinal(), State.CHOOSE_DAY.ordinal(), command);
                sm.setText(Message.INFO_ABOUT_JOB);
                SendHelper.setInlineKeyboard(sm, Collections.emptyList(), Message.BACK);
                question();
            }
        }
    }

    @Override
    public void question() {
        SendHelper.sendMessage(sm, context);
    }
}
