package htwb.ai.willi.SendService;

import htwb.ai.willi.io.SerialOutput;
import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RouteAck;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Logger;

/**
 * The SendService manages the sending of messages and waits for
 * matching responses
 */
public class SendService
{

     public static final Logger LOG = Logger.getLogger(SendService.class.getName());
     private static SendService sendService;
     private final PropertyChangeSupport changes;

     private final long maxListeningTime = 5000;

     private SendService()
     {
          changes = new PropertyChangeSupport(this);
     }

     public static SendService getInstance()
     {
          if (sendService == null)
          {
               sendService = new SendService();
          }
          return sendService;
     }


     public void sendAsynchronously(Request request)
     {
          Transmission transmission = new Transmission(request);
          TransmissionCoordinator coordinator = new TransmissionCoordinator(transmission);
          registerPropertyChangeListener(coordinator);
          new Thread(coordinator).start();
     }


     public void send(Request request)
     {
          SerialOutput.getInstance().sendRequest(request);
     }


     public void registerPropertyChangeListener(PropertyChangeListener l)
     {
          changes.addPropertyChangeListener(l);
     }

     public void unregisterPropertyChangeListener(PropertyChangeListener l)
     {
          changes.removePropertyChangeListener(l);

     }

     /**
      * Called by the Routers if a Ack or Route repl arrived
      * Notifies the Coordinators that a new replay arrived
      * The will check if it matches their request and stop the retries
      * @param request
      */
     public void gotReply(Request request)
     {
          PropertyChangeEvent event = new PropertyChangeEvent(this, "incomingReply", new RouteAck(), request); // The oldValue is not of interest, therefore i just use a random request
          changes.firePropertyChange(event);
     }
}
