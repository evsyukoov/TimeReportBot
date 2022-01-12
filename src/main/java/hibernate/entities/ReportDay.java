package hibernate.entities;


import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "report_days")
public class ReportDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "project")
    private String project;

    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "uid")
    private long uid;

    @Column(name = "extra_projects")
    private String extraProjects;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getExtraProjects() {
        return extraProjects;
    }

    public void setExtraProjects(String extraProjects) {
        this.extraProjects = extraProjects;
    }
}
