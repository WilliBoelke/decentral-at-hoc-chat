package htwb.ai.willi.SendService;

import htwb.ai.willi.controller.Address;
import htwb.ai.willi.controller.Constants;
import htwb.ai.willi.io.SerialOutput;
import htwb.ai.willi.message.*;
import htwb.ai.willi.message.Acks.HopAck;
import htwb.ai.willi.message.Acks.SendTextRequestAck;
import htwb.ai.willi.routing.RoutingTable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;


public class TransmissionCoordinator implements PropertyChangeListener, Runnable
{
     public static final Logger LOG = Logger.getLogger(TransmissionCoordinator.class.getName());

     private final Transmission transmission;

     private boolean finished;

     private boolean failed;

     public TransmissionCoordinator(Transmission transmission)
     {
          this.transmission = transmission;
     }


     @Override
     public void run()
     {
          if(transmission.getRequest() instanceof  RouteRequest)
          {
               SerialOutput.getInstance().broadcast(transmission.getRequest());
               waitForAck(30);
          }
          else
          {
               for (int i = 0; i < Transmission.STD_RETRIES; i++)
               {
                    if (finished)
                    {
                         return;
                    }
                    else
                    {

                         LOG.info("Trying to send, tr  : " + i);
                         SerialOutput.getInstance().sendRequest(transmission.getRequest());
                    }
                    waitForAck(5);
               }

               if (!finished)
               {
                    LOG.info("Transmission failed, out of retries");
                    onTransmissionFailed();
               }
          }
     }


     public void waitForAck(Integer times)
     {
          LOG.info("Waiting for answer");
          for (int i = 0; i < times; i++)
          {
               try
               {
                    Thread.sleep(Constants.SEND_MESSAGE_TIMEOUT);
               }
               catch (InterruptedException e)
               {
                    e.printStackTrace();
               }
          }
          LOG.info("Finished waiting");
     }


     private void onRouteRequestSuccess()
     {
          if (transmission.getRequest() instanceof RouteRequest)
          {
               LOG.info("Received reply for Route Request, your message will now be send ");
               RouteRequest routeRequest = (RouteRequest) transmission.getRequest();
               if (routeRequest.getSendTextRequest() != null)
               {
                    SendTextRequest sendTextRequest = routeRequest.getSendTextRequest();
                    //Setting the next hop
                    sendTextRequest.setNextHopInRoute(RoutingTable.getInstance().getNextInRouteTo(sendTextRequest.getDestinationAddress()));
                    Dispatcher.getInstance().dispatchWithAck(routeRequest.getSendTextRequest());
               }
          }
     }


     private void onTransmissionFailed()
     {
          LOG.info("The message was send unsuccessfully" + Transmission.STD_RETRIES + " times. Consider a different destination address");
          this.finished = false;
          this.failed = true;
          RoutingTable.getInstance().removeRoute(transmission.getRequest().getDestinationAddress());
          sendErrorRequest();
     }

     /**
      * Send a RouteError if the transmission wasn't successful
      *
      */
     private void sendErrorRequest()
     {
          LOG.info("Send link break error to other nodes, unreachable : " + this.transmission.getRequest().getNextHopInRoute());
          RouteError routeError = new RouteError();
          routeError.setUnreachableDestinationAddress(this.transmission.getRequest().getNextHopInRoute());
          routeError.setUnreachableDestinationSequenceNumber(RoutingTable.getInstance().getNewesKnownSequenceNumberFromNode(transmission.getRequest().getNextHopInRoute()));
          routeError.setDestinationCount((byte) 1);
          routeError.setAdditionalAddress((byte) 0);
          routeError.setAdditionalSequenceNumber((byte) 0);
          Dispatcher.getInstance().dispatchBroadcast(routeError);
     }

     @Override
     public void propertyChange(PropertyChangeEvent event)
     {

          Request incomingReply = (Request) event.getNewValue();
          LOG.info("Received a Reply : " + incomingReply.encode());

          // Outgoing SendTextRequest
          if(this.transmission.getRequest() instanceof  SendTextRequest)
          {
               LOG.info("This a SendTextRequest waiting for a SendTextAck or a HopAck , got a reply of type " + incomingReply.getType());
               //from me
               if (this.transmission.getRequest().getOriginAddress() == Address.getInstance().getAddress())
               {
                    if(incomingReply instanceof SendTextRequestAck && ((SendTextRequestAck) incomingReply).getMessageSequenceNumber() == ((SendTextRequestAck) transmission.getRequest()).getMessageSequenceNumber())
                    {
                         this.finished = true;
                    }
               }
               // If forwarded
               else if(this.transmission.getRequest().getOriginAddress() != Address.getInstance().getAddress())
               {
                    if(incomingReply instanceof HopAck && ((HopAck) incomingReply).getMessageSequenceNumber() ==  ((SendTextRequestAck) transmission.getRequest()).getMessageSequenceNumber())
                    {
                         finished = true;
                    }
               }
          }
          // RouteRequest
          else if(this.transmission.getRequest() instanceof RouteRequest && incomingReply instanceof  RouteReply)
          {
               if (incomingReply.getOriginAddress() == this.transmission.getRequest().getDestinationAddress())
               {
                    this.finished = true;
                    onRouteRequestSuccess();
               }
          }
          else
          {
               LOG.info("Reply not for this request");
          }
     }
}
