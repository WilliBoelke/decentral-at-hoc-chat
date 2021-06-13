package htwb.ai.willi.message;

public class SendTextRequestAck extends Request
{


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
