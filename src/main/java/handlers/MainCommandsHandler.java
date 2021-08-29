package handlers;

import bot.BotContext;
import exceptions.DateAfterTodayException;
import hibernate.access.ClientDao;
import hibernate.access.ProjectsDao;
import messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import stateMachine.EnumTranslators;
import stateMachine.State;
import utils.SendHelper;
import utils.Utils;

import javax.swing.text.DateFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainCommandsHandler {
    BotContext context;

    String resultToClient;

    State newState;

    String message;

    public MainCommandsHandler(BotContext context,
                               State newState, String message) {
        this.context = context;
        this.newState = newState;
        this.message = message;
    }

    public SendMessage handleBackButton() {
        String command = context.getMessage();
        if (command.equals(Message.BACK)) {
            SendMessage sm = new SendMessage();
            ClientDao.updateState(context.getClient(), newState.ordinal());
            sm.setText(message);
            if (message.equals(Message.REGISTER_DEPARTMENT)) {
                SendHelper.setInlineKeyboard(sm, Message.departments, Message.BACK);
            } else if (message.equals(Message.SELECT_PROJECT)) {
                SendHelper.setInlineKeyboard(sm, ProjectsDao.getAllProjectsNames(), Message.BACK);
            } else if (message.equals(Message.CHOOSE_REPORT_TYPE)) {
                SendHelper.setInlineKeyboard(sm, Message.days, null);
            } else if (message.equals(Message.SELECT_DATE)) {
                SendHelper.setInlineKeyboard(sm, Collections.emptyList(), Message.BACK);
            }
            return sm;
        }
        return null;
    }

    public SendMessage parseDate() {
        String command = context.getMessage();
        SendMessage sm = new SendMessage();
        LocalDateTime date;
        try {
            date = Utils.parseDate(command);

        } catch (DateAfterTodayException e) {
            sm.setText(Utils.generateResultMessage(Message.ERROR_DATE_AFTER_TODAY, Message.SELECT_DATE));
            SendHelper.setInlineKeyboard(sm,Collections.emptyList(), Message.BACK);
            return sm;
        }
        catch (ParseException e) {
            sm.setText(Utils.generateResultMessage(Message.ERROR_DATE_FORMAT, Message.SELECT_DATE));
            SendHelper.setInlineKeyboard(sm,Collections.emptyList(), Message.BACK);
            return sm;
        }
        ClientDao.updateDate(context.getClient(), State.SELECT_PROJECT.ordinal(), date);
        List<String> projects = ProjectsDao.getAllProjectsNames();
        sm.setText(Message.SELECT_PROJECT);
        SendHelper.setInlineKeyboard(sm, projects, Message.BACK);
        return sm;
    }

    public boolean handleReportChoice(SendMessage message) {
        String command = context.getMessage();
        if (command.equals(Message.days.get(1))) {
            message.setText(EnumTranslators.translate(State.SELECT_PROJECT.ordinal()));
            List<String> projects = ProjectsDao.getAllProjectsNames();
            ClientDao.updateStates(context.getClient(), State.SELECT_PROJECT.ordinal());
            SendHelper.setInlineKeyboard(message, projects, Message.BACK);
            return true;
        }
        else if (command.equals(Message.days.get(0))) {
            message.setText(EnumTranslators.translate(State.PARSE_DATE.ordinal()));
            ClientDao.updateStates(context.getClient(), State.PARSE_DATE.ordinal());
            SendHelper.setInlineKeyboard(message, Collections.emptyList(), Message.BACK);
            return true;
        }
        return false;
    }

}
