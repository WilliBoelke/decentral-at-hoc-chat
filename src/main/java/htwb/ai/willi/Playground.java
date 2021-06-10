package htwb.ai.willi;

import htwb.ai.willi.message.RequestEncoderAndDecoder;
import htwb.ai.willi.message.SendTextRequest;

public class Playground
{
     public static void main(String[] args)
     {
          byte eins = 5;
          byte zwei = 11;
          SendTextRequest request = new SendTextRequest(eins, zwei, "Hallo");

          RequestEncoderAndDecoder decoder = new RequestEncoderAndDecoder();

          String ascii = decoder.encode(request);
          System.out.println(ascii);


          SendTextRequest result = (SendTextRequest) decoder.decode(ascii);

          System.out.println(result.getClass().getSimpleName());
          System.out.println(result.getOriginAddress());
          System.out.println(result.getSequenceNumber());
          System.out.println(result.getReadableMessage());
     }


}
