package htwb.ai.willi.message;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class SendTextRequest extends Request
{
     private final byte type = 5;
     private byte originAddress = 13;
     private byte destinationAddress;
     private String message;
     private byte sequenceNumber;


     public SendTextRequest(byte destinationAddress, byte sequenceNumber, String message)
     {
          this.sequenceNumber = sequenceNumber;
          this.destinationAddress = destinationAddress;
          this.message = message;
     }

     public SendTextRequest(String encodedMessage)
     {
          this.setUpInstanceFromString(encodedMessage);
     }

     public static SendTextRequest getInstanceFromEncodedString(String encoded)
     {
          return new SendTextRequest(encoded);
     }

     private byte getDestinationAddress()
     {
          return this.destinationAddress;
     }

     public String getReadableMessage()
     {
          return originAddress + " >> " + message;
     }

     public String getEncodedMessage()
     {
          return getEncodedMessage();
     }


     public String encode()
     {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          //Message type
          byteArrayOutputStream.write(this.type);
          //origin address
          byteArrayOutputStream.write(this.originAddress);
          byteArrayOutputStream.write(this.destinationAddress);
          byteArrayOutputStream.write(this.sequenceNumber);
          String encodedHeader = byteArrayOutputStream.toString();
          String encodedMessage = new String(message.getBytes(StandardCharsets.US_ASCII));

          return encodedHeader + encodedMessage;
     }


     private String decode(String encoded)
     {
          ByteArrayInputStream byteArrayInputStream =
                  new ByteArrayInputStream(encoded.getBytes(StandardCharsets.US_ASCII));
          return new String(byteArrayInputStream.readAllBytes());
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
}
