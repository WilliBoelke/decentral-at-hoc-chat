package htwb.ai.willi.message;

import htwb.ai.willi.message.Acks.HopAck;
import htwb.ai.willi.message.Acks.RouteReplyAck;
import htwb.ai.willi.message.Acks.SendTextRequestAck;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Request Decoder decodes an Arriving, encoded Request to
 * a instance of its class representation
 */
public class RequestEncoderAndDecoder
{

     //---------------public methods--------------//

     public String encode(Request request)
     {
          return request.encode();
     }


     public Request decode(String encodedRequest) throws IllegalArgumentException
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
                    request.setLastHopInRoute(Byte.parseByte(address));
                    return request;
               case Request.ROUTE_REPLY:
                    request = RouteReply.getInstanceFromEncodedString(requestBody);
                    request.setLastHopInRoute(Byte.parseByte(address));
                    return request;
               case Request.ROUTE_ERROR:
                    request = RouteError.getInstanceFromEncodedString(requestBody);
                    request.setLastHopInRoute(Byte.parseByte(address));
                    return request;
               case Request.ROUTE_ACK:
                    request = RouteReplyAck.getInstanceFromEncodedString(requestBody);
                    request.setLastHopInRoute(Byte.parseByte(address));
                    return request;
               case Request.SEND_TEXT_REQUEST:
                    request = SendTextRequest.getInstanceFromEncodedString(requestBody);
                    request.setLastHopInRoute(Byte.parseByte(address));
                    return request;
               case Request.HOP_ACK:
                    request = HopAck.getInstanceFromEncodedString(requestBody);
                    request.setLastHopInRoute(Byte.parseByte(address));
                    return request;
               case Request.SEND_TEXT_REQUEST_ACK:
                    request = SendTextRequestAck.getInstanceFromEncodedString(requestBody);
                    request.setLastHopInRoute(Byte.parseByte(address));
                    return request;
               default:
                    throw new IllegalArgumentException();
          }
     }

     private byte getEncodedMessageType(String encoded)
     {
          byte[] bytes = encoded.getBytes(StandardCharsets.US_ASCII);
          return bytes[0];
     }

}
