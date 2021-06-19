package htwb.ai.willi.message;

import htwb.ai.willi.controller.Controller;
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

          String address = "12";
          String requestBody = encodedRequest;

          Request request = null;

          switch (getEncodedMessageType(requestBody))
          {
               case Request.ROUTE_REQUEST:
                    LOG.info("Decoding Route Request");
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
          LOG.info("decodeding message type = " + bytes[0]);
          return bytes[0];
     }

}
