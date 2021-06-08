package htwb.ai.willi;

import htwb.ai.willi.io.SerialOutput;
import htwb.ai.willi.routing.RoutingTable;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Playground
{
     public static void main(String[] args)
     {
          byte eins = 5;
          byte zwei = 11;
          byte drei = 13;
          byte vier = 1;
          byte[] bytes = "Hello".getBytes();
          byte[] result = new byte[4 + bytes.length];

          result[0] = eins;
          result[1] = zwei;
          result[2] = drei;
          result[3] = vier;

          for (int i = 0; i < bytes.length; i++)
          {
               result[i+4] = bytes[i];
          }

          String ascii = new String(result) ;

          System.out.println("----- ascii, to send ------");
          System.out.println(ascii);


          byte[] decodeBytes = ascii.getBytes();

          byte decodedEins = decodeBytes[0];
          byte decodedZwei = decodeBytes[1];
          byte decodedDrei = decodeBytes[2];
          byte decodedView = decodeBytes[3];

          String decodedPayLoad;
          byte[] decodedPayloadBytes = new byte[decodeBytes.length - 4];

          for (int i = 4; i < decodeBytes.length; i++)
          {
               decodedPayloadBytes[i-4] = decodeBytes[i];
          }

          decodedPayLoad = new String(decodedPayloadBytes);

          System.out.println("----- ascii------");

          System.out.println(decodedEins + " " + decodedZwei +" "+ decodedDrei + " " + decodedView + " " + decodedPayLoad);
     }



}
