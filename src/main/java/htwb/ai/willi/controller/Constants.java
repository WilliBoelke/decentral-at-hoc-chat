package htwb.ai.willi.controller;

/**
 * Contains all  values
 */
public class Constants
{


     private Constants()
     {
     }


     /**
      * The Raspberry's serial port
      * for the lora module
      */
     public static final String PORT = "/dev/ttyS0";

     /**
      * The broadcast Adress
      */
     public static final String BROADCAST_ADDRESS = "FFFF";

     public static final String CARRIAGE_RETURN_LINE_FEED = "\r\n";

     public static final int SEND_MESSAGE_TIMEOUT = 1000;

     public static final byte SDT_TTL = 5;

}
