package htwb.ai.willi.SendService;

import htwb.ai.willi.message.Request;

public class Transmission
{

     public static final byte STD_RETRIES = 5;

     /**
      * The Request to be send
      */
     private Request request;


     public Transmission(Request request)
     {
          this.request = request;
     }


     public static byte getStdRetries()
     {
          return STD_RETRIES;
     }

     public Request getRequest()
     {
          return request;
     }

     public void setRequest(Request request)
     {
          this.request = request;
     }
}

