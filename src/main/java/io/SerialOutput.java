package io;

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
     private static PrintWriter printWriter;


     //--------------class variable--------------//

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
      * Sets the PrintWriter and flush's it
      *
      * @param printWriter
      *         PrintWriter
      */
     public void setPrintWriter(PrintWriter printWriter)
     {
          this.printWriter = printWriter;
          this.printWriter.flush();
     }

     /**
      *
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

          int messageLength= message.length();
          try
          {
               Thread.sleep(250);
          }
          catch (InterruptedException e)
          {
               e.printStackTrace();
          }
          // Writing the String with AT command and carriage return
          printWriter.println("AT+SEND=" + messageLength + System.lineSeparator());
          printWriter.flush();
          printWriter.println(message + System.lineSeparator());
          printWriter.flush();
     }

     /**
      * Writes an AT command configuration string  to the Serial port
      * and thus to the Lora module
      *
      * @param config
      * the configuration Strinf
      */
     public void sendConfiguration(String config)
     {
          try
          {
               printWriter.println(config + System.lineSeparator());
               printWriter.flush();
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
          printWriter.close();
     }

}
