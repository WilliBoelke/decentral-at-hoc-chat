package htwb.ai.willi.message;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public abstract class Request
{
     public static final byte ROUTE_REQUEST = 1;
     public static final byte ROUTE_REPLY = 2;
     public static final byte ROUTE_ERROR = 3;
     public static final byte ROUTE_ACK = 4;
     public static final byte SEND_TEXT_REQUEST = 5;
     public static final byte HOP_ACK = 6;
     public static final byte SEND_TEXT_REQUEST_ACK = 7;

     private byte type;

     public byte getType()
     {
          return this.getType();
     }

     public abstract String encode();

     private String decode(String encoded)
     {
          ByteArrayInputStream byteArrayInputStream =
                  new ByteArrayInputStream(encoded.getBytes(StandardCharsets.US_ASCII));
          return new String(byteArrayInputStream.readAllBytes());
     }


}


