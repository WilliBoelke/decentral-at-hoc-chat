package htwb.ai.willi.message;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public abstract class Request implements Routable
{

     //--------------static variables--------------//

     public static final byte ROUTE_REQUEST = 1;
     public static final byte ROUTE_REPLY = 2;
     public static final byte ROUTE_ERROR = 3;
     public static final byte ROUTE_ACK = 4;
     public static final byte SEND_TEXT_REQUEST = 5;
     public static final byte HOP_ACK = 6;
     public static final byte SEND_TEXT_REQUEST_ACK = 7;


     //--------------instance variables--------------//

     private byte type;

     private byte nextHopInRoute;

     private byte lastHopInRoute;


     //---------------public methods--------------//

     public abstract String encode();

     private String decode(String encoded)
     {
          ByteArrayInputStream byteArrayInputStream =
                  new ByteArrayInputStream(encoded.getBytes(StandardCharsets.US_ASCII));
          return new String(byteArrayInputStream.readAllBytes());
     }


     //--------------getter and setter--------------//

     public byte getType()
     {
          return type;
     }

     public void setType(byte type)
     {
          this.type = type;
     }

     public byte getLastHopInRoute()
     {
          return lastHopInRoute;
     }

     public void setLastHopInRoute(byte lastHopInRoute)
     {
          this.lastHopInRoute = lastHopInRoute;
     }

     public byte getNextHopInRoute()
     {
          return nextHopInRoute;
     }

     public void setNextHopInRoute(byte nextHopInRoute)
     {
          this.nextHopInRoute = nextHopInRoute;
     }

     public abstract String getAsReadable();
}


