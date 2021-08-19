package messages;

import utils.Utils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Message {

    public static final List<String> departments;

    static {
        departments = Utils.getProperties("./src/main/resources/property/job_departments.properties").
                values()
                .stream()
                .map(String.class::cast)
                .collect(Collectors.toList());
    }

    public static final String ERROR_SEND_MESSAGE = "Ошибка при отправке сообщения пользователю";

    public static final String STOP = "/stop";

    public static final String START = "/start";

    public static final String BACK = "Назад";

    public static final String REGISTER_DEPARTMENT = "Выберите отдел";

    public static final String REGISTER_NAME = "Введите свою фамилию имя и отчество через пробел";

    public static final String REGISTER_POSITION = "Введите свою должность";

    public static final String CHOOSE_DAY = "Выберите день";

    public static final String ERROR_EMPTY_FIO = "Фио не отправлено";

    public static final String ERROR_INCORRECT_FIO = "Нужно ввести фамилию и имя";

}
