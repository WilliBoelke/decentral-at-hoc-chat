package htwb.ai.willi;

import htwb.ai.willi.io.SerialOutput;
import htwb.ai.willi.routing.RoutingTable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Playground
{
     public static void main(String[] args)
     {
          String test = "LR.0011.4A.Hello Lora test 123";

          Pattern headerPattern = Pattern.compile("LR\\.[0-9]{4}\\.");
          Matcher headerMatcher = headerPattern.matcher(test);
          headerMatcher.find();
          String header = headerMatcher.group();

          System.out.printf("found header: " + header);
          Pattern addressPattern = Pattern.compile("[0-9]{4}");
          Matcher  addressMatcher= addressPattern.matcher(header);
          addressMatcher.find();
          String address = addressMatcher.group();
          System.out.printf("found address: " + address);
          SerialOutput.getInstance().sendString("Hello module " + address + ", i received a message from you");
          RoutingTable.getInstance().addAddress(address);
          SerialOutput.getInstance().sendString("Known Addresses : " +RoutingTable.getInstance().gteKnowDevices().toString());
     }
}
