package htwb.ai.willi.message;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class RouteAck extends Request
{

     private byte destinationAddress;


     public RouteAck()
     {
     }

     public RouteAck(String encoded)
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
          this.destinationAddress = bytes[2];
          this.setType(ROUTE_ACK);
     }

     @Override
     public String encode()
     {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          //Message type
          byteArrayOutputStream.write(this.getType());
          //destination address
          byteArrayOutputStream.write(this.destinationAddress);
          return byteArrayOutputStream.toString();
     }


     public byte getDestinationAddress()
     {
          return destinationAddress;
     }

     @Override
     public byte getOriginAddress()
     {
          return -1;
     }

     public void setDestinationAddress(byte destinationAddress)
     {
          this.destinationAddress = destinationAddress;
     }
}