package htwb.ai.willi.dataProcessor;

import htwb.ai.willi.message.SendTextRequest;
import htwb.ai.willi.routing.RoutingTable;
import htwb.ai.willi.routing.SequenceNumberManager;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

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

     private static UserCommandProcessor instance;

     private UserCommandProcessor()
     {

     }

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
          switch (data)
          {
               case "tab":
                    System.out.println(RoutingTable.getInstance().toString());
          }
     }


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
          while ((!isValidAddress(destinationAddress)));

          do
          {
               System.out.println("Enter a message: ");
               message = scanner.nextLine();
          }
          while (!isValidAddress(destinationAddress));


          return new SendTextRequest(Byte.parseByte(destinationAddress),
                  SequenceNumberManager.getInstance().getCurrentSequenceNumberAndIncrement(), message);
     }


     private boolean isValidAddress(String givenAddress)
     {
          try
          {
               Byte.parseByte(givenAddress);
               if (Byte.parseByte(givenAddress) != (byte) 13) // not own address
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
