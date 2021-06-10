package htwb.ai.willi.message;

import java.io.ByteArrayInputStream;
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


     private String decode(String encoded)
     {
          ByteArrayInputStream byteArrayInputStream =
                  new ByteArrayInputStream(encoded.getBytes(StandardCharsets.US_ASCII));
          return new String(byteArrayInputStream.readAllBytes());
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


}
