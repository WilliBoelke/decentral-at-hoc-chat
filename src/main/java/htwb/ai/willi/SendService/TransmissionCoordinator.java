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
          for (int i = 0; i < Transmission.STD_RETRIES; i++)
          {
               if (finished)
               {
                    return;
               }
               if(transmission.getRequest() instanceof RouteRequest)
               {
                    SerialOutput.getPrintWriter().broadcast(transmission.getRequest());
               }
               else
               {
                    SerialOutput.getPrintWriter().sendRequest(transmission.getRequest());
               }
               waitForAck(5);
          }

          if (!finished)
          {
               onTransmissionFailed();
          }
     }


     public void waitForAck(Integer times)
     {
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
          LOG.info("The message was send unsuccessfully" + Transmission.STD_RETRIES + " times. Consider a different " +
                  "destination address");
          this.finished = false;
          this.failed = false;
          RoutingTable.getInstance().removeRoute(transmission.getRequest().getDestinationAddress());
          sendErrorRequest();
     }

     private void sendErrorRequest()
     {
     }

     @Override
     public void propertyChange(PropertyChangeEvent event)
     {
          Request incomingReply = (Request) event.getNewValue();

          // Outgoing SendTextRequest
          if(this.transmission.getRequest() instanceof  SendTextRequest)
          {
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
               }
          }
     }
}
