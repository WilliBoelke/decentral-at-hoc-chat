package htwb.ai.willi.routingManager;

import htwb.ai.willi.io.SerialOutput;
import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RequestEncoderAndDecoder;
import htwb.ai.willi.routing.RoutingTable;

public class SendTextRequestManager extends Manager
{
     @Override
     public void processRequest(Request request)
     {
          if(isRequestFromMe(request))
          {
               sendRequest(request);
          }
          else if(isRequestForMe(request))
          {
               requestToForward(request);
          }
          else if(isRequestToForward(request))
          {
               requestToForward(request);
          }
     }

     private void sendRequest(Request request)
     {
          if(RoutingTable.getInstance().hasFittingRoute(request))
          {
               SerialOutput.getInstance().sendString(new RequestEncoderAndDecoder().encode(request));
          }
     }


     @Override
     public void requestFromMe(Request request)
     {

     }

     @Override
     public void requestToForward(Request request)
     {

     }
}
