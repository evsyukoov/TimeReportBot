package stateMachine;

import bot.BotContext;

public interface AbstractBotState {

    // хендлим ответ на вопрос
    void handleMessage();

    //задаем новый вопрос
    void question(String message);

}
