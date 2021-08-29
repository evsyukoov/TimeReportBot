package messages;

import utils.Utils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Message {

    public static final List<String> departments;

    public static final List<String> days;

    static {
        departments = Utils.getMessagesFromProps("./src/main/resources/property/job_departments.properties");

        days = Utils.getMessagesFromProps("./src/main/resources/property/days_choice.properties");
    }

    public static final String ERROR_SEND_MESSAGE = "Ошибка при отправке сообщения пользователю";

    public static final String STOP = "/stop";

    public static final String START = "/start";

    public static final String BACK = " ⬆️️ Назад";

    public static final String REGISTER_DEPARTMENT = "Выберите отдел";

    public static final String REGISTER_NAME = "Введите свою фамилию имя и отчество через пробел";

    public static final String REGISTER_POSITION = "Введите свою должность";

    public static final String CHOOSE_DAY = "Выберите день";

    public static final String ERROR_EMPTY_FIO = "Некорректное ФИО";

    public static final String ERROR_DEPARTMENT = "Неизвестный отдел";

    public static final String ERROR_INCORRECT_FIO = "Нужно ввести фамилию и имя";

    public static final String ERROR_DATE_FORMAT = "Некорректный формат даты";

    public static final String REGISTER_IS_FINISHED = "Регистрация успешно завершена";

    public static final String CHOOSE_REPORT_TYPE = "Выберите за какой день отчитываться";

    public static final String SELECT_PROJECT = "Выберите проект на котором работали";

    public static final String INFO_ABOUT_JOB = "Напишите краткий отчет о проделанной работе";

    public static final String FINISH = " ✅ Принято!";

    public static final String SELECT_DATE = "Введите день в формате dd.mm.yyyy";

}
