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
import java.util.List;

public class ReportDaysDao {
    final private static SessionFactory factory;

    static {
        factory = new Configuration()
                .configure("hibernate_work.cfg.xml")
                .buildSessionFactory();
    }

    public static boolean isReportToday(Client client) {
        boolean result;
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createQuery("from ReportDay WHERE date = :date", ReportDay.class);
            java.sql.Date now = Utils.convertDate(LocalDateTime.now());
            query.setParameter("date", now);
            result = !Utils.isEmpty(query.getResultList());
        }
        return result;
    }

    public static void saveOrUpdate(Client client, String description) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createQuery("from ReportDay WHERE uid=:uid AND date=:date", ReportDay.class);

            query.setParameter("date", Utils.convertDate(client.getDateTime()));
            query.setParameter("uid", client.getUid());

            List<ReportDay> dayList = query.getResultList();
            if (!dayList.isEmpty()) {
                ReportDay rd = dayList.get(0);
                rd.setProject(client.getProject());
                rd.setExtraProjects(client.getExtraProjects());
                rd.setDescription(description);
                session.update(rd);
            } else {
                ReportDay rd = new ReportDay();
                rd.setDescription(description);
                rd.setName(client.getName());
                rd.setProject(client.getProject());
                rd.setExtraProjects(client.getExtraProjects());
                rd.setUid(client.getUid());
                // время ставим по МСК
                rd.setDate(Utils.convertDate(client.getDateTime()));
                session.save(rd);
            }
            session.getTransaction().commit();
        }
    }
}
