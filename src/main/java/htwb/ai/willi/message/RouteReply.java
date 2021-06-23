package htwb.ai.willi.message;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class RouteReply extends Request
{

     //--------------instance variable--------------//

     /**
      * The origin Node of this request
      */
     private byte originAddress;
     /**
      * The Hop count of already visited nodes
      * incremented by one when forwarded
      */
     private byte hopCount;
     /**
      * The Destination Nodes address
      */
     private byte destinationAddress;
     /**
      * The Sequence number of the origin node
      */
     private byte originSequenceNumber;
     /**
      * The last know sequence number of the
      * destination node
      */
     private byte destinationSequenceNumber;
     /**
      * the reining TTL
      */
     private byte remainingLifeTime;


     //--------------constructors and init--------------//


     public RouteReply(byte originAddress, byte hopCount, byte destinationAddress, byte originSequenceNumber,
                       byte destinationSequenceNumber, byte remainingLifeTime)
     {
          this.setType(ROUTE_REPLY);
          this.setTimeout(ROUTE_REPLY_TIMEOUT);
          this.originAddress = originAddress;
          this.hopCount = hopCount;
          this.destinationAddress = destinationAddress;
          this.originSequenceNumber = originSequenceNumber;
          this.destinationSequenceNumber = destinationSequenceNumber;
          this.remainingLifeTime = remainingLifeTime;
     }

     public RouteReply()
     {
          this.setType(ROUTE_REPLY);
     }

     public RouteReply(String encoded)
     {
          this.setUpInstanceFromString(encoded);
     }

     public static RouteReply getInstanceFromEncodedString(String encoded)
     {
          return new RouteReply(encoded);
     }

     private void setUpInstanceFromString(String encoded)
     {
          this.setType(ROUTE_REPLY);
          byte[] bytes = encoded.getBytes(StandardCharsets.US_ASCII);
          this.hopCount = bytes[1];
          this.destinationAddress = bytes[2];
          this.originAddress = bytes[3];
          this.destinationSequenceNumber = bytes[4];
          this.remainingLifeTime = bytes[5];
     }


     //--------------getter and setter--------------//


     @Override
     public String getAsReadable()
     {
          return "\n\n|----ROUTE REPLY----------------------------------------------------------|\n" + "| Ty: " + this.getType() + "    | Hc: " + hopCount + "    | Oa: " + originAddress + "    | Da: " + destinationAddress + "    | Ds: " + destinationSequenceNumber + "    | Lt: " + remainingLifeTime + "\n" + "|-------------------------------------------------------------------------|\n\n";
     }

     @Override
     public String encode()
     {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          byteArrayOutputStream.write(this.getType());
          byteArrayOutputStream.write(this.hopCount);
          byteArrayOutputStream.write(this.destinationAddress);
          byteArrayOutputStream.write(this.originAddress);
          byteArrayOutputStream.write(this.destinationSequenceNumber);
          byteArrayOutputStream.write(this.remainingLifeTime);
          return byteArrayOutputStream.toString();
     }


     public byte getOriginAddress()
     {
          return originAddress;
     }

     public void setOriginAddress(byte originAddress)
     {
          this.originAddress = originAddress;
     }

     public byte getHopCount()
     {
          return hopCount;
     }

     public void setHopCount(byte hopCount)
     {
          this.hopCount = hopCount;
     }

     public byte getDestinationAddress()
     {
          return destinationAddress;
     }

     public void setDestinationAddress(byte destinationAddress)
     {
          this.destinationAddress = destinationAddress;
     }

     public byte getDestinationSequenceNumber()
     {
          return destinationSequenceNumber;
     }

     public void setDestinationSequenceNumber(byte destinationSequenceNumber)
     {
          this.destinationSequenceNumber = destinationSequenceNumber;
     }

     public byte getRemainingLifeTime()
     {
          return remainingLifeTime;
     }

     public void setRemainingLifeTime(byte remainingLifeTime)
     {
          this.remainingLifeTime = remainingLifeTime;
     }

     public byte getOriginSequenceNumber()
     {
          return originSequenceNumber;
     }

     public void setOriginSequenceNumber(byte originSequenceNumber)
     {
          this.originSequenceNumber = originSequenceNumber;
     }
}
