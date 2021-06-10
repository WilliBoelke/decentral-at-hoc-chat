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

          System.out.println(decoder.encode(request));
     }


}
