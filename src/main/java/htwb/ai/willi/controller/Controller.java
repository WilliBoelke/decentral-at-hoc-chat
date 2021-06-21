package htwb.ai.willi.controller;

import htwb.ai.willi.dataProcessor.UserCommandProcessor;
import htwb.ai.willi.io.SerialInput;
import htwb.ai.willi.io.UserInput;
import htwb.ai.willi.loraModule.ModuleManger.LoraModule;
import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RequestEncoderAndDecoder;
import htwb.ai.willi.message.SendTextRequest;
import htwb.ai.willi.router.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;


/**
 * Initializes all needed classes and instances
 * Is the central point for incoming data
 * redirects incoming data
 */
public class Controller implements PropertyChangeListener
{
     public static final Logger LOG = Logger.getLogger(Controller.class.getName());

     public Controller()
     {
     }


     /**
      * Starts the initialization and an endless loop to listen for new events
      *
      * @param address
      *         The nodes address, passed by the user as argument
      *         or set to a standard of 0013
      */
     public void start(String address)
     {
          Address.getInstance().setAddress(Byte.parseByte(address));
          System.out.println("====================================================");
          LoraModule.getInstance().configureSerialPort();
          LoraModule.getInstance().configureModule();
          UserInput.getInstance();
          new Thread(UserInput.getInstance()).start();
          SerialInput.getInstance().registerPropertyChangeListener(this);
          UserInput.getInstance().registerPropertyChangeListener(this);
          System.out.println("====================================================");
     }


     /**
      * Listening to all sorts of incoming events
      * such as
      * UserInput
      * SerialInput (f Ex. incoming messages)
      * and redirect them to process the accordingly
      * * @param event
      */
     @Override
     public void propertyChange(PropertyChangeEvent event)
     {
          Object changedData = event.getNewValue();

          // if User Input
          if (event.getSource() instanceof UserInput)
          {
               if (changedData instanceof String)
               {
                    LOG.info("propertyChange: received user command" + changedData);
                    UserCommandProcessor.getInstance().processData((String) changedData);
               }
               else if (changedData instanceof SendTextRequest)
               {
                    LOG.info("propertyChange: received message from user, sending to manager" + changedData);
                    SendTextRequestRouter sendTextRequestManager = new SendTextRequestRouter();
                    sendTextRequestManager.route((SendTextRequest) changedData);
               }
          }
          else if (event.getSource() instanceof SerialInput && changedData instanceof String && event.getPropertyName() == "request")
          {
               //Message from another node
               if (((String) changedData).contains("LR,"))
               {
                    LOG.info(">> received:  " + changedData);
                    try
                    {
                         RequestEncoderAndDecoder decoder = new RequestEncoderAndDecoder();
                         Request request = decoder.decode((String) changedData);
                         Router router;
                         switch (request.getType())
                         {
                              case Request.ROUTE_REQUEST:
                                   LOG.info("Received: Route Request");
                                   router = new RouteRequestRouter();
                                   router.route(request);
                                   break;
                              case Request.ROUTE_REPLY:
                                   LOG.info("Received: Route Reply");
                                   router = new RouteReplyRouter();
                                   router.route(request);
                                   break;
                              case Request.ROUTE_ERROR:
                                   LOG.info("Received: Route Error");
                                   router = new RouteErrorRouter();
                                   router.route(request);
                                   break;
                              case Request.ROUTE_ACK:
                                   LOG.info("Received: Route Reply Ack");
                                   router = new RouteAckRouter();
                                   router.route(request);
                                   break;
                              case Request.SEND_TEXT_REQUEST:
                                   LOG.info("Received: Send Text Request");
                                   router = new SendTextRequestRouter();
                                   router.route(request);
                                   break;
                              case Request.HOP_ACK:
                                   LOG.info("Received: Hop Ack");
                                   router = new HopAckRouter();
                                   router.route(request);
                                   break;
                              case Request.SEND_TEXT_REQUEST_ACK:
                                   LOG.info("Received: Send text Ack");
                                   router = new SendTextRequestAckRouter();
                                   router.route(request);
                                   break;
                         }
                    }
                    catch (IllegalStateException e)
                    {
                         LOG.info("No address for " + changedData + " found");
                    }
                    catch (IllegalArgumentException e)
                    {
                         LOG.info("Received an invalid message type");
                    }
                    catch (NullPointerException e)
                    {
                         LOG.info("A request was received incompletely and thus cant be decoded");
                    }
               }
          }
     }
}