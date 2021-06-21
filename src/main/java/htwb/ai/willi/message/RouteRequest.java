package htwb.ai.willi.message;


import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class RouteRequest extends Request
{

     public static final Logger LOG = Logger.getLogger(RouteRequest.class.getName());

     //-------------instance variables--------------//
     /**
      * The address of the origin node
      */
     private byte originAddress;
     /**
      * The address of the destination Address
      */
     private byte destinationAddress;
     /**
      * Counts the hops which will be needed for this
      * Route
      * = 0 if sent from here
      * if its forwarded it will be incremented by 1
      */
     private byte hopCount;
     /**
      * The Sequence number of the origin node
      */
     private byte originSequenceNumber;
     /**
      * the sequence number of the destination node
      */
     private byte destinationSequenceNumber;

     private byte broadcastID;
     /*
      * 1 if the dest sequence number is unknown
      * else 0
      */
     private byte uFlag;

     /**
      * if a route for a SendTextRequest is unknown
      * then we need to send a RouteRequest first, a
      * <p>
      * in this case the SendTextRequest will be saved here
      * so it can be sen from the TransmissionCoordinator
      * after a matching RouteReply was received
      */
     private SendTextRequest sendTextRequest;


     //-------------constructors and init-------------//


     public RouteRequest(byte originAddress, byte destinationAddress, byte hopCount, byte broadcastID,
                         byte originSequenceNumber, byte destinationSequenceNumber, byte uFlag,
                         SendTextRequest sendTextRequest)
     {
          this.setType(ROUTE_REQUEST);
          this.setTimeout(ROUTE_REQUEST_TIMEOUT);
          this.originAddress = originAddress;
          this.destinationAddress = destinationAddress;
          this.hopCount = hopCount;
          this.broadcastID = broadcastID;
          this.originSequenceNumber = originSequenceNumber;
          this.destinationSequenceNumber = destinationSequenceNumber;
          this.uFlag = uFlag;
          this.sendTextRequest = sendTextRequest;
     }

     public RouteRequest()
     {
          this.setType(ROUTE_REQUEST);
     }


     public RouteRequest(String encoded)
     {
          this.setUpInstanceFromString(encoded);
     }

     public static RouteRequest getInstanceFromEncodedString(String encoded)
     {
          return new RouteRequest(encoded);
     }

     private void setUpInstanceFromString(String encoded)
     {
          this.setType(ROUTE_REQUEST);
          byte[] bytes = encoded.getBytes(StandardCharsets.US_ASCII);
          LOG.info("Bytes length: " + bytes.length);
          this.uFlag = bytes[1];
          this.hopCount = bytes[2];
          this.broadcastID = bytes[3];
          this.originAddress = bytes[4];
          this.originSequenceNumber = bytes[5];
          this.destinationAddress = bytes[6];
          this.destinationSequenceNumber = bytes[7];
     }

     @Override
     public String getAsReadable()
     {
           return   "\n\n|----ROUTE REQUEST--------------------------------------------------------|\n" +
                          "|     " + this.getType() + "    |    " + uFlag + "    |    " + hopCount + "    |    " + broadcastID + "    |    " + originAddress + "    |    " + originSequenceNumber +  "    |    " + destinationAddress +  "    |    " + destinationSequenceNumber +"\n"+
                         "|-------------------------------------------------------------------------|\n";

     }


     //----------------public methods-----------------//


     @Override
     public String encode()
     {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          //Message type
          byteArrayOutputStream.write(this.getType());
          //origin address
          byteArrayOutputStream.write(this.uFlag);
          byteArrayOutputStream.write(this.hopCount);
          byteArrayOutputStream.write(this.broadcastID);
          byteArrayOutputStream.write(this.originAddress);
          byteArrayOutputStream.write(this.originSequenceNumber);
          byteArrayOutputStream.write(this.destinationAddress);
          byteArrayOutputStream.write(this.destinationSequenceNumber);
          LOG.info("Encoded as : " + byteArrayOutputStream);
          LOG.info("Encoded len : " + byteArrayOutputStream.toString().length());
          return byteArrayOutputStream.toString();
     }


     //---------------getter and setter---------------//

     @Override
     public byte getDestinationAddress()
     {
          return destinationAddress;
     }

     @Override
     public byte getOriginAddress()
     {
          return originAddress;
     }

     public SendTextRequest getSendTextRequest()
     {
          return sendTextRequest;
     }

     public void setSendTextRequest(SendTextRequest sendTextRequest)
     {
          this.sendTextRequest = sendTextRequest;
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

     public void setDestinationAddress(byte destinationAddress)
     {
          this.destinationAddress = destinationAddress;
     }

     public byte getOriginSequenceNumber()
     {
          return originSequenceNumber;
     }

     public void setOriginSequenceNumber(byte originSequenceNumber)
     {
          this.originSequenceNumber = originSequenceNumber;
     }

     public byte getDestinationSequenceNumber()
     {
          return destinationSequenceNumber;
     }

     public void setDestinationSequenceNumber(byte destinationSequenceNumber)
     {
          this.destinationSequenceNumber = destinationSequenceNumber;
     }

     public byte getuFlag()
     {
          return uFlag;
     }

     public void setuFlag(byte uFlag)
     {
          this.uFlag = uFlag;
     }


     public byte getBroadcastID()
     {
          return broadcastID;
     }

     public void setBroadcastID(byte broadcastID)
     {
          this.broadcastID = broadcastID;
     }
}
