package htwb.ai.willi.message;

public class SendTextRequestAck extends Request
{
     private final byte type = SEND_TEXT_REQUEST_ACK;


     public SendTextRequestAck()
     {
     }


     public SendTextRequestAck(String encoded)
     {
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

     public byte getType()
     {
          return type;
     }
}
