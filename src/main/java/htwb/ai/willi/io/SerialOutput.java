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

     private static SerialOutput instance;

     /**
      * PrintWriter which has a reference to the serial output
      */
     private static PrintWriter printWriter;


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
          if (instance == null)
          {
               instance = new SerialOutput();
          }
          return instance;
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
          htwb.ai.willi.io.SerialOutput.printWriter = printWriter;
          htwb.ai.willi.io.SerialOutput.printWriter.flush();
     }


     //--------------public methods--------------//

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
          printWriter.println("AT+SEND=" + messageLength + Constants.CARRIAGE_RETURN_LINE_FEED);
          printWriter.flush();
          printWriter.println(message + Constants.CARRIAGE_RETURN_LINE_FEED);
          printWriter.flush();
     }


     /**
      * Writes an AT commands /  configuration string  to the Serial port
      * and thus to the Lora module
      *
      * @param config
      *         the configuration String
      */
     public void sendConfiguration(String config)
     {
          LOG.info("sendConfiguration: " + config);
          try
          {
               printWriter.println(config + Constants.CARRIAGE_RETURN_LINE_FEED);
               printWriter.flush();
               Thread.sleep(1000);
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
          printWriter.close();
     }

     public void sendRequest(Request request)
     {
          String encodedRequest = request.encode();
          System.out.println("\n\n ====>UNICAST" + request.getAsReadable() );
          try
          {
               printWriter.println("AT+DEST=" + "00" + request.getNextHopInRoute() + Constants.CARRIAGE_RETURN_LINE_FEED);
               printWriter.flush();
               printWriter.println("AT+SEND=" + encodedRequest.length() + Constants.CARRIAGE_RETURN_LINE_FEED);
               printWriter.flush();
               printWriter.println(encodedRequest + Constants.CARRIAGE_RETURN_LINE_FEED);
               printWriter.flush();
          }
          catch (NullPointerException e)
          {
               LOG.info("next hop in route was null");
          }
     }

     public void broadcast(Request request)
     {
          String encodedRequest = request.encode();
          System.out.println("\n\n ====>BROADCAST" + request.getAsReadable() );
          printWriter.println("AT+DEST=" + Constants.BROADCAST_ADDRESS + Constants.CARRIAGE_RETURN_LINE_FEED);

          printWriter.flush();
          try
          {
               Thread.sleep(250);
          }
          catch (InterruptedException e)
          {
               e.printStackTrace();
          }
          printWriter.println("AT+SEND=" + encodedRequest.length() + Constants.CARRIAGE_RETURN_LINE_FEED);
          printWriter.flush();
          printWriter.println(encodedRequest + Constants.CARRIAGE_RETURN_LINE_FEED);
          printWriter.flush();
     }
}
