package htwb.ai.willi;

import htwb.ai.willi.message.RequestEncoderAndDecoder;
import htwb.ai.willi.message.SendTextRequest;
import htwb.ai.willi.routing.SequenceNumberManager;

public class Playground
{
     public static void main(String[] args)
     {

          while (true)
          {
               System.out.println(SequenceNumberManager.getInstance().getCurrentSequenceNumberAndIncrement());
          }
     }


}
