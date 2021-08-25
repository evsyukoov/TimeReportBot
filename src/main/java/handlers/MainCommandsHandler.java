package handlers;

import bot.BotContext;
import hibernate.access.ClientDao;
import hibernate.access.ProjectsDao;
import messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import stateMachine.EnumTranslators;
import stateMachine.State;
import utils.SendHelper;

import java.util.Collections;
import java.util.List;

public class MainCommandsHandler {
    BotContext context;

    // стартовая позиция клиента ожет быть разной в зависимости от того прошел или нет регистрацию
    State start;

    String resultToClient;

    public MainCommandsHandler(BotContext context, State start) {
        this.context = context;
        this.start = start;
    }

    public boolean handle() {
        String command = context.getMessage();
        if (command.equals(Message.BACK)) {
            ClientDao.updateState(context.getClient(), context.getClient().getPreviousState() + 1);
            resultToClient = EnumTranslators.translate(context.getClient().getPreviousState());
            return true;
        } else if (command.equals(Message.START) || command.equals(Message.STOP)) {
            ClientDao.updateState(context.getClient(), start.ordinal());
            resultToClient = EnumTranslators.translate(start.ordinal());
            return true;
        }
        return false;
    }

    // у некоторых состояний нет кнопки назад
    public boolean handleMainCommands() {
        String command = context.getMessage();
        if (command.equals(Message.START) || command.equals(Message.STOP)) {
            ClientDao.updateState(context.getClient(), start.ordinal());
            resultToClient = EnumTranslators.translate(start.ordinal());
            return true;
        }
        return false;
    }

    public boolean handleReportChoice(SendMessage message) {
        String command = context.getMessage();
        if (command.equals(Message.days.get(1))) {
            message.setText(EnumTranslators.translate(State.SELECT_PROJECT.ordinal()));
            List<String> projects = ProjectsDao.getAllProjectsNames();
            ClientDao.updateStates(context.getClient(), State.SELECT_PROJECT.ordinal(), State.REPORT_TYPE.ordinal());
            SendHelper.setInlineKeyboard(message, projects, Message.BACK);
            return true;
        } else if (command.equals(Message.days.get(0))) {
            message.setText(EnumTranslators.translate(State.TYPE_DAY.ordinal()));
            ClientDao.updateStates(context.getClient(), State.TYPE_DAY.ordinal(), State.REPORT_TYPE.ordinal());
            SendHelper.setInlineKeyboard(message, Collections.emptyList(), Message.BACK);
            return true;
        }
        return false;
    }


    public String getResultToClient() {
        return resultToClient;
    }
}
