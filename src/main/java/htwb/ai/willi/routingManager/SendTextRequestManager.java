package htwb.ai.willi.routingManager;

import htwb.ai.willi.controller.Controller;
import htwb.ai.willi.io.SerialOutput;
import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RequestEncoderAndDecoder;
import htwb.ai.willi.routing.RoutingTable;

import java.util.logging.Logger;

public class SendTextRequestManager extends Manager
{
     public static final Logger LOG = Logger.getLogger(SendTextRequestManager.class.getName());

     @Override
     public void processRequest(Request request)
     {
          LOG.info("process request");
          if(isRequestFromMe(request))
          {
               LOG.info("sending request");
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
