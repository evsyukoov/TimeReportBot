package utils;

import exceptions.ValidationException;
import messages.Message;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Utils {

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

    public static String generateResultMessage(String error, String message) {
        return String.format("%s\n%s", error, message);
    }

    public static Properties getProperties(String path) {
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

}
