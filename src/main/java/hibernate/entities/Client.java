package hibernate.entities;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import stateMachine.State;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column(name = "position")
    private String position;

    @Column(name = "report_date")
    private LocalDateTime dateTime;

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Column(name = "registered")
    private boolean registered;

    @Column(name = "department")
    private String department;

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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Client() {
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
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
