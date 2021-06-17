package htwb.ai.willi.message.Acks;

import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RouteReply;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;


public class RouteReplyAck extends Request
{

     private byte destinationAddress;


     public RouteReplyAck()
     {
          this.setType(ROUTE_ACK);
     }

     public RouteReplyAck(String encoded)
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