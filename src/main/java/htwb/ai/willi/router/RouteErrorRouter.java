package htwb.ai.willi.router;

import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RouteError;
import htwb.ai.willi.routing.RoutingTable;

public class RouteErrorRouter extends Router
{

     @Override
     protected void anyCase(Request request)
     {
          RouteError routeError = (RouteError) request;
          byte address = routeError.getUnreachableDestinationAddress();
          byte sequenceNumber = routeError.getUnreachableDestinationSequenceNumber();

          RoutingTable.Route routeToUnreachable = RoutingTable.getInstance().getRouteTo(address);
          if (routeToUnreachable.getDestinationSequenceNumber() < sequenceNumber)
          {
               RoutingTable.getInstance().removeRoute(address);
          }
     }

     @Override
     protected void requestFromMe(Request request)
     {

     }

     @Override
     protected void requestToForward(Request request)
     {

     }

     @Override
     protected void requestForMe(Request request)
     {

     }


     @Override
     protected void dispatchAck(Request request)
     {

     }
}
