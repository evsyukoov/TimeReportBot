package hibernate.access;

import hibernate.entities.Client;
import hibernate.entities.ReportDay;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import utils.Utils;

import javax.persistence.Query;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class ReportDaysDao {
    final private static SessionFactory factory;

    static {
        factory = new Configuration()
                .configure("hibernate_work.cfg.xml")
                .buildSessionFactory();
    }

    public static void saveOrUpdate(Client client) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createQuery("from ReportDay WHERE uid=:uid AND date=:date", ReportDay.class);

            query.setParameter("date", Utils.convertDate(client.getDateTime()));
            query.setParameter("uid", client.getUid());

            List<ReportDay> dayList = query.getResultList();
            if (!dayList.isEmpty()) {
                ReportDay rd = dayList.get(0);
                rd.setProject(client.getProject());
                rd.setDescription(client.getDescription());
                session.update(rd);
            } else {
                ReportDay rd = new ReportDay();
                rd.setDescription(client.getDescription());
                rd.setName(client.getName());
                rd.setProject(client.getProject());
                rd.setUid(client.getUid());
                // время ставим по МСК
                rd.setDate(Utils.convertDate(client.getDateTime()));
                session.save(rd);
            }
            session.getTransaction().commit();
        }
    }

    public static void createReportDay(Client client) {

        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            ReportDay reportDay = new ReportDay();
            reportDay.setDescription(client.getDescription());
            reportDay.setName(client.getName());
            reportDay.setProject(client.getProject());
            reportDay.setUid(client.getUid());
            // время ставим по МСК
            reportDay.setDate(Utils.convertDate(client.getDateTime()));
            session.save(reportDay);
            session.getTransaction().commit();
        }
    }

    public static void main(String[] args) {
        Client cl = new Client();
        cl.setDateTime(LocalDateTime.now());
        cl.setUid(349939502);
        saveOrUpdate(cl);
    }
}
