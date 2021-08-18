package stateMachine;

import bot.BotContext;

import static stateMachine.State.*;

public class BotStateFactory {

    public static AbstractBotState createBotState(State state, BotContext context) {
        if (state == REGISTER_NAME) {
            return new RegisterName(context);
        } else if (state == REGISTER_DEPARTMENT) {
            return new RegisterDepartment(context);
        } else if (state == REGISTER_POSITION) {
            return new RegisterPosition(context);
        }
        return null;
    }
}
