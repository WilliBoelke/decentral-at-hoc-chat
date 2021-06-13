package htwb.ai.willi.router;

import htwb.ai.willi.SendService.SendService;
import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RouteReply;
import htwb.ai.willi.message.RouteRequest;
import htwb.ai.willi.message.SendTextRequest;
import htwb.ai.willi.routing.RoutingTable;
import jdk.jshell.execution.Util;


/**
 * A router instance takes a incoming or outgoing request
 * and decides what to do next wih it based on its parameters
 *
 */
public abstract class Router
{


     public abstract void route(Request request);

     public abstract void requestFromMe(Request request);


     public  void requestToForward(Request request)
     {
          RoutingTable.getInstance().addRoute(request);

          if(request instanceof SendTextRequest)
          {
               if(RoutingTable.getInstance().getNextInRouteTo(request.getDestinationAddress()) == -1)
               {
                    return;
               }
               SendService.getInstance().send(prepareForwardRequest(request));
          }
     }

     void requestForMe(Request request)
     {

     }

     protected boolean isRequestForMe(Request request)
     {
          if (request.getDestinationAddress() == (byte)13)
          {
               return true;
          }
          return false;
     }

     protected boolean isRequestFromMe(Request request)
     {
          if (request.getOriginAddress() == (byte)13)
          {
               return true;
          }
          return false;
     }

     protected boolean isRequestToForward(Request request)
     {
          if(request.getOriginAddress() !=(byte)13 )
          {
               if(request.getDestinationAddress() != (byte)13)
               {
                    return true;
               }
          }
          return false;
     }

     public static Request prepareForwardRequest(Request request)
     {
          byte nextHop = RoutingTable.getInstance().getNextInRouteTo(request.getDestinationAddress());

          if(request instanceof RouteReply) {
               RouteReply preparedRequest = (RouteReply) request;
               byte hopCount = ((RouteReply) request).getHopCount();
               hopCount++;
               preparedRequest.setHopCount(hopCount);
               if (nextHop != -1)
               {
                    (preparedRequest).setNextHopInRoute(nextHop);
               }

               return preparedRequest;
          }

          if(request instanceof SendTextRequest)
          {
               SendTextRequest preparedRequest = (SendTextRequest) request;
               if (nextHop != -1)
               {
                    preparedRequest.setNextHopInRoute(nextHop);
               }

               return preparedRequest;
          }

          if(request instanceof RouteRequest)
          {
               RouteRequest preparedRequest = (RouteRequest) request;
               byte hopCount = ((RouteRequest) request).getHopCount();
               hopCount++;
               preparedRequest.setHopCount(hopCount);
               return preparedRequest;
          }

          return null;
     }
}
