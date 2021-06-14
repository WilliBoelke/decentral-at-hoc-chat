package htwb.ai.willi.router;

import htwb.ai.willi.SendService.Dispatcher;
import htwb.ai.willi.message.Request;

public class HopAckRouter extends Router
{
     @Override
     public void route(Request request)
     {
          if (isRequestForMe(request))
          {
               requestForMe(request);
          }
     }

     @Override
     public void requestFromMe(Request request)
     {
          Dispatcher.getInstance().gotReply(request);
     }

     @Override
     public void requestToForward(Request request)
     {
     }
}
