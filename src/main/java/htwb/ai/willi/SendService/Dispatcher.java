package htwb.ai.willi.SendService;

import htwb.ai.willi.io.SerialOutput;
import htwb.ai.willi.message.Acks.RouteReplyAck;
import htwb.ai.willi.message.Request;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Logger;

/**
 * The SendService manages the sending of messages and waits for
 * matching responses
 */
public class Dispatcher
{

     public static final Logger LOG = Logger.getLogger(Dispatcher.class.getName());
     private static Dispatcher sendService;
     private final PropertyChangeSupport changes;

     private final long maxListeningTime = 5000;

     private Dispatcher()
     {
          changes = new PropertyChangeSupport(this);
     }

     public static Dispatcher getInstance()
     {
          if (sendService == null)
          {
               sendService = new Dispatcher();
          }
          return sendService;
     }


     /**
      * For RouteRequests (from this node), SendTextRequests and RouteReplies
      * ----------------------------------------------------------------------
      * Will be send using the TransmissionCoordinator, which ill try
      * to send the Requests multiple times, till he gets a requests or the max retries are
      * reached
      * ----------------------------------------------------------------------
      *
      * @param transmission
      */
     public void dispatchWithAck(Transmission transmission)
     {
          LOG.info("dispatching and waiting for ACK");
          TransmissionCoordinator coordinator = new TransmissionCoordinator(transmission);
          registerPropertyChangeListener(coordinator);
          new Thread(coordinator).start();
     }


     /**
      * For forwarded RouteRequests
      *
      * @param request
      */
     public void dispatchBroadcast(Request request)
     {
          try
          {
               Thread.sleep(request.getTimeout() / 500); // TODO now so long waiting?
          }
          catch (InterruptedException e)
          {
               e.printStackTrace();
          }
          SerialOutput.getInstance().broadcast(request);
     }


     /**
      * For sending ACKs
      * ----------------------------------------------------------------------
      * Will only be send once, without waiting for a reply or ack of
      * any kind
      *
      * @param request
      */
     public void dispatch(Request request)
     {
          try
          {
               Thread.sleep(request.getTimeout() * 1000);
          }
          catch (InterruptedException e)
          {
               e.printStackTrace();
          }
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
      *
      * @param request
      */
     public void gotReply(Request request)
     {
          PropertyChangeEvent event = new PropertyChangeEvent(this, "incomingReply", new RouteReplyAck(), request);
          // The oldValue is not of interest, therefore i just use a random request
          changes.firePropertyChange(event);
     }
}
