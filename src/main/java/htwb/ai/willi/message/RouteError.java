package htwb.ai.willi.message;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class RouteError extends Request
{
     private byte destinationCount;
     private byte unreachableDestinationAddress;
     private byte unreachableDestinationSequenceNumber;
     private ArrayList<Byte> additionalAddresses;
     private ArrayList<Byte> additionalSequenceNumbers;


     public RouteError(ArrayList<Byte> additionalAddresses, byte destinationCount, byte unreachableDestinationAddress,
                       byte unreachableDestinationSequenceNumber, ArrayList<Byte> additionalSequenceNumbers)
     {
          this.setType(ROUTE_ERROR);
          this.additionalAddresses = additionalAddresses;
          this.additionalSequenceNumbers = additionalSequenceNumbers;
          this.destinationCount = destinationCount;
          this.unreachableDestinationAddress = unreachableDestinationAddress;
          this.unreachableDestinationSequenceNumber = unreachableDestinationSequenceNumber;
     }

     public RouteError()
     {

     }

     public RouteError(String encoded)
     {
          this.setUpInstanceFromString(encoded);
     }

     public static RouteError getInstanceFromEncodedString(String encoded)
     {
          return new RouteError(encoded);
     }

     private void setUpInstanceFromString(String encoded)
     {
          this.setType(ROUTE_ERROR);
          byte[] bytes = encoded.getBytes(StandardCharsets.US_ASCII);
          this.destinationCount = bytes[1];
          this.unreachableDestinationAddress = bytes[2];
          this.unreachableDestinationSequenceNumber = bytes[3];
          for (int i = 0; i < destinationCount; i++)
          {
               //Lets make both in one loop
               additionalAddresses.add(bytes[4+i]);
               additionalSequenceNumbers.add(bytes[4+i+destinationCount]);
          }
     }

     @Override
     public String encode()
     {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          byteArrayOutputStream.write(this.getType());
          byteArrayOutputStream.write(this.additionalAddresses.size());
          byteArrayOutputStream.write(this.unreachableDestinationAddress);
          byteArrayOutputStream.write(this.unreachableDestinationSequenceNumber);
          for (Byte a: additionalAddresses)
          {
               byteArrayOutputStream.write(a.byteValue());
          }
          for (Byte s: additionalSequenceNumbers)
          {
               byteArrayOutputStream.write(s.byteValue());
          }
          return byteArrayOutputStream.toString();
     }

     @Override
     public String getAsReadable()
     {
          return "\n\n|----ROUTE ERROR----------------------------------------------------------|\n"
                  + "| Ty: " + this.getType() + "  | Dc: " + destinationCount + "  | Ua: " + unreachableDestinationAddress + "  | Us: "
                  + unreachableDestinationSequenceNumber + "  | Aa:" + additionalAddresses.toString()
                  + "  | As: " + additionalSequenceNumbers.toString() + "\n"
                  + "|-------------------------------------------------------------------------|\n\n";

     }

     @Override
     public byte getDestinationAddress()
     {
          return -1;
     }

     @Override
     public byte getOriginAddress()
     {
          return -1;
     }

     public byte getDestinationCount()
     {
          return destinationCount;
     }

     public void setDestinationCount(byte destinationCount)
     {
          this.destinationCount = destinationCount;
     }

     public byte getUnreachableDestinationAddress()
     {
          return unreachableDestinationAddress;
     }

     public void setUnreachableDestinationAddress(byte unreachableDestinationAddress)
     {
          this.unreachableDestinationAddress = unreachableDestinationAddress;
     }

     public byte getUnreachableDestinationSequenceNumber()
     {
          return unreachableDestinationSequenceNumber;
     }

     public void setUnreachableDestinationSequenceNumber(byte unreachableDestinationSequenceNumber)
     {
          this.unreachableDestinationSequenceNumber = unreachableDestinationSequenceNumber;
     }

     public ArrayList<Byte> getAdditionalAddresses()
     {
          return this.additionalAddresses;
     }

     public void addAdditionalAddress(byte additionalAddress)
     {
          this.additionalAddresses.add(additionalAddress);
     }

     public ArrayList<Byte> getAdditionalSequenceNumber()
     {
          return this.additionalSequenceNumbers;
     }

     public void addAdditionalSequenceNumber(byte additionalSequenceNumber)
     {
          this.additionalSequenceNumbers.add( additionalSequenceNumber);
     }
}
