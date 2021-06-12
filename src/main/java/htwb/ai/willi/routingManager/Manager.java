package htwb.ai.willi.routingManager;

import htwb.ai.willi.message.Request;

public abstract class Manager
{

     public void processRequest(Request request)
     {

     }

     public abstract void requestFromMe(Request request);
     public abstract void requestToForward(Request request);

     private boolean requestForMe(Request request)
     {
          if (request.getDestinationAddress() == (byte)13)
          {
               return true;
          }
          return false;
     }


     private boolean isRequestFromMe(Request request)
     {
          if (request.getOriginAddress() == (byte)13)
          {
               return true;
          }
          return false;
     }


}
