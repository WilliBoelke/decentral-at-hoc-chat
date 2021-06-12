package htwb.ai.willi.routingManager;

import htwb.ai.willi.message.Request;

public abstract class Manager
{

     public abstract void processRequest(Request request);

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
