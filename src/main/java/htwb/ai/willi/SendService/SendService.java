package htwb.ai.willi.SendService;

import htwb.ai.willi.io.SerialOutput;
import htwb.ai.willi.message.Request;

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

     public void gotReply(Request request)
     {

     }
}
