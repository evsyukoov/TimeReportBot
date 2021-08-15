package hibernate.access;

import exceptions.UnknownDataBaseException;
import hibernate.entities.Client;
import hibernate.entities.ReportDay;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.time.LocalDateTime;

public class ReportDaysDao {
    final private static SessionFactory factory;

    static {
        factory = new Configuration()
                .configure("hibernate_work.cfg.xml")
                .buildSessionFactory();
    }

    public static void createReportDay(long id) throws Exception {
        Client client = ClientDao.getClient(id);
        if (client == null) {
            throw new UnknownDataBaseException("Неизвестная ошибка. Обратитесь в техподдержку");
        }
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            ReportDay reportDay = new ReportDay();
            reportDay.setDescription(client.getDescription());
            reportDay.setProject(client.getProject());
            // время ставим по МСК
            reportDay.setDateTime(LocalDateTime.now().plusHours(3));
            session.saveOrUpdate(reportDay);
            session.getTransaction().commit();
        }
    }
}
