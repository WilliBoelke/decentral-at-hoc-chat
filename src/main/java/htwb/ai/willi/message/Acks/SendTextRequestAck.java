package htwb.ai.willi.message.Acks;

import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RouteReply;

public class SendTextRequestAck extends Request
{
     private byte originAddress;
     private byte destinationAddress;
     private byte messageSequenceNumber;

     public void setOriginAddress(byte originAddress)
     {
          this.originAddress = originAddress;
     }

     public void setDestinationAddress(byte destinationAddress)
     {
          this.destinationAddress = destinationAddress;
     }

     public byte getMessageSequenceNumber()
     {
          return messageSequenceNumber;
     }

     public void setMessageSequenceNumber(byte messageSequenceNumber)
     {
          this.messageSequenceNumber = messageSequenceNumber;
     }

     public SendTextRequestAck()
     {
          this.setType(SEND_TEXT_REQUEST_ACK);
     }


     public SendTextRequestAck(String encoded)
     {
          this.setType(SEND_TEXT_REQUEST_ACK);
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
     public String getAsReadable()
     {
          return  "" + this.getType() ;
     }


     @Override
     public String encode()
     {
          return null;
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
}
