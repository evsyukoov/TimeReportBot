package handlers;

import bot.BotContext;
import exceptions.DateAfterTodayException;
import hibernate.access.ClientDao;
import hibernate.access.NotificationDao;
import hibernate.access.ProjectsDao;
import hibernate.access.ReportDaysDao;
import hibernate.entities.Project;
import messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import stateMachine.EnumTranslators;
import stateMachine.State;
import utils.SendHelper;
import utils.Utils;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
                SendHelper.setInlineKeyboardProjects(sm, ProjectsDao.getProjects());
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
        SendHelper.setInlineKeyboardProjects(sm, projects);
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
            SendHelper.setInlineKeyboardProjects(message, projects);
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

    public long countFilledBoxes(String telegramSymbol, InlineKeyboardMarkup markup) {
        return markup.getKeyboard().stream()
                .flatMap(Collection::stream)
                .filter(button -> button.getText().contains(telegramSymbol))
                .count();
    }

    public SendMessage handleProjectsChoice() {
        if (!context.isCallBackQuery()) {
            return null;
        } else {
            if (!context.getMessage().equals(Message.APPROVE)) {
                handleProjectBox();
                return null;
            } else {
                List<String> result = collectProjectChoice();
                if (result.isEmpty()) {
                    return null;
                } else {
                    ClientDao.updateProject(context.getClient(), State.FINISH.ordinal(), result,
                            context.getClient().getDateTime() == null ? LocalDateTime.now() :
                            context.getClient().getDateTime());
                }
            }
        }
        SendMessage sm = new SendMessage();
        sm.setText(Message.INFO_ABOUT_JOB);
        SendHelper.setInlineKeyboard(sm, Collections.emptyList(), Message.BACK);
        return sm;
    }

    private List<String> collectProjectChoice() {
        InlineKeyboardMarkup markup = context.getUpdate().getCallbackQuery().
                getMessage().getReplyMarkup();
        List<String> projects = new ArrayList<>();
        InlineKeyboardButton mainProjectButton = findConfirmedBoxes(markup, Message.CONFIRM_SYMBOL);
        if (mainProjectButton != null) {
            projects.add(mainProjectButton.getCallbackData());
            List<InlineKeyboardButton> extraButtons = findExtraConfirmedBoxes(markup, Message.EXTRA_CONFIRM_SYMBOL);

            projects.addAll(extraButtons.stream()
                    .map(InlineKeyboardButton::getCallbackData)
                    .collect(Collectors.toList()));

        }
        return projects;
    }

    private InlineKeyboardButton findConfirmedBoxes(InlineKeyboardMarkup markup, String telegramSymbol) {
        return markup.getKeyboard().stream()
                .flatMap(Collection::stream)
                .filter(button -> button.getText().contains(telegramSymbol))
                .findAny()
                .orElse(null);
    }

    private List<InlineKeyboardButton> findExtraConfirmedBoxes(InlineKeyboardMarkup markup, String telegramSymbol) {
        return markup.getKeyboard().stream()
                .flatMap(Collection::stream)
                .filter(button -> button.getText().contains(telegramSymbol))
                .collect(Collectors.toList());
    }

    private InlineKeyboardButton findPressedButton(InlineKeyboardMarkup markup, String text) {
        return markup.getKeyboard().stream()
                .flatMap(Collection::stream)
                .filter(button -> button.getCallbackData().equals(text))
                .findAny()
                .orElse(null);
    }

    private void handleProjectBox() {
        String command = context.getMessage();
        InlineKeyboardMarkup markup = context.getUpdate().getCallbackQuery().
                getMessage().getReplyMarkup();
        int id = context.getUpdate().getCallbackQuery().getMessage().getMessageId();

        InlineKeyboardButton pressedButton = findPressedButton(markup, command);
        if (Objects.isNull(pressedButton)) {
            return;
        }

        boolean isModified = false;
        // главных объектов 1, дополнительных 3
        if (countFilledBoxes(Message.CONFIRM_SYMBOL, markup) == 0) {
            pressedButton.setText(pressedButton.getText().replace(Message.EMPTY_SYMBOL, Message.CONFIRM_SYMBOL));
            isModified = true;
        } else if (countFilledBoxes(Message.CONFIRM_SYMBOL, markup) == 1) {
            if (pressedButton.getText().contains(Message.CONFIRM_SYMBOL)) {
                pressedButton.setText(pressedButton.getText().replace(Message.CONFIRM_SYMBOL, Message.EMPTY_SYMBOL));
                isModified = true;
            } else {
                if (pressedButton.getText().contains(Message.EXTRA_CONFIRM_SYMBOL)) {
                    pressedButton.setText(pressedButton.getText().replace(Message.EXTRA_CONFIRM_SYMBOL, Message.EMPTY_SYMBOL));
                    isModified = true;
                } else if (countFilledBoxes(Message.EXTRA_CONFIRM_SYMBOL, markup) < 3){
                    pressedButton.setText(pressedButton.getText().replace(Message.EMPTY_SYMBOL, Message.EXTRA_CONFIRM_SYMBOL));
                    isModified = true;
                }
            }
        }
        if (isModified) {
            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup()
                    .setMessageId(id)
                    .setChatId(context.getClient().getUid())
                    .setReplyMarkup(markup);
            try {
                context.getBot().execute(editMessageReplyMarkup);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

}
