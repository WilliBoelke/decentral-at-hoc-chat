package htwb.ai.willi.message.Acks;

import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RouteReply;

import java.util.logging.Logger;

public class HopAck extends Request
{

     public static final Logger LOG = Logger.getLogger(HopAck.class.getName());

     public byte messageSequenceNumber;

     public HopAck()
     {
          this.setType(HOP_ACK);
     }


     public static HopAck getInstanceFromEncodedString(String encoded, String address)
     {
          byte addressAsByte = Byte.parseByte(address.substring(2));
          LOG.info("HOP ADDRESS ====== " +addressAsByte);
          HopAck reply = new HopAck();
          reply.setLastHopInRoute(addressAsByte);
          return reply;
     }




     @Override
     public String encode()
     {
          return null;
     }

     @Override
     public String getAsReadable()
     {
          return  "" + this.getType() ;
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

     public byte getMessageSequenceNumber()
     {
          return messageSequenceNumber;
     }


     public void setMessageSequenceNumber(byte messageSequenceNumber)
     {
          this.messageSequenceNumber = messageSequenceNumber;
     }
}
