package htwb.ai.willi.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class RouteAck extends Request
{
     private final byte type = ROUTE_ACK;
     private byte destinationAddress;


     public RouteAck()
     {
     }


     public RouteAck(String encoded)
     {
          this.setUpInstanceFromString(encoded);
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
          this.destinationAddress = bytes[2];
     }

     @Override
     public String encode()
     {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          //Message type
          byteArrayOutputStream.write(this.type);
          //destination address
          byteArrayOutputStream.write(this.destinationAddress);
          return byteArrayOutputStream.toString();
     }
}