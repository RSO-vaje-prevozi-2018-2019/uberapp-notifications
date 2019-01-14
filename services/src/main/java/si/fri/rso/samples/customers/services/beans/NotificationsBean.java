package si.fri.rso.samples.customers.services.beans;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.eclipse.microprofile.metrics.annotation.Timed;
import si.fri.rso.samples.customers.models.dtos.Order;
import si.fri.rso.samples.customers.models.entities.Customer;
import si.fri.rso.samples.customers.models.entities.Notification;
import si.fri.rso.samples.customers.services.configuration.AppProperties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


@RequestScoped
public class NotificationsBean {

    private Logger log = Logger.getLogger(NotificationsBean.class.getName());

    @Inject
    private EntityManager em;

    @Inject
    private AppProperties appProperties;

    @Inject
    private NotificationsBean notificationsBean;

    private Client httpClient;

    /*
    @Inject
    @DiscoverService("rso-orders")
    private Optional<String> baseUrl;
    */

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
//        baseUrl = "http://localhost:8081"; // only for demonstration
    }


    public List<Notification> getNotifications() {

        TypedQuery<Notification> query = em.createNamedQuery("Notification.getAll", Notification.class);
        return query.getResultList();

    }


    public Notification getNotification(Integer notificationId) {

        Notification notification = em.find(Notification.class, notificationId);

        if (notification == null) {
            throw new NotFoundException();
        }


        return notification;
    }

    public Notification createNotification(Notification notification) {

        try {
            beginTx();
            em.persist(notification);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return notification;
    }

    public Notification editNotfication(Integer notificationId, Notification notification){
        Notification n = em.find(Notification.class, notificationId);
        if(n == null){
            return null;
        }
        try{
            beginTx();
            n.setSeen(notification.getSeen());
            n = em.merge(n);
            commitTx();
        }catch (Exception e){
            rollbackTx();
        }

        return n;
    }

    public Notification putNotification(String notificationId, Notification notification) {

        Notification c = em.find(Notification.class, notificationId);

        if (c == null) {
            return null;
        }

        try {
            beginTx();
            notification.setId(c.getId());
            notification = em.merge(notification);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return notification;
    }

    public boolean deleteCustomer(String customerId) {

        Customer customer = em.find(Customer.class, customerId);

        if (customer != null) {
            try {
                beginTx();
                em.remove(customer);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }

    public void loadOrder(Integer n) {


    }
}
