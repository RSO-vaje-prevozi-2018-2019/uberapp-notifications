package si.fri.rso.samples.customers.api.v1.resources;


import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.metrics.Timer;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.eclipse.microprofile.metrics.annotation.Timed;
import si.fri.rso.samples.customers.models.entities.Customer;
import si.fri.rso.samples.customers.models.entities.Notification;
import si.fri.rso.samples.customers.services.beans.CustomersBean;
import si.fri.rso.samples.customers.services.beans.NotificationsBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Path("/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {

    @Inject
    private NotificationsBean notificationsBean;

    @Context
    protected UriInfo uriInfo;

    @Inject
    @DiscoverService(value = "uberapp-rides")
    private Optional<String> basePathRides;

    @GET
    @Metered(name="count_get_all_notifications")
    public Response getNotifications() {
        try {
            List<Notification> notifications = notificationsBean.getNotifications();

            return Response.ok(notifications).build();
        }catch (Exception e){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    @Inject
    @Metric(name = "time_unread_notifications")
    private Timer unreadnotifications_timer;

    @GET
    @Path("/Unread")
    public Response getUnreadNotifications(){
        final Timer.Context context = unreadnotifications_timer.time();
        try{
            List<Notification> listOfAllNotifications = notificationsBean.getNotifications();
            List<Notification> listOfUnreadNotifications = new ArrayList<Notification>();
            for(Notification singleNotification : listOfAllNotifications){
                if(singleNotification.getSeen() != null && singleNotification.getSeen() == 0){
                    listOfUnreadNotifications.add(singleNotification);
                }
            }
            context.stop();
            return Response.ok(listOfUnreadNotifications).build();
        }catch(Exception e){
            context.stop();
            return Response.status(Response.Status.BAD_REQUEST).build();

        }



    }


    @GET
    @Path("/UserUnread/{userid}")
    public Response getUserUnreadNotifications(@PathParam("userid") Integer userid){
        try {
            List<Notification> listOfAll = notificationsBean.getNotifications();
            List<Notification> foundNotifications = new ArrayList<Notification>();
            if (listOfAll != null) {
                for (Notification singleNotification : listOfAll) {
                    if (singleNotification.getUserid() != null && singleNotification.getUserid() == userid && singleNotification.getSeen() != null && singleNotification.getSeen() == 0) {
                        foundNotifications.add(singleNotification);
                    }
                }
            }

            return Response.ok(foundNotifications).build();
        }catch(Exception e){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("/User/{userid}")
    @Metered(name="get-user-notifications")
    public Response getUserNotifications(@PathParam("userid") Integer userid){
        try {
            List<Notification> listOfAll = notificationsBean.getNotifications();
            List<Notification> foundNotifications = new ArrayList<Notification>();
            if (listOfAll != null) {
                for (Notification singleNotification : listOfAll) {
                    if (singleNotification.getUserid() != null && singleNotification.getUserid() == userid) {
                        foundNotifications.add(singleNotification);
                    }
                }
            }

            return Response.ok(foundNotifications).build();
        }catch(Exception e){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    @POST
    @Path("create")
    @Metered(name="count_post_create_notification")
    public Response createNotification (Notification notification){
        System.out.println(notification.getNotificationtext());
        try {
            if (notification == null) {
                return Response.status(Response.Status.GONE).build();
            } else {


                if ((notification.getUserid() == null || notification.getNotificationtext().isEmpty() || notification.getRideid() == null)) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                } else {
                    if (notification.getSeen() == null) {
                        notification.setSeen(0);
                    }
                    notification = notificationsBean.createNotification(notification);
                }

                if (notification.getId() != null) {
                    return Response.status(Response.Status.CREATED).entity(notification).build();
                } else {
                    return Response.status(Response.Status.CONFLICT).entity(notification).build();
                }
                //return Response.status(Response.Status.ACCEPTED).entity("to pa gre tako").build();
            }
        }catch(Exception e){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("/{notificationid}")
    public Response getNotification(@PathParam("notificationid") Integer notificationid){
        try {
            Notification what = notificationsBean.getNotification(notificationid);
            if (what == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            Notification newwhat = new Notification();
            newwhat.setSeen(10);
            newwhat.setNotificationtext(what.getNotificationtext());
            newwhat.setUserid(what.getUserid());
            notificationsBean.editNotfication(what.getId(), newwhat);
            return Response.status(Response.Status.OK).entity(what).build();
        }catch(Exception e){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

}
