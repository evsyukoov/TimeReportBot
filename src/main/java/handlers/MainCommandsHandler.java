package handlers;

import bot.BotContext;
import com.google.inject.internal.cglib.core.$CollectionUtils;
import exceptions.DateAfterTodayException;
import hibernate.access.ClientDao;
import hibernate.access.NotificationDao;
import hibernate.access.ProjectsDao;
import hibernate.access.ReportDaysDao;
import hibernate.entities.Project;
import messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import stateMachine.EnumTranslators;
import stateMachine.State;
import utils.SendHelper;
import utils.Utils;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainCommandsHandler {
    BotContext context;

    State newState;

    String message;

    public MainCommandsHandler(BotContext context,
                               State newState, String message) {
        this.context = context;
        this.newState = newState;
        this.message = message;
    }

    public MainCommandsHandler(BotContext context) {
        this.context = context;
    }

    public SendMessage handleBackButton() {
        String command = context.getMessage();
        if (command.equals(Message.BACK)) {
            SendMessage sm = new SendMessage();
            ClientDao.updateState(context.getClient(), newState.ordinal());
            if (message.equals(Message.MENU)) {
              SendHelper.setInlineKeyboard(sm, Message.actionsMenu, null);
            } else if (message.equals(Message.SELECT_PROJECT)) {
                SendHelper.setInlineKeyboardProjects(sm, ProjectsDao.getProjects(), Message.BACK);
            } else if (message.equals(Message.CHOOSE_REPORT_TYPE)) {
                SendHelper.setInlineKeyboard(sm, Message.days, Message.BACK);
            } else if (message.equals(Message.SELECT_DATE)) {
                SendHelper.setInlineKeyboard(sm, Collections.emptyList(), Message.BACK);
            }
            sm.setText(message);
            return sm;
        }
        return null;
    }

    public SendMessage handleMenuChoice() {
        String command = context.getMessage();
        SendMessage sm = new SendMessage();
        if (command.equals(Message.actionsMenu.get(0))) {
            ClientDao.updateState(context.getClient(), State.CHOOSE_DAY.ordinal());
            SendHelper.setInlineKeyboard(sm, Message.days, Message.BACK);
            sm.setText(Message.CHOOSE_REPORT_TYPE);
            return sm;
        } else if (command.equals(Message.actionsMenu.get(1))) {
            ClientDao.updateState(context.getClient(), State.NOTIFICATION_CHOICE.ordinal());
            SendHelper.setDateTimeInlineQuery(sm);
            sm.setText(Message.NOTIFICATION_CHOICE);
            return sm;
        }
        return null;
    }

    public SendMessage handleTimeChoice() {
        String command = context.getMessage();
        LocalDateTime resultTime;
        SendMessage sm = null;
        if (!context.isCallBackQuery()) {
            return null;
        }
        if (command.split(" ")[0].matches("\\d+")) {
            refreshTimeBox(command);
        } else if (command.equals(Message.DISCHARGE_NOTIFICATION)) {
            sm = new SendMessage();
            NotificationDao.saveClientOption(null, context.getClient().getUid());
            sm.setText(Utils.generateResultMessage(Message.DISCHARGE_ACTION_ENABLED, Message.MENU));
            SendHelper.setInlineKeyboard(sm, Message.actionsMenu, null);
            ClientDao.updateState(context.getClient(), State.MENU_CHOICE.ordinal());
            return sm;
        } else if (command.equals(Message.APPROVE_NOTIFICATION) &&
                (resultTime = getTimeFromClientChoice()) != null) {
            sm = new SendMessage();
            NotificationDao.saveClientOption(resultTime, context.getClient().getUid());
            sm.setText(Utils.generateResultMessage(
                    String.format(Message.APPROVE_NOTIFICATION_ENABLED, getTimeStringFromDate(resultTime)),
                    Message.MENU));

            SendHelper.setInlineKeyboard(sm, Message.actionsMenu, null);
            ClientDao.updateState(context.getClient(), State.MENU_CHOICE.ordinal());
            return sm;
        }
        return null;
    }

    private String getTimeStringFromDate(LocalDateTime dateTime) {
        return String.format("%s:%s по МСК", dateTime.getHour() < 10 ? "0".concat(String.valueOf(dateTime.getHour()))
                        : String.valueOf(dateTime.getHour())
                , dateTime.getMinute() < 10 ?
                "0".concat(String.valueOf(dateTime.getMinute()))
                : String.valueOf(dateTime.getMinute()));
    }

    private InlineKeyboardButton findPressedButton(String payload, InlineKeyboardMarkup markup) {
        return markup.getKeyboard().stream()
                .flatMap(Collection::stream)
                .filter(button -> button.getText().contains(payload)
                        && button.getText().contains(Message.CONFIRM_SYMBOL))
                .findFirst()
                .orElse(null);
    }

    private LocalDateTime getTimeFromClientChoice() {
        InlineKeyboardMarkup markup = context.getUpdate().getCallbackQuery().
                getMessage().getReplyMarkup();
        InlineKeyboardButton pressedHour = findPressedButton("час." , markup);
        InlineKeyboardButton pressedMinutes = findPressedButton("мин.", markup);
        if (pressedHour == null || pressedMinutes == null) {
            return null;
        }
        Integer hour = Integer.parseInt(pressedHour.getCallbackData().split(" ")[0]);
        Integer minutes = Integer.parseInt(pressedMinutes.getCallbackData().split(" ")[0]);
        LocalDateTime nextFireTime = LocalDateTime.now();
        if (ReportDaysDao.isReportToday(context.getClient())) {
            nextFireTime = nextFireTime.plusHours(24);
        }
        return LocalDateTime.of(nextFireTime.getYear(), nextFireTime.getMonth(), nextFireTime.getDayOfMonth(),
                hour, minutes);
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
        List<Project> projects = ProjectsDao.getProjects();
        SendHelper.setInlineKeyboardProjects(sm, projects, Message.BACK);
        sm.setText(Message.SELECT_PROJECT);
        return sm;
    }

    public SendMessage handleReportChoice() {
        String command = context.getMessage();
        SendMessage message = new SendMessage();
        if (command.equals(Message.days.get(1))) {
            List<Project> projects = ProjectsDao.getProjects();
            message.setText(EnumTranslators.translate(State.SELECT_PROJECT.ordinal()));
            SendHelper.refreshInlineKeyboard(context);
            SendHelper.setInlineKeyboardProjects(message, projects, Message.BACK);
            ClientDao.updateStates(context.getClient(), State.SELECT_PROJECT.ordinal());
            return message;
        }
        else if (command.equals(Message.days.get(0))) {
            message.setText(EnumTranslators.translate(State.PARSE_DATE.ordinal()));
            SendHelper.refreshInlineKeyboard(context);
            SendHelper.setInlineKeyboard(message, Collections.emptyList(), Message.BACK);
            ClientDao.updateStates(context.getClient(), State.PARSE_DATE.ordinal());
            return message;
        }
        return null;
    }


    private boolean isValidTimeBoxChoice(String currentPress) {
        InlineKeyboardMarkup markup = context.getUpdate().getCallbackQuery().
                getMessage().getReplyMarkup();
        if (currentPress.contains("час.")) {
            long countHourChoice = markup.getKeyboard().stream()
                    .flatMap(Collection::stream)
                    .filter(button -> button.getText().contains("час.")
                            && button.getText().contains(Message.CONFIRM_SYMBOL)
                            && !button.getCallbackData().equals(currentPress))
                    .count();
            return countHourChoice == 0;
        } else  if (currentPress.contains("мин.")) {
            long countMinutesChoice = markup.getKeyboard().stream()
                    .flatMap(Collection::stream)
                    .filter(button -> button.getText().contains("мин.")
                            && button.getText().contains(Message.CONFIRM_SYMBOL)
                            && !button.getCallbackData().equals(currentPress))
                    .count();
            return countMinutesChoice == 0;
        }
        return true;
    }

    private void refreshTimeBox(String command) {
        InlineKeyboardMarkup markup = context.getUpdate().getCallbackQuery().
                getMessage().getReplyMarkup();
        if (isValidTimeBoxChoice(command)) {
            SendHelper.refreshInlineKeyboard(context);
        }
    }

}
