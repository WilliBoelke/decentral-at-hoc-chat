package htwb.ai.willi;

import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RequestEncoderAndDecoder;
import htwb.ai.willi.message.RouteRequest;
import htwb.ai.willi.message.SendTextRequest;
import htwb.ai.willi.routing.SequenceNumberManager;

public class Playground
{
     public static void main(String[] args)
     {

          RouteRequest request = new RouteRequest();
          request.setuFlag((byte) 0);
          request.setHopCount((byte) 0);
          request.setOriginAddress((byte) 12);
          request.setDestinationAddress((byte) 13);
          request.setOriginSequenceNumber((byte) 123);
          request.setDestinationSequenceNumber((byte) 321);

          System.out.println(request.getAsReadable());

          String send = request.encode();
          System.out.println(send);

          RequestEncoderAndDecoder decoder = new RequestEncoderAndDecoder();

          Request req = decoder.decode(send);

          System.out.println(req.getAsReadable());

     }


}
