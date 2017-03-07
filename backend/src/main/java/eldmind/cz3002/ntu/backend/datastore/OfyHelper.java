package eldmind.cz3002.ntu.backend.datastore;

import com.googlecode.objectify.ObjectifyService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import eldmind.cz3002.ntu.backend.model.Custodian_Elderly_Relation;
import eldmind.cz3002.ntu.backend.model.TaskReminder;
import eldmind.cz3002.ntu.backend.model.User;

/**
 * Created by n on 23/2/2017.
 */

public class OfyHelper implements ServletContextListener {
    public void contextInitialized(ServletContextEvent event) {
        // This will be invoked as part of a warmup request, or the first user request if no warmup
        // request.
        ObjectifyService.register(User.class);
        ObjectifyService.register(TaskReminder.class);
        ObjectifyService.register(Custodian_Elderly_Relation.class);
    }

    public void contextDestroyed(ServletContextEvent event) {
        // App Engine does not currently invoke this method.
    }
}
