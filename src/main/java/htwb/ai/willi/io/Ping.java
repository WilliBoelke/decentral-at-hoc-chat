package htwb.ai.willi.io;

import java.util.logging.Logger;

public class Ping implements Runnable
{

     //--------------static variables--------------//

     public static final Logger LOG = Logger.getLogger(Ping.class.getName());

     private final int interval;
     private final String message;

     public Ping(int interval, String msg)
     {
          this.interval = interval;
          this.message = msg;
     }


     @Override
     public void run()
     {
          while (true)
          {
               SerialOutput.getPrintWriter().sendString(message);
               try
               {
                    Thread.sleep(interval);
               }
               catch (InterruptedException e)
               {
                    e.printStackTrace();
               }
          }
     }
}
