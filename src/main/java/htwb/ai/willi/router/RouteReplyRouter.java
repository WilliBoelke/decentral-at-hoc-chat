package htwb.ai.willi.router;

import htwb.ai.willi.SendService.Dispatcher;
import htwb.ai.willi.message.Request;
import htwb.ai.willi.routing.RoutingTable;


/**
 * Gets every incoming RouteReply
 * if the RouteReply has this nodes
 * address as its Destination it will be given to the SendManager
 * who is most likely waiting for a Reply to a previously send Request
 */
public class RouteReplyRouter extends Router
{
     @Override
     public void route(Request request)
     {
          RoutingTable.getInstance().addRoute(request);
          dispatchRouteReplyAc(request.getLastHopInRoute());
          if (isRequestForMe(request))
          {
               requestForMe(request);
          }
          else if (isRequestToForward(request))
          {
               requestToForward(request);
          }
     }

     @Override
     public void requestFromMe(Request request)
     {

     }


     @Override
     void requestForMe(Request request)
     {
          super.requestForMe(request);
          Dispatcher.getInstance().gotReply(request);
     }
}
