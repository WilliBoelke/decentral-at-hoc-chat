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

     public static RouteReply getInstanceFromEncodedString(String encoded, String address)
     {
          byte addressAsByte = Byte.parseByte(address.substring(2));
          RouteReply reply = new RouteReply(encoded);
          reply.setOriginAddress(addressAsByte);
          return new RouteReply(encoded);
     }

     private void setUpInstanceFromString(String encoded)
     {
          this.setType(ROUTE_ACK);
          byte[] bytes = encoded.getBytes(StandardCharsets.US_ASCII);
          this.destinationAddress = bytes[2];

     }

     @Override
     public String encode()
     {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          //Message type
          byteArrayOutputStream.write(this.getType());
          return byteArrayOutputStream.toString();
     }

     @Override
     public String getAsReadable()
     {
          return  "" + this.getType() ;
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