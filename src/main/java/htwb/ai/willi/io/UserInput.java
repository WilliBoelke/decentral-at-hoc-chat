package htwb.ai.willi.io;

import htwb.ai.willi.dataProcessor.UserCommandProcessor;
import htwb.ai.willi.message.SendTextRequest;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Scanner;
import java.util.logging.Logger;


/**
 * Reads user input asynchronous
 */
public class UserInput implements Runnable
{

     //--------------static variables--------------//

     public static final Logger LOG = Logger.getLogger(UserInput.class.getName());

     /**
      * instance following the singleton design pattern
      */
     private static UserInput userInput;


     //--------------instance variables--------------//

     /**
      * scanner to scan for user input
      */
     private final Scanner inputScanner;

     /**
      * Changes when the user send a message
      */
     private final PropertyChangeSupport changes;


     //--------------constructors and init--------------//

     /**
      * private constructor , following the singleton pattern
      */
     private UserInput()
     {
          // cant be initialized in static context
          this.changes = new PropertyChangeSupport(this);
          this.inputScanner = new Scanner(System.in);
     }

     /**
      * getInstance method following the Singleton pattern
      * returns a new instance of the userInput class
      * if it hasn't been initialize yet
      *
      * @return new Instance or the one saved in the userInput variable
      */
     public static UserInput getInstance()
     {
          if (userInput == null)
          {
               userInput = new UserInput();
          }
          return userInput;
     }


     //--------------public methods--------------//


     /**
      * Starts the UserInput thread and
      * reads new input from System.in in an asynchronous manner
      * notifies Listeners when a new event occurs (new user input)
      */
     @Override
     public void run()
     {
          while (inputScanner.hasNext())
          {
               String input = inputScanner.nextLine();
               if (input.equals("msg"))
               {
                    SendTextRequest request = UserCommandProcessor.getInstance().createSendTextRequest();
                    changes.firePropertyChange(new PropertyChangeEvent(this, "userInput", "", request));
               }
               changes.firePropertyChange(new PropertyChangeEvent(this, "userInput", "", input));
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
      * Closes scanner
      * and stops reading from the system in
      */
     public void close()
     {
          inputScanner.close();
     }

}
