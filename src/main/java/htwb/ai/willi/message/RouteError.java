package htwb.ai.willi.message;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class RouteError extends Request
{
     private byte destinationCount;
     private byte unreachableDestinationAddress;
     private byte unreachableDestinationSequenceNumber;

     private byte additionalAddress;
     private byte additionalSequenceNumber;


     public RouteError(byte additionalAddress, byte destinationCount, byte unreachableDestinationAddress,
                       byte unreachableDestinationSequenceNumber, byte additionalSequenceNumber)
     {
          this.setType(ROUTE_ERROR);
          this.additionalAddress = additionalAddress;
          this.additionalSequenceNumber = additionalSequenceNumber;
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

     public static RouteReply getInstanceFromEncodedString(String encoded)
     {
          return new RouteReply(encoded);
     }


     private void setUpInstanceFromString(String encoded)
     {
          this.setType(ROUTE_ERROR);
          byte[] bytes = encoded.getBytes(StandardCharsets.US_ASCII);
          this.destinationCount = bytes[2];
          this.unreachableDestinationAddress = bytes[3];
          this.unreachableDestinationSequenceNumber = bytes[4];
          this.additionalAddress = bytes[5];
          this.additionalSequenceNumber = bytes[6];
     }

     @Override
     public String encode()
     {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          //Message type
          byteArrayOutputStream.write(this.getType());
          //origin address
          byteArrayOutputStream.write(this.destinationCount);
          byteArrayOutputStream.write(this.unreachableDestinationAddress);
          byteArrayOutputStream.write(this.unreachableDestinationSequenceNumber);
          byteArrayOutputStream.write(this.additionalAddress);
          byteArrayOutputStream.write(this.additionalSequenceNumber);
          return byteArrayOutputStream.toString();
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

     public byte getAdditionalAddress()
     {
          return additionalAddress;
     }

     public void setAdditionalAddress(byte additionalAddress)
     {
          this.additionalAddress = additionalAddress;
     }

     public byte getAdditionalSequenceNumber()
     {
          return additionalSequenceNumber;
     }

     public void setAdditionalSequenceNumber(byte additionalSequenceNumber)
     {
          this.additionalSequenceNumber = additionalSequenceNumber;
     }
}
