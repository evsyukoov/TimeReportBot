package hibernate.access;

import hibernate.entities.Client;
import hibernate.entities.ReportDay;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class ReportDaysDao {
    final private static SessionFactory factory;

    static {
        factory = new Configuration()
                .configure("hibernate_work.cfg.xml")
                .buildSessionFactory();
    }

    public static void createReportDay(Client client) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            ReportDay reportDay = new ReportDay();
            reportDay.setDescription(client.getDescription());
            reportDay.setName(client.getName());
            reportDay.setProject(client.getProject());
            // время ставим по МСК
            reportDay.setDateTime(client.getDateTime().plusHours(3));
            session.saveOrUpdate(reportDay);
            session.getTransaction().commit();
        }
    }
}
