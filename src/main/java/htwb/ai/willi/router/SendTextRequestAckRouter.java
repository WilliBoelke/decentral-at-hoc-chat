package htwb.ai.willi.router;

import htwb.ai.willi.SendService.Dispatcher;
import htwb.ai.willi.message.Request;

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
          //not needed here
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
