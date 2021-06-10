package htwb.ai.willi.message;

import java.nio.charset.StandardCharsets;

public class RequestEncoderAndDecoder
{


     public String encode(Request request)
     {
        return  request.encode();
     }


     public Request decode(String request)
     {
          switch (getEncodedMessageType(request))
          {
               case Request.ROUTE_REQUEST:
                    return RouteRequest.getInstanceFromEncodedString(request);
               case Request.ROUTE_REPLY:
                    return null;
               case Request.ROUTE_ERROR:
                    return null;
               case Request.ROUTE_ACK:
                    return null;
               case Request.SEND_TEXT_REQUEST:
                    return SendTextRequest.getInstanceFromEncodedString(request);
               case Request.HOP_ACK:
                    return null;
               case Request.SEND_TEXT_REQUEST_ACK:
                    return null;
          }
          return null;
     }

     private byte getEncodedMessageType(String encoded)
     {
          byte[] bytes = encoded.getBytes(StandardCharsets.US_ASCII);
          return bytes[0];
     }


}
