package htwb.ai.willi.message;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class RouteError extends Request
{
     private final byte type = ROUTE_ERROR;
     private byte originAddress = 13;
     private byte destinationCount;
     private byte unreachableDestinationAddress;
     private byte unreachableDestinationSequenceNumber;
     private byte originSequenceNumber;


     public RouteError(byte hopCount, byte destinationAddress, byte originSequenceNumber,
                       byte destinationSequenceNumber)
     {

     }


     public RouteError(String encoded)
     {
          this.setUpInstanceFromString(encoded);
     }

     public static RouteReply getInstanceFromEncodedString(String encoded)
     {
          return new RouteReply(encoded);
     }



     private void setUpInstanceFromString(String encoded)
     {
          byte[] bytes = encoded.getBytes(StandardCharsets.US_ASCII);
          this.destinationCount = bytes[2];
          this.unreachableDestinationAddress = bytes[3];
          this.unreachableDestinationSequenceNumber = bytes[4];
          this.originAddress = bytes[5];
          this.originSequenceNumber = bytes[6];
     }

     @Override
     public String encode()
     {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          //Message type
          byteArrayOutputStream.write(this.type);
          //origin address
          byteArrayOutputStream.write(this.destinationCount);
          byteArrayOutputStream.write(this.unreachableDestinationAddress);
          byteArrayOutputStream.write(this.unreachableDestinationSequenceNumber);
          byteArrayOutputStream.write(this.originAddress);
          byteArrayOutputStream.write(this.originSequenceNumber);
          return byteArrayOutputStream.toString();
     }

     public byte getType()
     {
          return type;
     }

     @Override
     public byte getDestinationAddress()
     {
          return -1;
     }

     public byte getOriginAddress()
     {
          return originAddress;
     }

     public void setOriginAddress(byte originAddress)
     {
          this.originAddress = originAddress;
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

     public byte getOriginSequenceNumber()
     {
          return originSequenceNumber;
     }

     public void setOriginSequenceNumber(byte originSequenceNumber)
     {
          this.originSequenceNumber = originSequenceNumber;
     }
}
