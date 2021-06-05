package controller;

/**
 * Contains all  values
 *
 */
public class Constants
{


     private Constants()
     {}


     /**
      * The Raspberry's serial port
      * for the lora module
      */
     public static final String PORT = "/dev/ttyS0";


     /**
      * The Lora modules configuration command with
      * the chosen values
      */
     public static final String CONFIG = "AT+CFG=433500000,5,9,7,1,1,0,0,0,0,3000,8,4";

     /**
      * The broadcast Adress
      */
     public static final String BROADCAST_ADDRESS = "FFFF";

}
