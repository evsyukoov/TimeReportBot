package hibernate.entities;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import stateMachine.State;

import javax.persistence.*;

@Entity
@Table(name = "clients")
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Client {

    @Id
    @Column(name = "uid")
    private long uid;

    @Column(name = "name")
    private String name;

    @Column(name = "state")
    private int state;

    @Column(name = "current_description")
    private String description;

    @Column(name = "current_project")
    private String project;

    @Column(name = "previous_state")
    private int previousState;

    @Column(name = "position")
    private String position;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public int getPreviousState() {
        return previousState;
    }

    public void setPreviousState(int previousState) {
        this.previousState = previousState;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Client() {
    }




    @Override
    public String toString() {
        return "Client{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", state=" + state +
                '}';
    }
}
