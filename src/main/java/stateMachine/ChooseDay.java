package stateMachine;

import bot.BotContext;
import handlers.MainCommandsHandler;
import messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import utils.SendHelper;

public class ChooseDay implements AbstractBotState {

    SendMessage sm;

    BotContext context;

    public ChooseDay(BotContext context) {
        this.sm = new SendMessage();
        this.context = context;
    }

    @Override
    public void handleMessage() {
        //кнопки назад на этом стейте нет, поскольку он первый после регистрации, хендлим только старт/стоп,
        // они хендлятся вверху для всей логики
        MainCommandsHandler handler = new MainCommandsHandler(context,
                State.REPORT_TYPE, Message.CHOOSE_REPORT_TYPE);
        if (handler.handleReportChoice(sm)) {
            question();
        }
    }

    @Override
    public void question() {
        SendHelper.sendMessage(sm, context);
    }
}
