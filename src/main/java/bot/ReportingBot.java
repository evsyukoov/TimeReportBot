package bot;

import hibernate.access.ProjectsDao;
import hibernate.access.ReportDaysDao;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ReportingBot extends TelegramLongPollingBot {
    final String token = "";
    private final String botName = "";


    @Override
    public void onUpdateReceived(Update update) {
        if (update != null && update.getMessage() != null) {
            SendMessage sm = new SendMessage();
            sm.setText("test");
            sm.setChatId(update.getMessage().getChatId());
            try {
                this.execute(sm);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public static void main(String[] args) throws Exception {
//        TelegramBotsApi botsApi = new TelegramBotsApi();
//        ApiContextInitializer.init();
//        try {
//            botsApi.registerBot(new ReportingBot());
//        } catch (TelegramApiRequestException e) {
//            e.printStackTrace();
//        }
//        Client client = ClientDao.getClient(1);
//        System.out.println(client);

//        ReportDaysDao.createReportDay(1);
        //ProjectsDao.addProject("Проект 2");
        ProjectsDao.getAllProjectsNames().forEach(pr -> System.out.println(pr.getProjectName()));
    }
}
