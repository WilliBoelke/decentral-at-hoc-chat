package htwb.ai.willi.message;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestEncoderAndDecoder
{


     public String encode(Request request)
     {
          return request.encode();
     }


     public Request decode(String encodedRequest)
     {
          Pattern headerPattern = Pattern.compile("LR\\,[0-9]{4}\\,");
          Matcher headerMatcher = headerPattern.matcher(encodedRequest);
          headerMatcher.find();
          String header = headerMatcher.group();

          Pattern addressPattern = Pattern.compile("[0-9]{4}");
          Matcher addressMatcher = addressPattern.matcher(header);
          addressMatcher.find();
          String address = addressMatcher.group();
          String requestBody = encodedRequest.replace(header, "");

          Request request = null;

          switch (getEncodedMessageType(requestBody))
          {
               case Request.ROUTE_REQUEST:
                    request = RouteRequest.getInstanceFromEncodedString(requestBody);
               case Request.ROUTE_REPLY:
                    request = RouteReply.getInstanceFromEncodedString(requestBody);
               case Request.ROUTE_ERROR:
                    request = RouteError.getInstanceFromEncodedString(requestBody);
               case Request.ROUTE_ACK:
                    request = RouteAck.getInstanceFromEncodedString(requestBody);
               case Request.SEND_TEXT_REQUEST:
                    request = SendTextRequest.getInstanceFromEncodedString(requestBody);
               case Request.HOP_ACK:
                    request = HopAck.getInstanceFromEncodedString(requestBody);
               case Request.SEND_TEXT_REQUEST_ACK:
                    request = SendTextRequestAck.getInstanceFromEncodedString(requestBody);
          }

          request.setLastHopInRoute(Byte.parseByte(address));
          return request;
     }

     private byte getEncodedMessageType(String encoded)
     {
          byte[] bytes = encoded.getBytes(StandardCharsets.US_ASCII);
          return bytes[0];
     }


}
