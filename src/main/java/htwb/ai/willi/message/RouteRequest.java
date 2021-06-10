package htwb.ai.willi.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class RouteRequest extends Request
{
     private final byte type = ROUTE_REQUEST;
     private byte originAddress = 13;
     private byte hopCount;
     private byte destinationAddress;
     private byte originSequenceNumber;
     private byte destinationSequenceNumber;
     private byte uFlag;


     public RouteRequest(byte hopCount, byte destinationAddress, byte originSequenceNumber,
                         byte destinationSequenceNumber)
     {

     }


     public RouteRequest(String encoded)
     {
          this.setUpInstanceFromString(encoded);
     }

     public static SendTextRequest getInstanceFromEncodedString(String encoded)
     {
          return new SendTextRequest(encoded);
     }



     private void setUpInstanceFromString(String encoded)
     {
          byte[] bytes = encoded.getBytes(StandardCharsets.US_ASCII);
          this.uFlag = bytes[1];
          this.hopCount = bytes[2];
          this.originAddress = bytes[3];
          this.originSequenceNumber = bytes[4];
          this.destinationAddress = bytes[5];
          this.destinationSequenceNumber = bytes[6];
     }

     @Override
     public String encode()
     {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          //Message type
          byteArrayOutputStream.write(this.type);
          //origin address
          byteArrayOutputStream.write(this.uFlag);
          byteArrayOutputStream.write(this.hopCount);
          byteArrayOutputStream.write(this.originAddress);
          byteArrayOutputStream.write(this.originSequenceNumber);
          byteArrayOutputStream.write(this.destinationAddress);
          byteArrayOutputStream.write(this.destinationSequenceNumber);
          return byteArrayOutputStream.toString();
     }
}
