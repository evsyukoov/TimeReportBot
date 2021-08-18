package stateMachine;

import messages.Message;

public class EnumTranslators {

    public static String translate(int state) {
        if (state == State.REGISTER_NAME.ordinal()) {
            return Message.REGISTER_NAME;
        } else if (state == State.REGISTER_DEPARTMENT.ordinal()) {
            return Message.REGISTER_DEPARTMENT;
        } else if (state == State.REGISTER_POSITION.ordinal()) {
            return Message.REGISTER_POSITION;
        }
        return "";
    }
}
