package htwb.ai.willi.router;

import htwb.ai.willi.SendService.Dispatcher;
import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RouteRequest;
import htwb.ai.willi.message.SendTextRequest;
import htwb.ai.willi.message.Acks.SendTextRequestAck;
import htwb.ai.willi.routing.RoutingTable;
import htwb.ai.willi.routing.SequenceNumberManager;

import java.util.logging.Logger;

public class SendTextRequestRouter extends Router
{
     public static final Logger LOG = Logger.getLogger(SendTextRequestRouter.class.getName());

     @Override
     public void route(Request request)
     {
          dispatchHopAck((SendTextRequest) request);
          LOG.info("process request");
          if (isRequestFromMe(request))
          {
               requestFromMe(request);
          }
          else if (isRequestForMe(request))
          {
               dispatchSendTextAck((SendTextRequest) request);
               requestForMe(request);
          }
          else if (isRequestToForward(request))
          {
               requestToForward(request);
          }
     }


     @Override
     void requestForMe(Request request)
     {
          //Adding a new Route
          RoutingTable.getInstance().addRoute(request);
          // Output message
          SendTextRequest sendTextRequest = (SendTextRequest) request;
          LOG.info(sendTextRequest.getReadableMessage());
          //Sending ACK
          SendTextRequestAck acknowledge = new SendTextRequestAck();
          Dispatcher.getInstance().dispatch(acknowledge);
     }

     @Override
     public void requestFromMe(Request request)
     {
          LOG.info("Sending request");
          if (RoutingTable.getInstance().hasFittingRoute(request))
          {
               LOG.info("Found Route");
               Dispatcher.getInstance().dispatchWithAck(request);
          }
          else
          {
               LOG.info("No matching route found");
               RouteRequest routeRequest = buildRequest((SendTextRequest) request);
               Dispatcher.getInstance().dispatchWithAck(routeRequest);
          }
     }


     public RouteRequest buildRequest(SendTextRequest sendTextRequest)
     {
          RouteRequest routeRequest = new RouteRequest((byte) 0, sendTextRequest.getDestinationAddress(),
                  SequenceNumberManager.getInstance().getCurrentSequenceNumberAndIncrement(), sendTextRequest.getOriginAddress());
          //TODO dest qequence number?
          return routeRequest;
     }
}
