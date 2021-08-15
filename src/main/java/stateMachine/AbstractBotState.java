package stateMachine;

import bot.BotContext;

public interface AbstractBotState {

    AbstractBotState next = null;

    void read(BotContext botContext);

    void write(BotContext botContext);

    AbstractBotState next();


}
