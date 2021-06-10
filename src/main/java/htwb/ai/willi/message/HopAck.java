package htwb.ai.willi.message;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class HopAck extends Request
{

     private final byte type = HOP_ACK;

     public HopAck()
     {
     }


     public HopAck(String encoded)
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

}
