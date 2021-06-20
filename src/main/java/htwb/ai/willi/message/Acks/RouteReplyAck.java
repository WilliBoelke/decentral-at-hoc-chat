package htwb.ai.willi.message.Acks;

import htwb.ai.willi.message.Request;

import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;


public class RouteReplyAck extends Request
{

     public static final Logger LOG = Logger.getLogger(RouteReplyAck.class.getName());

     public RouteReplyAck()
     {
          this.setType(ROUTE_ACK);
     }

     public static RouteReplyAck getInstanceFromEncodedString(String encoded, String address)
     {
          byte addressAsByte = Byte.parseByte(address.substring(2));
          LOG.info("REPLY ADDRESS ====== " + addressAsByte);
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
          return "" + this.getType();
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