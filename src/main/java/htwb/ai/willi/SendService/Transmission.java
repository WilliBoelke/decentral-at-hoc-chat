package htwb.ai.willi.SendService;

import htwb.ai.willi.message.Request;

public class Transmission
{

     public static final byte STD_RETRIES = 3;

     /**
      * The Request to be send
      */
     private Request request;

     private int hops;

     public Transmission(Request request)
     {
          this.hops = 1;
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

     public int getHops()
     {
          return hops;
     }

     public void setHops(int hops)
     {
          this.hops = hops;
     }
}

