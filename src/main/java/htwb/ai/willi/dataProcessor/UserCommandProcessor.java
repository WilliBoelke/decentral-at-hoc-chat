package htwb.ai.willi.dataProcessor;

import htwb.ai.willi.SendService.Dispatcher;
import htwb.ai.willi.SendService.TransmissionCoordinator;
import htwb.ai.willi.controller.Address;
import htwb.ai.willi.controller.Controller;
import htwb.ai.willi.io.SerialInput;
import htwb.ai.willi.io.SerialOutput;
import htwb.ai.willi.io.UserInput;
import htwb.ai.willi.loraModule.ModuleManger.LoraModule;
import htwb.ai.willi.message.Acks.RouteReplyAck;
import htwb.ai.willi.message.RequestEncoderAndDecoder;
import htwb.ai.willi.message.RouteRequest;
import htwb.ai.willi.message.SendTextRequest;
import htwb.ai.willi.router.Router;
import htwb.ai.willi.router.SendTextRequestRouter;
import htwb.ai.willi.routing.BlackList;
import htwb.ai.willi.routing.RoutingTable;
import htwb.ai.willi.routing.SequenceNumberManager;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
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

     private boolean debug = true;


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
          String command = data.trim().toLowerCase();
          switch (command)
          {
               case "tab":
                    System.out.println(RoutingTable.getInstance().toString());
                    break;
               case "deb":
                    debug = !debug;
                    if (debug == false)
                    {
                         System.out.println(">>>Disable debug mode");
                    }
                    else
                    {
                         System.out.println(">>>Enable debug mode");
                    }
                    toggleDebug();
                    break;
               case "adr":
                    System.out.println(">>>This nodes Address is : " + Address.getInstance().getAddress());
                    break;
               case "seq":
                    System.out.println(">>>The current Sequence Number is : " + SequenceNumberManager.getInstance().getCurrentSequenceNumber());
                    break;
               case "bli":
                    System.out.println(BlackList.getInstance().toString());
                    break;
               case "pwr":
                    changeModulePower();
                    break;
               case "drp":
                    System.out.println(">>>Reset Routing Table");
                    RoutingTable.getInstance().dropRoutingTable();
                    break;
               case "cls":
                    final String ANSI_CLS = "\u001b[2J";
                    final String ANSI_HOME = "\u001b[H";
                    System.out.print(ANSI_CLS + ANSI_HOME);
                    System.out.flush();
                    break;
               default:
                    System.out.println(">>>unknown user command");
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


     private void changeModulePower()
     {
          Scanner scanner = new Scanner(System.in);
          String newPowerValue = "";
          do
          {
               System.out.println("Enter a power level between 1 and 20 . ");
               newPowerValue = scanner.nextLine();
          }
          while (!isValidPowerLevel(newPowerValue));
          LoraModule.getInstance().setPower(newPowerValue);
     }

     private boolean isValidPowerLevel(String newPowerValue)
     {
          if (Integer.parseInt(newPowerValue) <= 20 && Integer.parseInt(newPowerValue) > 0)
          {
               return true;
          }
          return false;
     }


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

     private void toggleDebug()
     {
          Level loggerLevel;

          if (debug)
          {
               loggerLevel = Level.ALL;
          }
          else
          {
               loggerLevel = Level.OFF;
          }

          Controller.LOG.setLevel(loggerLevel);
          Router.LOG.setLevel(loggerLevel);
          RouteRequest.LOG.setLevel(loggerLevel);
          RouteReplyAck.LOG.setLevel(loggerLevel);
          SendTextRequestRouter.LOG.setLevel(loggerLevel);
          RequestEncoderAndDecoder.LOG.setLevel(loggerLevel);
          RoutingTable.LOG.setLevel(loggerLevel);
          UserInput.LOG.setLevel(loggerLevel);
          SerialInput.LOG.setLevel(loggerLevel);
          SerialOutput.LOG.setLevel(loggerLevel);
          TransmissionCoordinator.LOG.setLevel(loggerLevel);
          Dispatcher.LOG.setLevel(loggerLevel);
          RoutingTable.LOG.setLevel(loggerLevel);
     }


}
