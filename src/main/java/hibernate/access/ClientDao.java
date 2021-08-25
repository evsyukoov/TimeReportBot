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

    public static Client createClient(final long id, final State state, final State prev) {
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

    public static void updateStates(Client client, final int state, final int prev) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            client.setState(state);
            client.setPreviousState(prev);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void updatePreviousState(Client client, final int prev) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            client.setPreviousState(prev);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void updateName(Client client,
                                  final int current, final int previous, final String name) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            client.setState(current);
            client.setPreviousState(previous);
            client.setName(name);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void updatePosition(Client client, final int current, final int previous,
                                      final String position, boolean isRegistered) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            client.setState(current);
            client.setPreviousState(previous);
            client.setPosition(position);
            client.setRegistered(isRegistered);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void updateDepartment(Client client, final int current, final int previous,
                                      final String department) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            client.setState(current);
            client.setPreviousState(previous);
            client.setDepartment(department);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void updateDescription(Client client, final int state, final int prev,  final String description) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            client.setState(state);
            client.setPreviousState(prev);
            client.setDescription(description);
            session.update(client);
            session.getTransaction().commit();
        }
    }

    public static void updateProject(Client client,
                                     final int state, final int prev, final String project) {
        try(Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            client.setState(state);
            client.setPreviousState(prev);
            client.setProject(project);
            session.update(client);
            session.getTransaction().commit();
        }
    }
}
