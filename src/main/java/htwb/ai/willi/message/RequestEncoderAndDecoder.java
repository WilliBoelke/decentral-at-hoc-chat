package htwb.ai.willi.message;

import htwb.ai.willi.message.Acks.HopAck;
import htwb.ai.willi.message.Acks.RouteReplyAck;
import htwb.ai.willi.message.Acks.SendTextRequestAck;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Request Decoder decodes an Arriving, encoded Request to
 * a instance of its class representation
 */
public class RequestEncoderAndDecoder
{

     public static final Logger LOG = Logger.getLogger(RequestEncoderAndDecoder.class.getName());

     //---------------public methods--------------//

     public String encode(Request request)
     {
          return request.encode();
     }


     public Request decode(String encodedRequest) throws IllegalArgumentException
     {

          Pattern headerPattern = Pattern.compile("LR\\,[0-9]{4}\\,[0-9]{2}\\,");
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
                         LOG.info("Decoding Route Request");
                         request = RouteRequest.getInstanceFromEncodedString(requestBody);
                         request.setLastHopInRoute(Byte.parseByte(address));
                         return request;
                    case Request.ROUTE_REPLY:
                         LOG.info("Decoding Route Reply");
                         request = RouteReply.getInstanceFromEncodedString(requestBody);
                         request.setLastHopInRoute(Byte.parseByte(address));
                         return request;
                    case Request.ROUTE_ERROR:
                         LOG.info("Decoding Route Error");
                         request = RouteError.getInstanceFromEncodedString(requestBody);
                         request.setLastHopInRoute(Byte.parseByte(address));
                         return request;
                    case Request.ROUTE_ACK:
                         LOG.info("Decoding Route Error");
                         request = RouteReplyAck.getInstanceFromEncodedString(requestBody, address);
                         request.setLastHopInRoute(Byte.parseByte(address));
                         return request;
                    case Request.SEND_TEXT_REQUEST:
                         LOG.info("Decoding Send Text Requets");
                         request = SendTextRequest.getInstanceFromEncodedString(requestBody);
                         request.setLastHopInRoute(Byte.parseByte(address));
                         return request;
                    case Request.HOP_ACK:
                         LOG.info("Decoding Hop Ack");
                         request = HopAck.getInstanceFromEncodedString(requestBody, address);
                         request.setLastHopInRoute(Byte.parseByte(address));
                         return request;
                    case Request.SEND_TEXT_REQUEST_ACK:
                         LOG.info("Decoding Text Ack");
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
          LOG.info("decodeding message type = " + bytes[0]);
          return bytes[0];
     }

}
