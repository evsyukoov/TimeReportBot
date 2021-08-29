package utils;

import exceptions.DateAfterTodayException;
import exceptions.ValidationException;
import messages.Message;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class Utils {

    public static void validateDepartment(String text) throws ValidationException {
        if (!Message.departments.contains(text))
            throw new ValidationException(Message.ERROR_DEPARTMENT);
    }

    public static void validateFio(String text) throws ValidationException {
        String[] fio = text.split("\\s+");
        if (isBlank(fio)) {
            // наверно нереальный кейс, но пусть будет
            throw new ValidationException(Message.ERROR_EMPTY_FIO);
        }
        if (fio.length < 2) {
            throw new ValidationException(Message.ERROR_INCORRECT_FIO);
        }
    }

    public static boolean isBlank(String[] arr) {
        return arr == null || arr.length == 0;
    }

    public static String generateResultMessage(String msg1, String msg2) {
        return String.format("%s\n%s", msg1, msg2);
    }

    private static Properties getProperties(String path) {
        Properties properties = null;
        try (InputStream inputStream = new FileInputStream(path);
             Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
             properties = new Properties();
             properties.load(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static List<String> getMessagesFromProps(String path) {
        Properties props = Utils.getProperties(path);
        return  props
                .values()
                .stream()
                .map(String.class::cast)
                .map(s -> "🔳 ".concat(s))
                .sorted()
                .collect(Collectors.toList());
    }

    public static LocalDateTime parseDate(String dateText) throws ParseException, DateAfterTodayException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        //  флаг проверяет дату на наличие в календаре
        dateFormat.setLenient(false);
        Date date = dateFormat.parse(dateText);
        if (date.after(new Date())) {
            throw new DateAfterTodayException();
        }
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .plusHours(3);
    }

    public static java.sql.Date convertDate(LocalDateTime dateTime) {
        return java.sql.Date.valueOf(dateTime.toLocalDate());
    }

}
