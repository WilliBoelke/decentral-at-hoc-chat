package htwb.ai.willi.router;

import htwb.ai.willi.SendService.Dispatcher;
import htwb.ai.willi.message.Request;

public class HopAckRouter extends Router
{


     @Override
     protected void anyCase(Request request)
     {
          Dispatcher.getInstance().gotReply(request);
     }

     @Override
     public void requestFromMe(Request request)
     {
          //not needed here
     }

     @Override
     public void requestToForward(Request request)
     {
          //not needed here
     }

     @Override
     protected void requestForMe(Request request)
     {
          //not needed here
     }

     @Override
     protected void dispatchAck(Request request)
     {
          //not needed here
     }
}
