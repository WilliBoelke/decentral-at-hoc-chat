package htwb.ai.willi.io;

import purejavacomm.SerialPortEvent;
import purejavacomm.SerialPortEventListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Manages input from the Serial port asynchronous
 */
public class SerialInput implements SerialPortEventListener, Runnable
{
     public static final Logger LOG = Logger.getLogger(SerialInput.class.getName());

     /**
      * PropertyChangeSupport, updates with new received String
      */
     private PropertyChangeSupport changes;

     /**
      * SerialInput from LoRa-Module
      */
     private static SerialInput eventListener;

     /**
      * Scanner for SerialInput
      */
     private Scanner inputScanner;

     private SerialInput()
     {
          changes = new PropertyChangeSupport(this);
     }

     /**
      * @return The SerialInput Singleton instance
      */
     public static synchronized SerialInput getInstance()
     {
          if (eventListener == null)
          {
               eventListener = new SerialInput();
          }

          return eventListener;
     }

     /**
      *  Starts the SerialInput thread and
      *  reads new input from it in an asynchronous manner
      *  notifies Listeners when a new event occurs (new input)
      */
     @Override
     public void run()
     {
          while (true)
          {
               if (inputScanner.hasNext())
               {
                    String msg = inputScanner.nextLine();
                    LOG.info("run: received message "+ msg);
                    changes.firePropertyChange(new PropertyChangeEvent(this, "serialInput", "", msg));

               }
          }
     }

     /**
      * Lets new LPropertyChangeListener register
      * to get notified about incoming messages
      */
     public void registerPropertyChangeListener(PropertyChangeListener l)
     {
          changes.addPropertyChangeListener(l);
     }

     /**
      * Unregisters a properties change listener
      */
     public void unregisterPropertyChangeListener(PropertyChangeListener l)
     {
          changes.removePropertyChangeListener(l);
     }

     /**
      * Closes the input stream
      */
     public void close()
     {
          inputScanner.close();
     }

     @Override
     public void serialEvent(SerialPortEvent serialPortEvent)
     {
     }

     public Scanner getInputScanner()
     {
          return inputScanner;
     }

     public void setInputScanner(Scanner scanner)
     {
          inputScanner = scanner;
     }
}
