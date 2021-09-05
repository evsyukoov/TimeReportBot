package hibernate.access;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class EmployeeDao {

    final private static SessionFactory factory;

    static {
        factory = new Configuration()
                .configure("hibernate_employee.cfg.xml")
                .buildSessionFactory();
    }

    public static List<String> getEmployeeNames() {
        List<String> result;
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            result = session.createQuery("SELECT name From Employee", String.class).list();
            session.getTransaction().commit();
        }
        return result;
    }
}
