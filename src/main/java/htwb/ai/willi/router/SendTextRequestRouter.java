package htwb.ai.willi.router;

import htwb.ai.willi.SendService.SendService;
import htwb.ai.willi.SendService.TransmissionCoordinator;
import htwb.ai.willi.io.SerialOutput;
import htwb.ai.willi.message.*;
import htwb.ai.willi.routing.RoutingTable;
import htwb.ai.willi.routing.SequenceNumberManager;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SendTextRequestRouter extends Router
{
     public static final Logger LOG = Logger.getLogger(SendTextRequestRouter.class.getName());

     @Override
     public void route(Request request)
     {
          LOG.info("process request");
          if(isRequestFromMe(request))
          {
               sendRequest(request);
          }
          else if(isRequestForMe(request))
          {
               requestForMe(request);
          }
          else if(isRequestToForward(request))
          {
               requestFromMe(request);
          }
     }

     private void requestForMe(Request request)
     {
          //Adding a new Route
          RoutingTable.getInstance().addRoute(request);
          // Output message
          SendTextRequest sendTextRequest = (SendTextRequest) request;
          LOG.info(sendTextRequest.getReadableMessage());
          //Sending ACK
          SendTextRequestAck acknowledge = new SendTextRequestAck() ;
          SendService.getInstance().send(acknowledge);
     }

     private void sendRequest(Request request)
     {
          LOG.info("Sending request");
          if (RoutingTable.getInstance().hasFittingRoute(request))
          {
               LOG.info("Found Route");
               SendService.getInstance().sendAsynchronously(request);
               return;
          }
          else
          {
               RouteRequest routeRequest = new RouteRequest((byte) 0, request.getDestinationAddress(), SequenceNumberManager.getInstance().getCurrentSequenceNumberAndIncrement());
               SendService.getInstance().sendAsynchronously(routeRequest);
          }
     }


     @Override
     public void requestFromMe(Request request)
     {

     }

     @Override
     public void requestToForward(Request request)
     {

     }


     public RouteRequest buildRequest(SendTextRequest sendTextRequest)
     {
          RouteRequest routeRequest = new RouteRequest((byte) 0, sendTextRequest.getDestinationAddress(), SequenceNumberManager.getInstance().getCurrentSequenceNumberAndIncrement());
          //TODO dest qequence number?
          return routeRequest;
     }
}
