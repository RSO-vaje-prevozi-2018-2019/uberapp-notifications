package si.fri.rso.samples.customers.models.entities;

import org.eclipse.persistence.annotations.UuidGenerator;
import si.fri.rso.samples.customers.models.dtos.Order;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity(name = "notification")
@NamedQueries(value =
        {
                @NamedQuery(name = "Notification.getAll", query = "SELECT c FROM notification c")
        })
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "userid")
    private Integer userid;

    @Column(name = "seen")
    private Integer seen;

    @Column(name = "notificationtext")
    String notificationtext;

    @Column(name = "rideid")
    private Integer rideid;



    public Integer getRideid() { return rideid; }
    public void setRideid(Integer rideid){ this.rideid = rideid; }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserid() {
        return userid;
    }

    public Integer getSeen(){
        return seen;
    }
    public void setSeen(Integer seen){
        this.seen = seen;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getNotificationtext () { return notificationtext;}
    public void setNotificationtext (String notificationtext) { this.notificationtext = notificationtext;}



}