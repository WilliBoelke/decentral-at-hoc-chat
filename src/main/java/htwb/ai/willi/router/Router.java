package htwb.ai.willi.router;

import htwb.ai.willi.message.Request;


/**
 * A router instance takes a incoming or outgoing request
 * and decides what to do next wih it based on its parameters
 *
 */
public abstract class Router
{


     public abstract void route(Request request);

     public abstract void requestFromMe(Request request);
     public abstract void requestToForward(Request request);

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


}
