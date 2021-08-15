package stateMachine;

import static stateMachine.State.REGISTER_NAME;

public class BotStateFactory {

    AbstractBotState createBotState(State state) {
        if (state == REGISTER_NAME) {
            new RegisterNameBotState();
        }
        return null;
    }
}
