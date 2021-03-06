package htwb.ai.willi.router;

import htwb.ai.willi.SendService.Dispatcher;
import htwb.ai.willi.SendService.Transmission;
import htwb.ai.willi.controller.Address;
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
     protected void anyCase(Request request)
     {
          // updating route and precursors
          RoutingTable.getInstance().addRoute(request);
     }

     @Override
     protected void requestFromMe(Request request)
     {
          //not needed here, drop looping request
     }

     @Override
     protected void requestToForward(Request request)
     {
          RouteRequest routeRequest = (RouteRequest) request;
          if (!RoutingTable.getInstance().getRouteTo(routeRequest.getOriginAddress()).broadcastAlreadyReceived(routeRequest.getBroadcastID())) // Broadcast already send
          {
               RoutingTable.getInstance().getRouteTo(routeRequest.getOriginAddress()).addBroadcastId(routeRequest.getBroadcastID()); // Adding the Broadcast ID to the list
               routeRequest.setHopCount((byte) (routeRequest.getHopCount() + 1));
               Dispatcher.getInstance().dispatchBroadcast(routeRequest);
          }
     }

     @Override
     protected void requestForMe(Request request)
     {
          LOG.info("Route Request for me, sending Route Reply");
          //If the request is for this node , we need to send a reply :
          RouteReply reply = new RouteReply();
          try
          {
               reply.setNextHopInRoute(RoutingTable.getInstance().getNextInRouteTo(request.getOriginAddress()));
               reply.setHopCount((byte) (((RouteRequest) request).getHopCount() + 1));
               reply.setOriginAddress(Address.getInstance().getAddress());
               reply.setDestinationAddress(request.getOriginAddress());
               reply.setDestinationSequenceNumber(((RouteRequest) request).getOriginSequenceNumber());
               reply.setOriginSequenceNumber(SequenceNumberManager.getInstance().getCurrentSequenceNumberAndIncrement());
               reply.setRemainingLifeTime(Constants.SDT_TTL);
               Transmission transmission = new Transmission(reply);
               transmission.setHops(RoutingTable.getInstance().getRouteTo(request.getOriginAddress()).getHops());
               Dispatcher.getInstance().dispatchWithAck(transmission);
          }
          catch (NullPointerException e)
          {
               e.printStackTrace();
               LOG.info("Something went wrong while building the route reply");
          }

     }

     @Override
     protected void dispatchAck(Request request)
     {

     }
}
