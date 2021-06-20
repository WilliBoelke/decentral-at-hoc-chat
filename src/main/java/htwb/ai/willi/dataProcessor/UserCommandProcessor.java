package htwb.ai.willi.dataProcessor;

import htwb.ai.willi.controller.Address;
import htwb.ai.willi.message.SendTextRequest;
import htwb.ai.willi.routing.RoutingTable;
import htwb.ai.willi.routing.SequenceNumberManager;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Logger;


/**
 * Processes a user input
 * for example a  to send a new message
 * etc.
 * <p>
 * Will be called from the Controller propertyChange
 * method
 */
public class UserCommandProcessor
{
     public static final Logger LOG = Logger.getLogger(UserCommandProcessor.class.getName());

     private static UserCommandProcessor instance;


     //--------------constructors and init--------------//

     /**
      * Private constructor, singleton
      */
     private UserCommandProcessor()
     {
     }

     /**
      * Singleton getInstance method.
      * returns the saved instance or inits a new one
      *
      * @return A Comma
      */
     public static UserCommandProcessor getInstance()
     {
          if (instance == null)
          {
               instance = new UserCommandProcessor();
          }
          return instance;
     }


     public void processData(String data)
     {
          switch (data.trim().toLowerCase())
          {
               case "tab":
                    System.out.println(RoutingTable.getInstance().toString());
                    break;
               case "deb":
                    break;
               case "adr":
                    System.out.println("This nodes Address is : " + Address.getInstance().getAddress());
                    break;
               case "seq":
                    System.out.println("The current Sequence Number is : " + SequenceNumberManager.getInstance().getCurrentSequenceNumber());
                    break;
               default:
                    System.out.println("unknown user command");
          }
     }

     //--------------public methods--------------//


     public SendTextRequest createSendTextRequest()
     {
          Scanner scanner = new Scanner(System.in);
          String destinationAddress = "";
          String message = "";

          do
          {
               System.out.println("Enter the Destination Address ");
               destinationAddress = scanner.nextLine();
          }
          while (!isValidAddress(destinationAddress));

          do
          {
               System.out.println("Enter a message: ");
               message = scanner.nextLine();
          }
          while (!isValidMessage(message));

          SendTextRequest request = new SendTextRequest();
          request.setDestinationAddress(Byte.parseByte(destinationAddress));
          request.setOriginAddress(Address.getInstance().getAddress());
          request.setMessage(message);

          return request;
     }


     //----------------check input----------------//

     private boolean isValidAddress(String givenAddress)
     {
          try
          {
               Byte.parseByte(givenAddress);
               if (Byte.parseByte(givenAddress) != Address.getInstance().getAddress()) // not own address
               {
                    return true;
               }
          }
          catch (Exception e)
          {
               System.out.println("invalid Address");
               return false;
          }
          System.out.println("invalid Address");
          return false;
     }

     private boolean isValidMessage(String givenMessage)
     {
          if (givenMessage.getBytes(StandardCharsets.US_ASCII).length < 30)
          {
               return true;
          }

          System.out.println("Your message is to long");
          return false;
     }
}
