package htwb.ai.willi.router;

import htwb.ai.willi.SendService.Dispatcher;
import htwb.ai.willi.controller.Constants;
import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RouteReply;
import htwb.ai.willi.message.RouteRequest;
import htwb.ai.willi.routing.RoutingTable;
import htwb.ai.willi.routing.SequenceNumberManager;

/**
 * Gets every single RouteRequest, and will Add a route to the Routing Table
 * iFi the Request has a different destination address then this node , it will be forwarded
 * to the next hops address (from the routing table )
 */
public class RouteRequestRouter extends Router
{
     @Override
     public void route(Request request)
     {
          //RoutingTable.getInstance().addRoute(request);
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
          //If the request is for this node , we need to send a reply :
          RouteReply reply = new RouteReply(((RouteRequest) request).getHopCount(), request.getOriginAddress(),
                  ((RouteRequest) request).getOriginSequenceNumber(),
                  SequenceNumberManager.getInstance().getCurrentSequenceNumberAndIncrement());
          reply.setRemainingLifeTime(Constants.SDT_TTL);
          Dispatcher.getInstance().dispatch(reply);
     }


}
