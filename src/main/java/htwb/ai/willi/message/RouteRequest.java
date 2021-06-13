package htwb.ai.willi.message;


import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class  RouteRequest extends Request
{
     private byte originAddress = 13;
     private byte hopCount;
     private byte destinationAddress;
     private byte originSequenceNumber;
     private byte destinationSequenceNumber;
     private byte uFlag;
     private SendTextRequest sendTextRequest;

     public RouteRequest(byte hopCount, byte destinationAddress, byte originSequenceNumber)
     {
          this.hopCount = hopCount;
          this.destinationAddress = destinationAddress;
          this.originSequenceNumber = originSequenceNumber;
          this.destinationSequenceNumber = destinationSequenceNumber;
     }


     public RouteRequest(String encoded)
     {
          this.setUpInstanceFromString(encoded);
     }

     public static SendTextRequest getInstanceFromEncodedString(String encoded)
     {
          return new SendTextRequest(encoded);
     }



     private void setUpInstanceFromString(String encoded)
     {
          this.setType(ROUTE_REQUEST);
          byte[] bytes = encoded.getBytes(StandardCharsets.US_ASCII);
          this.uFlag = bytes[1];
          this.hopCount = bytes[2];
          this.originAddress = bytes[3];
          this.originSequenceNumber = bytes[4];
          this.destinationAddress = bytes[5];
          this.destinationSequenceNumber = bytes[6];
     }

     @Override
     public String encode()
     {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          //Message type
          byteArrayOutputStream.write(this.getType());
          //origin address
          byteArrayOutputStream.write(this.uFlag);
          byteArrayOutputStream.write(this.hopCount);
          byteArrayOutputStream.write(this.originAddress);
          byteArrayOutputStream.write(this.originSequenceNumber);
          byteArrayOutputStream.write(this.destinationAddress);
          byteArrayOutputStream.write(this.destinationSequenceNumber);
          return byteArrayOutputStream.toString();
     }

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
}
