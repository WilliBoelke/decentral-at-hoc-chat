package htwb.ai.willi.message.Acks;

import htwb.ai.willi.message.Request;

import java.io.ByteArrayOutputStream;
import java.util.Random;
import java.util.logging.Logger;

public class HopAck extends Request
{

     public static final Logger LOG = Logger.getLogger(HopAck.class.getName());

     public byte messageSequenceNumber;
     private byte originAddress;
     private byte destinationAddress;

     public HopAck()
     {
          Random r = new Random();

          this.setTimeout((int) (Math.random() * (ACK_TIMEOUT_MAX - ACK_TIMEOUT_MIN + 1) + ACK_TIMEOUT_MIN));
          this.setType(HOP_ACK);
     }

     public static HopAck getInstanceFromEncodedString(String encoded, String address)
     {
          byte addressAsByte = Byte.parseByte(address.substring(2));
          HopAck reply = new HopAck();
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
          return "\n\n|----HOP ACK--------------------------------------------------------|\n" + "| Ty: " + this.getType() + "  | Lh: " + this.getLastHopInRoute() + "  | Nh: " + this.getNextHopInRoute() + "\n" + "|-------------------------------------------------------------------------|\n\n";

     }

     @Override
     public byte getDestinationAddress()
     {
          return this.destinationAddress;
     }

     @Override
     public byte getOriginAddress()
     {
          return this.originAddress;
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
