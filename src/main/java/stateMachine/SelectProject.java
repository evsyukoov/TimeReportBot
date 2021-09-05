package stateMachine;

import bot.BotContext;
import handlers.MainCommandsHandler;
import hibernate.access.ClientDao;
import hibernate.access.ProjectsDao;
import hibernate.entities.Client;
import messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import utils.SendHelper;

import java.time.LocalDateTime;
import java.util.Collections;

public class SelectProject implements AbstractBotState {

    SendMessage sm;

    BotContext context;

    public SelectProject(BotContext context) {
        this.context = context;
    }

    @Override
    public void handleMessage() {
        Client client = context.getClient();
        MainCommandsHandler handler;
        // приходим на этот стейт с разных мест, по наличию даты понимаем откуда пришли
        if (client.getDateTime() == null) {
            handler = new MainCommandsHandler(context,
                    State.CHOOSE_DAY, Message.CHOOSE_REPORT_TYPE);
        } else {
            handler = new MainCommandsHandler(context,
                    State.PARSE_DATE, Message.SELECT_DATE);
        }
        if ((sm = handler.handleBackButton()) != null) {
            question();
        } else {
            sm = new SendMessage();
            String command = context.getMessage();
            String project = ProjectsDao.getProjectById(command);
            if (project != null) {
                ClientDao.updateProject(context.getClient(),
                        State.FINISH.ordinal(), command, context.getClient().getDateTime() == null ?
                        LocalDateTime.now() : context.getClient().getDateTime());
                SendHelper.refreshInlineKeyboard(context);
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
