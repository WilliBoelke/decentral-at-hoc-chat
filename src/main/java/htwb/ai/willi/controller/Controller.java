package htwb.ai.willi.controller;

import htwb.ai.willi.io.SerialInput;
import htwb.ai.willi.io.SerialOutput;
import purejavacomm.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Logger;
/**
 * The main htwb.au.willi.controller of the application
 */
public class Controller  implements PropertyChangeListener
{
     public static final Logger logger = Logger.getLogger(Controller.class.getName());

     private String address;

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
          this.address = address;

          confiureSerialPort();
          configureLoraModule();
               logger.info("Software initialized. Listening for messages now.");

                    System.out.printf("Write");
                    SerialOutput.getInstance().sendString("Test");
                    System.out.printf("Wait");
     }


     /**
      * Sets the Lora module to its standard
      * settings.
      */
     private void configureLoraModule()
     {

          SerialOutput.getInstance().sendConfiguration("AT");
          SerialOutput.getInstance().sendConfiguration("AT");
          SerialOutput.getInstance().sendConfiguration(Constants.CONFIG);
          SerialOutput.getInstance().sendConfiguration("AT+ADDR=" + address);
          SerialOutput.getInstance().sendConfiguration("AT+DEST=" + Constants.BROADCAST_ADDRESS);
          SerialOutput.getInstance().sendConfiguration("AT+RX");
          SerialOutput.getInstance().sendConfiguration("AT+SAVE");

     }

     private void confiureSerialPort()
     {

          logger.info("Finished configuring serial port.");

          try
          {
               SerialPort  ser =  ((SerialPort) CommPortIdentifier.getPortIdentifier(Constants.PORT).open(this.getClass().getName(), 0));
               ser.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
               ser.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

               ser.disableReceiveTimeout();
               ser.disableReceiveFraming();
               ser.disableReceiveThreshold();

               logger.info("Serial name: " + ser.getName());
               SerialInput.getInstance().setInputScanner(new Scanner(ser.getInputStream()));
               SerialOutput.getInstance().setPrintWriter(new PrintWriter(ser.getOutputStream()));
               new Thread(SerialInput.getInstance()).start();

               SerialInput.getInstance().registerPropertyChangeListener(this);
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
          catch (UnsupportedCommOperationException unsupportedCommOperationException)
           {
          unsupportedCommOperationException.printStackTrace();
          }
     }

     @Override
     public void propertyChange(PropertyChangeEvent event)
     {
          System.out.printf(event.toString());
     }
}