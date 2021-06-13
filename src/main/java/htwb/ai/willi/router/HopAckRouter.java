package htwb.ai.willi.router;

import htwb.ai.willi.SendService.SendService;
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
          SendService.getInstance().gotReply(request);
     }

     @Override
     public void requestToForward(Request request)
     {
     }
}
