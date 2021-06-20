package htwb.ai.willi.message.Acks;

import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RouteReply;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;


public class RouteReplyAck extends Request
{


     public RouteReplyAck()
     {
          this.setType(ROUTE_ACK);
     }

     public static RouteReplyAck getInstanceFromEncodedString(String encoded, String address)
     {
          byte addressAsByte = Byte.parseByte(address.substring(2));
          RouteReplyAck reply = new RouteReplyAck();
          reply.setLastHopInRoute(addressAsByte);
          return reply;
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

     @Override
     public byte getDestinationAddress()
     {
          return 0;
     }

     @Override
     public byte getOriginAddress()
     {
          return 0;
     }
}