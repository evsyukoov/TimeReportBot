package hibernate.access;

import hibernate.entities.Client;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import stateMachine.State;

import java.time.LocalDateTime;

public class ClientDao {

    final private static SessionFactory factory;

    static {
        factory = new Configuration()
                .configure("hibernate_clients.cfg.xml")
                .buildSessionFactory();
    }


    public static Client getClient(final long id) {
        Client result;
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            result = session.get(Client.class, id);
            session.getTransaction().commit();
        }
        return result;
    }

    public static Client createClient(final long id, final State state) {
        Client client;
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            client = new Client();
            client.setUid(id);
            client.setState(state.ordinal());
            session.save(client);
            session.getTransaction().commit();
        }
        return client;
    }

    public static void updateState(Client client,  final int state) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            client.setState(state);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void updateDate(Client client,  final int state, LocalDateTime dateTime) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            client.setState(state);
            client.setDateTime(dateTime);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void updateStates(Client client, final int state) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            client.setState(state);
            client.setDateTime(null);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void updateName(Client client,
                                  final int current, final String name) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            client.setState(current);
            client.setName(name);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void updatePosition(Client client, final int current,
                                      final String position, boolean isRegistered) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            client.setState(current);
            client.setPosition(position);
            client.setRegistered(isRegistered);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void updateDepartment(Client client, final int current,
                                      final String department) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            client.setState(current);
            client.setDepartment(department);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void clearClient(Client client) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            client.setDescription(null);
            client.setProject(null);
            client.setDateTime(null);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void updateDescription(Client client, final int state,  final String description) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            client.setState(state);
            client.setDescription(description);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void updateProject(Client client,
                                     final int state,
                                     final String project,
                                     LocalDateTime date) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            client.setState(state);
            client.setProject(project);
            client.setDateTime(date);
            session.update(client);
            session.getTransaction().commit();
        }
    }
}
