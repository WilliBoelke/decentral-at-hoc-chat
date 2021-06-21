package htwb.ai.willi.router;

import htwb.ai.willi.controller.Address;
import htwb.ai.willi.dataProcessor.UserCommandProcessor;
import htwb.ai.willi.message.Acks.HopAck;
import htwb.ai.willi.message.Acks.RouteReplyAck;
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
          System.out.println("\n\n ====>RECEIVED" + request.getAsReadable());
          anyCase(request);
          if (false == request instanceof HopAck || request instanceof RouteReplyAck == false)
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

     protected abstract void requestForMe(Request request);


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
          return request.getOriginAddress() != Address.getInstance().getAddress();
     }


     protected abstract void dispatchAck(Request request);
}
