package htwb.ai.willi.router;

import htwb.ai.willi.SendService.Dispatcher;
import htwb.ai.willi.SendService.Transmission;
import htwb.ai.willi.message.Acks.RouteReplyAck;
import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RouteReply;
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
     protected void anyCase(Request request)
     {
          this.dispatchAck(request);
          RoutingTable.getInstance().addRoute(request);
     }

     @Override
     protected void requestFromMe(Request request)
     {
          //not needed here
     }

     @Override
     protected void requestToForward(Request request)
     {
          RouteReply preparedToForward = (RouteReply) request;
          if (RoutingTable.getInstance().getRouteTo(preparedToForward.getDestinationAddress()) != null)
          {
               preparedToForward.setNextHopInRoute(RoutingTable.getInstance().getNextInRouteTo(preparedToForward.getDestinationAddress()));
               Transmission transmission = new Transmission(preparedToForward);
               transmission.setHops(RoutingTable.getInstance().getRouteTo(preparedToForward.getDestinationAddress()).getHops());
               Dispatcher.getInstance().dispatchWithAck(transmission);
          }
     }

     @Override
     protected void requestForMe(Request request)
     {
          Dispatcher.getInstance().gotReply(request);
     }


     @Override
     protected void dispatchAck(Request request)
     {
          RouteReplyAck ack = new RouteReplyAck();
          ack.setNextHopInRoute(request.getLastHopInRoute());
          Dispatcher.getInstance().dispatch(ack);
     }
}
