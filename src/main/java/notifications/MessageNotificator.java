package notifications;

import bot.ReportingBot;
import hibernate.access.NotificationDao;
import messages.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import utils.Utils;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;

public class MessageNotificator {

    ReportingBot bot;

    // каждые полминуты будем заглядывать в БД
    // и смотреть не пора ли отправить кому то нотификашку
    private final static int PERIOD = 30 * 1000;

    public MessageNotificator(ReportingBot bot) {
        this.bot = bot;
    }

    public void notificate() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (long uid : NotificationDao.getClientUids(LocalDateTime.now())) {
                    SendMessage sm = new SendMessage();
                    sm.setChatId(uid);
                    sm.setText(Message.NOTIFICATION);
                    try {
                        bot.execute(sm);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, PERIOD);
    }

    public void updateMessage() {
        List<Long> uids = Utils.getUidsFromProps("./src/main/resources/property/update.properties");
        for (long uid : uids) {
            SendMessage sm = new SendMessage();
            sm.setChatId(uid);
            sm.setText("Обновление 05.09.2021. Добавлены справочники, оповещения. Для старта введите /start");
            try {
                bot.execute(sm);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
