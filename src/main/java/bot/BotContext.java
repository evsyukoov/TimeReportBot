package bot;

import hibernate.entities.Client;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BotContext {
    private final ReportingBot bot;
    private final Update update;
    private final Client client;
    private final boolean callBackQuery;


    public BotContext(ReportingBot bot, Update update,
                      Client client, boolean callBackQuery) {
        this.bot = bot;
        this.update = update;
        this.client = client;
        this.callBackQuery = callBackQuery;
    }

    public ReportingBot getBot() {
        return bot;
    }

    public Update getUpdate() {
        return update;
    }

    public Client getClient() {
        return client;
    }

    public boolean isCallBackQuery() {
        return callBackQuery;
    }
}
