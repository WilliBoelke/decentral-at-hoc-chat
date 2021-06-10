package htwb.ai.willi.message;

public class RouteError
{
     private final byte type = 3;
     private final byte originAddress = 13;
     private byte destinationCount;
     private byte unreachableDestinationAddress;
     private String message;
     private byte originSequenceNumber;
     private byte unreachableDestinationSequenceNumber;

}
