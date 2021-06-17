package htwb.ai.willi.router;

import htwb.ai.willi.SendService.Dispatcher;
import htwb.ai.willi.controller.Address;
import htwb.ai.willi.message.Acks.HopAck;
import htwb.ai.willi.message.Acks.RouteReplyAck;
import htwb.ai.willi.message.Acks.SendTextRequestAck;
import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RouteReply;
import htwb.ai.willi.message.RouteRequest;
import htwb.ai.willi.message.SendTextRequest;
import htwb.ai.willi.routing.RoutingTable;


/**
 * A router instance takes a incoming or outgoing request
 * and decides what to do next wih it based on its parameters
 */
public abstract class Router
{

     public void route(Request request)
     {
          anyCase(request);
          if (isRequestFromMe(request))
          {
               requestFromMe(request);
          }
          else if (isRequestForMe(request))
          {
               requestForMe(request);
          }
          else if (isRequestToForward(request))
          {
               requestToForward(request);
          }
     }


     protected abstract void anyCase(Request request);

     protected abstract void requestFromMe(Request request);


     protected abstract void requestToForward(Request request);
     /**
     {
        //  RoutingTable.getInstance().addRoute(request);

          if (request instanceof SendTextRequest)
          {
               if (RoutingTable.getInstance().getNextInRouteTo(request.getDestinationAddress()) == -1)
               {
                    return;
               }
               Dispatcher.getInstance().dispatchWithAck(prepareForwardRequest(request));
          }
          if(request instanceof RouteRequest)
          {
               Dispatcher.getInstance().dispatchBroadcast(prepareForwardRequest(request));
          }
     }*/

     protected  abstract void requestForMe(Request request);


     protected boolean isRequestForMe(Request request)
     {
          return request.getDestinationAddress() == Address.getInstance().getAddress();
     }

     protected boolean isRequestFromMe(Request request)
     {
          return request.getOriginAddress() == Address.getInstance().getAddress();
     }

     protected boolean isRequestToForward(Request request)
     {
          if (request.getOriginAddress() != Address.getInstance().getAddress())
          {
               return true;
          }
          return false;
     }



     protected abstract void dispatchAck(Request request);
}
