package hibernate.access;

import hibernate.entities.Client;
import hibernate.entities.Notification;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import stateMachine.State;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationDao {

    final private static SessionFactory factory;

    static {
        factory = new Configuration()
                .configure("hibernate_notification.cfg.xml")
                .buildSessionFactory();
    }

        public static void saveClientOption(LocalDateTime dateTime, long uid){
            try(Session session = factory.getCurrentSession()) {
                session.beginTransaction();
                Notification notification = new Notification();
                notification.setUid(uid);
                notification.setNextFireTime(dateTime);
                session.saveOrUpdate(notification);
                session.getTransaction().commit();
            }
        }

    // получаем клиентов, которым пора получить сообщение
    public static List<Long> getClientUids(LocalDateTime time) {
        List<Long> result;
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Query<Notification> query = session.createQuery("FROM Notification " +
                    "WHERE nextFireTime <= :time", Notification.class);
            query.setParameter("time", time);

            List<Notification> notificationList = query.getResultList();
            notificationList.forEach(notification -> notification.
                    setNextFireTime(notification.getNextFireTime().plusHours(24)));

            result = notificationList
                    .stream()
                    .map(Notification::getUid)
                    .collect(Collectors.toList());
            session.getTransaction().commit();
        }
        return result;
    }
}
