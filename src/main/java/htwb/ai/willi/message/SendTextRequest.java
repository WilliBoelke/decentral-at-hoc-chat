package htwb.ai.willi.message;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class SendTextRequest extends Request
{
     private byte originAddress = 13;
     private byte destinationAddress;
     private String message;
     private byte sequenceNumber;


     public SendTextRequest(byte destinationAddress, byte sequenceNumber, String message)
     {
          this.setType(SEND_TEXT_REQUEST);
          this.sequenceNumber = sequenceNumber;
          this.destinationAddress = destinationAddress;
          this.message = message;
     }

     public SendTextRequest(String encodedMessage)
     {
          this.setType(SEND_TEXT_REQUEST);
          this.setUpInstanceFromString(encodedMessage);
     }

     public static SendTextRequest getInstanceFromEncodedString(String encoded)
     {
          return new SendTextRequest(encoded);
     }


     public String getReadableMessage()
     {
          return ">>" + originAddress + "  said >> " + message;
     }

     public String getEncodedMessage()
     {
          return getEncodedMessage();
     }


     public String encode()
     {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          //Message type
          byteArrayOutputStream.write(this.getType());
          //origin address
          byteArrayOutputStream.write(this.originAddress);
          byteArrayOutputStream.write(this.destinationAddress);
          byteArrayOutputStream.write(this.sequenceNumber);
          String encodedHeader = byteArrayOutputStream.toString();
          String encodedMessage = new String(message.getBytes(StandardCharsets.US_ASCII));

          return encodedHeader + encodedMessage;
     }

     private void setUpInstanceFromString(String encoded)
     {
          byte[] bytes = encoded.getBytes(StandardCharsets.US_ASCII);
          this.originAddress = bytes[1];
          this.destinationAddress = bytes[2];
          this.sequenceNumber = bytes[3];
          ByteArrayInputStream byteArrayInputStream =
                  new ByteArrayInputStream(encoded.getBytes(StandardCharsets.US_ASCII));
          byteArrayInputStream.skip(4);

          this.message = new String(byteArrayInputStream.readAllBytes());
     }

     @Override
     public byte getDestinationAddress()
     {
          return destinationAddress;
     }

     public byte getOriginAddress()
     {
          return originAddress;
     }

     public void setOriginAddress(byte originAddress)
     {
          this.originAddress = originAddress;
     }

     public void setDestinationAddress(byte destinationAddress)
     {
          this.destinationAddress = destinationAddress;
     }

     public String getMessage()
     {
          return message;
     }

     public void setMessage(String message)
     {
          this.message = message;
     }

     public byte getSequenceNumber()
     {
          return sequenceNumber;
     }

     public void setSequenceNumber(byte sequenceNumber)
     {
          this.sequenceNumber = sequenceNumber;
     }
}
