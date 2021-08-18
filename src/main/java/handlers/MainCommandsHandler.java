package handlers;

import bot.BotContext;
import hibernate.access.ClientDao;
import messages.Message;
import stateMachine.EnumTranslators;
import stateMachine.State;

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
            ClientDao.updateState(context.getClient().getUid(), context.getClient().getPreviousState());
            resultToClient = EnumTranslators.translate(context.getClient().getPreviousState());
            return true;
        } else if (command.equals(Message.START) || command.equals(Message.STOP)) {
            ClientDao.updateState(context.getClient().getUid(), start.ordinal());
            resultToClient = EnumTranslators.translate(start.ordinal());
            return true;
        }
        return false;
    }

    public String getResultToClient() {
        return resultToClient;
    }
}
