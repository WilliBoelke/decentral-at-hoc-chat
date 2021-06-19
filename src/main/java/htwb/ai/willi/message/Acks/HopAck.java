package htwb.ai.willi.message.Acks;

import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RouteReply;

public class HopAck extends Request
{

     public byte messageSequenceNumber;

     public HopAck()
     {
          this.setType(HOP_ACK);
     }


     public HopAck(String encoded)
     {
          this.setType(HOP_ACK);
          this.setUpInstanceFromString(encoded);
     }

     public static RouteReply getInstanceFromEncodedString(String encoded)
     {
          return new RouteReply(encoded);
     }


     private void setUpInstanceFromString(String encoded)
     {
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
