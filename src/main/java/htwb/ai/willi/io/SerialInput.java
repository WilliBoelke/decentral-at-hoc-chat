package htwb.ai.willi.io;

import htwb.ai.willi.controller.Constants;
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
     //--------------static variables--------------//

     public static final Logger LOG = Logger.getLogger(SerialInput.class.getName());

     /**
      * SerialInput from LoRa-Module
      */
     private static SerialInput eventListener;

     //--------------instance variables--------------//

     public static final String CPU_BUSY_EVENT = "cpu_busy";
     public static final String OK_EVENT = "ok";
     public static final String SENDED_EVENT = "sended";
     public static final String SENDING_EVENT = "sending";

     /**
      * PropertyChangeSupport, updates with new received String
      */
     private final PropertyChangeSupport changes;

     /**
      * Scanner for SerialInput
      */
     private Scanner inputScanner;


     //--------------constructors and init--------------//

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


     //--------------public methods--------------//


     /**
      * Starts the SerialInput thread and
      * reads new input from it in an asynchronous manner
      * notifies Listeners when a new event occurs (new input)
      */
     @Override
     public void run()
     {
          inputScanner.useDelimiter(Constants.CARRIAGE_RETURN_LINE_FEED);
          while (true)
          {
               if (inputScanner.hasNext())
               {
                    try
                    {
                         Thread.sleep(250);
                    }
                    catch (InterruptedException e)
                    {
                         e.printStackTrace();
                    }
                    catch (IllegalMonitorStateException e)
                    {
                         e.printStackTrace();
                    }
                    String msg = inputScanner.next();
                    if (msg.contains("CPU_BUSY"))
                    {
                         changes.firePropertyChange(new PropertyChangeEvent(this, this.CPU_BUSY_EVENT, "", msg));
                    }
                    else if (msg.contains("OK"))
                    {
                         changes.firePropertyChange(new PropertyChangeEvent(this, this.OK_EVENT, "", msg));
                    }
                    else if (msg.contains("SENDED"))
                    {
                         changes.firePropertyChange(new PropertyChangeEvent(this, this.SENDED_EVENT, "", msg));
                    }
                    else if (msg.contains("SENDING"))
                    {
                         changes.firePropertyChange(new PropertyChangeEvent(this, this.SENDING_EVENT, "", msg));
                    }
                    else if (!InputFilter.getInstance().wasRecentlyReceived(msg))
                    {
                         changes.firePropertyChange(new PropertyChangeEvent(this, "request", "", msg));
                    }
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
