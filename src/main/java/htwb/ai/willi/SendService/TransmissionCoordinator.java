package htwb.ai.willi.SendService;

import htwb.ai.willi.controller.Constants;
import htwb.ai.willi.io.SerialOutput;
import htwb.ai.willi.message.RouteRequest;
import htwb.ai.willi.message.SendTextRequest;
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
               SerialOutput.getInstance().sendRequest(transmission.getRequest());
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
               LOG.info("Received ACk for Route Request, your message will now be send ");
               RouteRequest routeRequest = (RouteRequest) transmission.getRequest();
               if (routeRequest.getSendTextRequest() != null)
               {
                    SendTextRequest sendTextRequest = routeRequest.getSendTextRequest();
                    //Setting the next hop
                    sendTextRequest.setNextHopInRoute(RoutingTable.getInstance().getNextInRouteTo(sendTextRequest.getDestinationAddress()));
                    SendService.getInstance().sendAsynchronously(routeRequest.getSendTextRequest());
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
     public void propertyChange(PropertyChangeEvent evt)
     {

     }
}
