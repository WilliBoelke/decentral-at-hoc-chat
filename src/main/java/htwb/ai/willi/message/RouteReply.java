package htwb.ai.willi.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class RouteReply extends Request
{

     private final byte type = ROUTE_REPLY;
     private byte originAddress = 13;
     private byte hopCount;
     private byte destinationAddress;
     private byte destinationSequenceNumber;
     private byte remainingLifeTime;


     public RouteReply(byte hopCount, byte destinationAddress, byte originSequenceNumber,
                       byte destinationSequenceNumber)
     {

     }


     public RouteReply(String encoded)
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
          this.hopCount = bytes[2];
          this.originAddress = bytes[3];
          this.destinationAddress = bytes[4];
          this.destinationSequenceNumber = bytes[5];
          this.remainingLifeTime = bytes[6];
     }

     @Override
     public String encode()
     {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          //Message type
          byteArrayOutputStream.write(this.type);
          //origin address
          byteArrayOutputStream.write(this.hopCount);
          byteArrayOutputStream.write(this.originAddress);
          byteArrayOutputStream.write(this.destinationAddress);
          byteArrayOutputStream.write(this.remainingLifeTime);
          return byteArrayOutputStream.toString();
     }

}
