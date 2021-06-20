package htwb.ai.willi.router;

import htwb.ai.willi.controller.Address;
import htwb.ai.willi.dataProcessor.UserCommandProcessor;
import htwb.ai.willi.message.Acks.HopAck;
import htwb.ai.willi.message.Acks.RouteReplyAck;
import htwb.ai.willi.message.Acks.SendTextRequestAck;
import htwb.ai.willi.message.Request;

import java.util.logging.Logger;


/**
 * A router instance takes a incoming or outgoing request
 * and decides what to do next wih it based on its parameters
 */
public abstract class Router
{
     public static final Logger LOG = Logger.getLogger(UserCommandProcessor.class.getName());

     public void route(Request request)
     {
          LOG.info("Routing Request of type : " + request.getType());
          anyCase(request);
          if( false == request instanceof HopAck || request instanceof RouteReplyAck == false)
          {
               if (isRequestFromMe(request))
               {
                    LOG.info("request from me ");
                    requestFromMe(request);
               }
               else if (isRequestForMe(request))
               {
                    LOG.info("request for me ");
                    requestForMe(request);
               }
               else if (isRequestToForward(request))
               {
                    LOG.info("request to forward ");
                    requestToForward(request);
               }
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
          LOG.info("Routing Request : Dest Address: " + request.getDestinationAddress());
          return request.getDestinationAddress() == Address.getInstance().getAddress();
     }

     protected boolean isRequestFromMe(Request request)
     {
          LOG.info("Routing Request : Origin Address: " + request.getOriginAddress());
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
