package htwb.ai.willi.io;

import htwb.ai.willi.controller.Constants;
import htwb.ai.willi.message.Request;

import java.io.PrintWriter;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Singleton to output to the SerialPort
 * Needs a PrintWriter set to the SerialPort of the Lora
 * module
 */
public class SerialOutput
{

     //--------------static variable--------------//

     private static SerialOutput SerialOutput;

     /**
      * PrintWriter which has a reference to the serial output
      */
     private static PrintWriter instance;


     //--------------instance variable--------------//

     /**
      * The log tag
      */
     public final String TAG = this.getClass().getSimpleName();
     /**
      * The logger
      */
     public final Logger LOG = Logger.getLogger(TAG);


     //--------------constructors and init--------------//

     /**
      * Private constructor
      */
     private SerialOutput()
     {
     }


     /**
      * Get instance following the singleton pattern
      *
      * @return the SerialOutput
      */
     public static SerialOutput getInstance()
     {
          if (SerialOutput == null)
          {
               SerialOutput = new SerialOutput();
          }
          return SerialOutput;
     }


     //--------------getter and setter--------------//

     /**
      * Sets the print writer
      *
      * @param printWriter
      *         the new PrintWriter(to the serial port)
      */
     public void setPrintWriter(PrintWriter printWriter)
     {
          instance = printWriter;
          instance.flush();
     }


     //--------------getter and setter--------------//

     /**
      * Writes a normal String to the serial output
      *
      * @param message
      *         String
      */
     public void sendString(String message)
     {
          //Random wait time to avoid collisions with other nodes
          int waitTime = new Random().nextInt(1000);
          try
          {
               Thread.sleep(waitTime);
          }
          catch (InterruptedException e)
          {
               e.printStackTrace();
          }

          int messageLength = message.length();
          try
          {
               Thread.sleep(250);
          }
          catch (InterruptedException e)
          {
               e.printStackTrace();
          }
          // Writing the String with AT command and carriage return
          instance.println("AT+SEND=" + messageLength + Constants.CARRIAGE_RETURN_LINE_FEED);
          instance.flush();
          instance.println(message + Constants.CARRIAGE_RETURN_LINE_FEED);
          instance.flush();
     }


     //--------------public methods--------------//

     /**
      * Writes an AT commands /  configuration string  to the Serial port
      * and thus to the Lora module
      *
      * @param config
      *         the configuration Strinf
      */
     public void sendConfiguration(String config)
     {
          LOG.info("sendConfiguration: " + config);
          try
          {
               instance.println(config + Constants.CARRIAGE_RETURN_LINE_FEED);
               instance.flush();
               Thread.sleep(2000);
          }
          catch (InterruptedException e)
          {
               e.printStackTrace();
          }
     }

     /**
      * Closes the PrintWriter Stream
      */
     public void close()
     {
          instance.close();
     }

     public void sendRequest(Request request)
     {
          String encodedRequest = request.encode();

          instance.println("AT+DEST=" + request.getNextHopInRoute() + Constants.CARRIAGE_RETURN_LINE_FEED);
          instance.flush();
          instance.println("AT+SEND=" + encodedRequest.length() + Constants.CARRIAGE_RETURN_LINE_FEED);
          instance.flush();
          instance.println(encodedRequest + Constants.CARRIAGE_RETURN_LINE_FEED);
          instance.flush();
     }

     public void broadcast(Request request)
     {
          String encodedRequest = request.encode();

          instance.println("AT+DEST=" + Constants.BROADCAST_ADDRESS+ Constants.CARRIAGE_RETURN_LINE_FEED);
          instance.flush();
          instance.println("AT+SEND=" + encodedRequest.length() + Constants.CARRIAGE_RETURN_LINE_FEED);
          instance.flush();
          instance.println(encodedRequest + Constants.CARRIAGE_RETURN_LINE_FEED);
          instance.flush();
     }
}
