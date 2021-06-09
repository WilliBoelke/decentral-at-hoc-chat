package htwb.ai.willi.message;

public class RouteRequest
{
     private final byte type = 1;
     private byte originAddress = 13;
     private byte hopCount;
     private byte destinationAddress;
     private String message;
     private byte originSequenceNumber;
     private byte destinationSequenceNumber;

}
