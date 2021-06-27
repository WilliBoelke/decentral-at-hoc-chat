package htwb.ai.willi.router;

import htwb.ai.willi.SendService.Dispatcher;
import htwb.ai.willi.message.Acks.SendTextRequestAck;
import htwb.ai.willi.message.Request;
import htwb.ai.willi.routing.RoutingTable;

public class SendTextRequestAckRouter extends Router
{

     @Override
     protected void anyCase(Request request)
     {
          //not needed here
     }

     @Override
     protected void requestFromMe(Request request)
     {
          //not needed here
     }

     @Override
     protected void requestToForward(Request request)
     {
          SendTextRequestAck ack = (SendTextRequestAck) request;
          if(RoutingTable.getInstance().hasFittingRoute(ack))
          {
               ack.setNextHopInRoute(RoutingTable.getInstance().getNextInRouteTo(ack.getDestinationAddress()));
               Dispatcher.getInstance().dispatch(ack);
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
          //not needed here
     }
}
