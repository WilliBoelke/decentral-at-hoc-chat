package htwb.ai.willi.loraModule.ModuleManger;

import htwb.ai.willi.controller.Address;
import htwb.ai.willi.controller.Constants;
import htwb.ai.willi.io.SerialInput;
import htwb.ai.willi.io.SerialOutput;
import purejavacomm.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Logger;

public class LoraModule implements PropertyChangeListener
{
     public static final Logger LOG = Logger.getLogger(LoraModule.class.getName());


     private static LoraModule instance;
     private String frequency;
     private String power;
     private String bandwidth;
     private String spreadingFactor;
     private String errorCorrectionCode;
     private String crc;
     private String implicitHeader;
     private String singleReceive;
     private String frequencyModulation;
     private String frequencyModulationPeriod;
     private String receiveTimeLimit;
     private String payloadLimit;
     private String preableLimit;


     private LoraModule()
     {
          this.frequency = "433000000";
          this.power = "20";
          this.bandwidth = "9";
          this.spreadingFactor = "10";
          this.errorCorrectionCode = "4";
          this.crc = "1";
          this.implicitHeader = "0";
          this.singleReceive = "0";
          this.frequencyModulation = "0";
          this.frequencyModulationPeriod = "0";
          this.receiveTimeLimit = "3000";
          this.payloadLimit = "8";
          this.preableLimit = "10";
     }

     public static LoraModule getInstance()
     {
          if (instance == null)
          {
               instance = new LoraModule();
          }
          return instance;
     }


     private String buildConfigString()
     {
          return "AT+CFG=" + frequency + "," + power + "," + bandwidth + "," + spreadingFactor + "," + errorCorrectionCode + "," + crc + "," + implicitHeader + "," + singleReceive + "," + frequencyModulation + "," + frequencyModulationPeriod + "," + receiveTimeLimit + "," + payloadLimit + "," + preableLimit;
     }

     public void configureModule()
     {
          this.resetGPIOPins();
          try
          {
               Thread.sleep(2000);
          }
          catch (InterruptedException e)
          {
               e.printStackTrace();
          }
          SerialOutput.getInstance().sendConfiguration("AT+RST");
          SerialOutput.getInstance().sendConfiguration(buildConfigString());
          SerialOutput.getInstance().sendConfiguration("AT+RX");
          SerialOutput.getInstance().sendConfiguration("AT+ADDR=" + Address.getInstance().getAddress());
          SerialOutput.getInstance().sendConfiguration("AT+SAVE");
     }

     public void resetGPIOPins()
     {
          Process process = null;
          try{
               process = Runtime.getRuntime().exec(new String[]{"reset"});
          }catch(Exception e) {
               System.out.println("Exception Raised" + e.toString());
          }
          InputStream stdout = process.getInputStream();
          BufferedReader reader = new BufferedReader(new InputStreamReader(stdout, StandardCharsets.UTF_8));
          String line;
          try{
               while((line = reader.readLine()) != null){
                    System.out.println("stdout: "+ line);
               }
          }catch(IOException e){
               System.out.println("Exception in reading output"+ e.toString());
          }
     }


     public void updateConfig()
     {
          SerialOutput.getInstance().sendConfiguration(buildConfigString());
     }


     public String getFrequency()
     {
          return frequency;
     }

     public void setFrequency(String frequency)
     {
          this.frequency = frequency;
          this.updateConfig();
     }

     public String getPower()
     {
          return power;
     }

     public void setPower(String power)
     {
          this.power = power;
          this.updateConfig();
     }

     public String getBandwidth()
     {
          return bandwidth;
     }

     public void setBandwidth(String bandwidth)
     {
          this.bandwidth = bandwidth;
          this.updateConfig();
     }

     public String getSpreadingFactor()
     {
          return spreadingFactor;
     }

     public void setSpreadingFactor(String spreadingFactor)
     {
          this.spreadingFactor = spreadingFactor;
          this.updateConfig();
     }

     public String getErrorCorrectionCode()
     {
          return errorCorrectionCode;
     }

     public void setErrorCorrectionCode(String errorCorrectionCode)
     {
          this.errorCorrectionCode = errorCorrectionCode;
          this.updateConfig();
     }

     public String getCrc()
     {
          return crc;
     }

     public void setCrc(String crc)
     {
          this.crc = crc;
          this.updateConfig();
     }

     public String getImplicitHeader()
     {
          return implicitHeader;
     }

     public void setImplicitHeader(String implicitHeader)
     {
          this.implicitHeader = implicitHeader;
          this.updateConfig();
     }

     public String getSingleReceive()
     {
          return singleReceive;
     }

     public void setSingleReceive(String singleReceive)
     {
          this.singleReceive = singleReceive;
          this.updateConfig();
     }

     public String getFrequencyModulation()
     {
          return frequencyModulation;
     }

     public void setFrequencyModulation(String frequencyModulation)
     {
          this.frequencyModulation = frequencyModulation;
          this.updateConfig();
     }

     public String getFrequencyModulationPeriod()
     {
          return frequencyModulationPeriod;
     }

     public void setFrequencyModulationPeriod(String frequencyModulationPeriod)
     {
          this.frequencyModulationPeriod = frequencyModulationPeriod;
          this.updateConfig();
     }

     public String getReceiveTimeLimit()
     {
          return receiveTimeLimit;
     }

     public void setReceiveTimeLimit(String receiveTimeLimit)
     {
          this.receiveTimeLimit = receiveTimeLimit;
          this.updateConfig();
     }

     public String getPayloadLimit()
     {
          return payloadLimit;
     }

     public void setPayloadLimit(String payloadLimit)
     {
          this.payloadLimit = payloadLimit;
          this.updateConfig();
     }

     public String getPreableLimit()
     {
          return preableLimit;
     }

     public void setPreableLimit(String preableLimit)
     {
          this.preableLimit = preableLimit;
          this.updateConfig();
     }

     @Override
     public void propertyChange(PropertyChangeEvent event)
     {
          if (event.getSource() instanceof SerialInput && event.getNewValue() instanceof String)
          {
               if (event.getPropertyName() == SerialInput.CPU_BUSY_EVENT)
               {
                    System.out.println("\n\n\n\nWARNING : RESETTING MODULE CPU_BUSY_ERROR\n\n\n\n");
                    this.resetGPIOPins();
               }
          }
     }

     public void configureSerialPort()
     {
          LOG.info("configureSerialPort: start configuration");

          try
          {
               SerialPort ser =
                       ((SerialPort) CommPortIdentifier.getPortIdentifier(Constants.PORT).open(this.getClass().getName(), 0));
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
          catch (UnsupportedCommOperationException e)
          {
               e.printStackTrace();
          }

     }
}
