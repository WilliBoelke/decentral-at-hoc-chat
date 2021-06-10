package htwb.ai.willi;

import htwb.ai.willi.controller.Controller;

import java.util.logging.Logger;

public class App
{
     //--------------class variable--------------//

     /**
      * The log tag
      */
     public final String TAG = this.getClass().getSimpleName();
     /**
      * The logger
      */
     public final Logger LOG = Logger.getLogger(TAG);


     public static void main(String[] args)
     {
          String addr = "";
          if (args.length != 1)
          {
               System.out.println("please set an adr4ess for your node...");
               System.out.println("using 0013 now");
               addr = "0013";
          }
          else
          {
               addr = args[0];
          }

          Controller controller = new Controller();
          controller.start(addr);
     }
}
