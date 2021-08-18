package hibernate.access;

import hibernate.entities.Client;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import stateMachine.State;

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

    public static void updateState(final long id, final int state) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Client client = new Client();
            client.setUid(id);
            client.setState(state);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void updatePreviousState(final long id, final int prev) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Client client = new Client();
            client.setUid(id);
            client.setPreviousState(prev);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void updateName(final long id, final int current, final int previous, final String name) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Client client = new Client();
            client.setUid(id);
            client.setState(current);
            client.setPreviousState(previous);
            client.setName(name);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void updatePosition(final long id, final int current, final int previous, final String position) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Client client = new Client();
            client.setUid(id);
            client.setState(current);
            client.setPreviousState(previous);
            client.setPosition(position);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void updateDescription(final long id, final int state, final String description) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Client client = new Client();
            client.setUid(id);
            client.setState(state);
            client.setDescription(description);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void updateProject(final long id, final State state, final String project) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Client client = new Client();
            client.setUid(id);
            client.setState(state.ordinal());
            client.setProject(project);
            session.update(client);
            session.getTransaction().commit();
        }
    }
}