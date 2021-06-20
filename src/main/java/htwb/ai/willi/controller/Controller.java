package htwb.ai.willi.controller;

import htwb.ai.willi.dataProcessor.UserCommandProcessor;
import htwb.ai.willi.io.Ping;
import htwb.ai.willi.io.SerialInput;
import htwb.ai.willi.io.SerialOutput;
import htwb.ai.willi.io.UserInput;
import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RequestEncoderAndDecoder;
import htwb.ai.willi.message.SendTextRequest;
import htwb.ai.willi.router.*;
import purejavacomm.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
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
          LOG.info("====================================================");
          configureSerialPort();
          configureLoraModule();
          //startPing();
          LOG.info("====================================================");
     }

     private void startPing()
     {
          Ping ping = new Ping(240000, "ping");
          new Thread(ping).start();
     }


     /**
      * Sets the Lora module to its standard
      * settings.
      */
     private void configureLoraModule()
     {

          LOG.info("configureLoraModule: start configuration");
          LOG.info("configureLoraModule: reset module");
          SerialOutput.getInstance().sendConfiguration("AT+RST");
          LOG.info("configureLoraModule: send config string");
          SerialOutput.getInstance().sendConfiguration(Constants.CONFIG);
          LOG.info("configureLoraModule: rx");
          SerialOutput.getInstance().sendConfiguration("AT+RX");
          LOG.info("configureLoraModule: send address");
          SerialOutput.getInstance().sendConfiguration("AT+ADDR=" + Address.getInstance().getAddress());
          SerialOutput.getInstance().sendConfiguration("AT+SAVE");
          //SerialOutput.getInstance().sendConfiguration("AT+DEST=" + Constants.BROADCAST_ADDRESS);
     }

     private void configureSerialPort()
     {

          LOG.info("configureSerialPort: start configuration");

          try
          {
               SerialPort ser = ((SerialPort) CommPortIdentifier.getPortIdentifier(Constants.PORT).open(this.getClass().getName(), 0));
               ser.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
               ser.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
               ser.disableReceiveTimeout();
               ser.disableReceiveFraming();
               ser.disableReceiveThreshold();

               LOG.info("configureSerialPort: serial port name: " + ser.getName());
               LOG.info("configureSerialPort: setup serial in- and output");
               SerialInput.getInstance().setInputScanner(new Scanner(ser.getInputStream()));
               SerialOutput.getInstance().setPrintWriter(new PrintWriter(ser.getOutputStream()));
               new Thread(SerialInput.getInstance()).start();
               //That doesnt belong here
               SerialInput.getInstance().registerPropertyChangeListener(this);
               UserInput.getInstance();
               new Thread(UserInput.getInstance()).start();
               UserInput.getInstance().registerPropertyChangeListener(this);

          }
          catch (PortInUseException e)
          {
               e.printStackTrace();
          }
          catch (NoSuchPortException e)
          {
               e.printStackTrace();
          }
          catch (IOException e)
          {
               e.printStackTrace();
          }
          catch (UnsupportedCommOperationException e)
          {
               e.printStackTrace();
          }
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
          else if (event.getSource() instanceof SerialInput && changedData instanceof String)
          {
               //Message from another node
               if (((String) changedData).contains("LR,"))
               {
                    LOG.info(">> received:  " + changedData);
                    try
                    {
                         RequestEncoderAndDecoder decoder = new RequestEncoderAndDecoder();
                         Request request = decoder.decode((String) changedData);
                         LOG.info("Message type = " + request.getType());
                         LOG.info("Message   = " + request.getAsReadable());
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