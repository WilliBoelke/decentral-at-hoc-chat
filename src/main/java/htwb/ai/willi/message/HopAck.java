package htwb.ai.willi.message;

public class HopAck extends Request
{


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
