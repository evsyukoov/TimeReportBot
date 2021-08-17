package handlers;

import bot.BotContext;
import bot.ReportingBot;
import hibernate.access.ClientDao;
import hibernate.entities.Client;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import stateMachine.AbstractBotState;
import stateMachine.BotStateFactory;
import stateMachine.State;

public class NewMessageHandler {

    private final Update update;

    private final ReportingBot bot;

    public NewMessageHandler(Update update, ReportingBot bot) {
        this.update = update;
        this.bot = bot;
    }

    public AbstractBotState getBotState() {
        State current;
        boolean callBack = update.getMessage() == null;
        Chat chat = callBack ?
                 update.getCallbackQuery().getMessage().getChat() : update.getMessage().getChat();
        long id = chat.getId();
        Client client = ClientDao.getClient(id);

        if (client == null) {
            current = State.REGISTER_NAME;
            client = ClientDao.createClient(id, current);
        } else {
            current = State.values()[client.getState()];
        }
        return BotStateFactory.createBotState(current, new BotContext(bot, update, client, callBack));

    }
}
