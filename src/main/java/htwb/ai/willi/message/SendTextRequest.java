package htwb.ai.willi.message;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * This Request type is for when a user wants to send a message to another node
 */
public class SendTextRequest extends Request
{

     //--------------instance variables---------------//

     /**
      * the message to be send
      */
     private String message;
     /**
      * The Sequence number of the message
      */
     private byte messageSequenceNumber;
     /**
      * The Address of the origin node
      */
     private byte originAddress;
     /**
      * The Address of the destination node
      */
     private byte destinationAddress;


     //-------------constructors and init-------------//


     public SendTextRequest(String message, byte messageSequenceNumber, byte originAddress, byte destinationAddress)
     {
          this.setType(SEND_TEXT_REQUEST);
          this.setTimeout(SEND_TEXT_TIMEOUT);
          this.message = message;
          this.messageSequenceNumber = messageSequenceNumber;
          this.originAddress = originAddress;
          this.destinationAddress = destinationAddress;
     }

     public SendTextRequest()
     {
          this.setType(SEND_TEXT_REQUEST);
     }

     public SendTextRequest(String encodedMessage)
     {
          this.setType(SEND_TEXT_REQUEST);
          this.setUpInstanceFromString(encodedMessage);
     }

     private void setUpInstanceFromString(String encoded)
     {
          byte[] bytes = encoded.getBytes(StandardCharsets.US_ASCII);
          this.originAddress = bytes[1];
          this.destinationAddress = bytes[2];
          this.messageSequenceNumber = bytes[3];
          ByteArrayInputStream byteArrayInputStream =
                  new ByteArrayInputStream(encoded.getBytes(StandardCharsets.US_ASCII));
          byteArrayInputStream.skip(4);

          this.message = new String(byteArrayInputStream.readAllBytes());
     }

     public static SendTextRequest getInstanceFromEncodedString(String encoded)
     {
          return new SendTextRequest(encoded);
     }


     //----------------public methods-----------------//

     public String encode()
     {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          //Message type
          byteArrayOutputStream.write(this.getType());
          //origin address
          byteArrayOutputStream.write(this.originAddress);
          byteArrayOutputStream.write(this.destinationAddress);
          byteArrayOutputStream.write(this.messageSequenceNumber);
          String encodedHeader = byteArrayOutputStream.toString();
          String encodedMessage = new String(message.getBytes(StandardCharsets.US_ASCII));

          return encodedHeader + encodedMessage;
     }


     //---------------getter and setter---------------//

     @Override
     public String getAsReadable()
     {
          return "\n\n|----SEND TEXT REQUEST----------------------------------------------------|\n" + "| Ty:" + this.getType() + "  | oA: " + originAddress + "  | dA: " + destinationAddress + "  | Sq: " + messageSequenceNumber + "  | Tx:" + message + " \n" + "|-------------------------------------------------------------------------|\n";

     }

     public String getReadableMessage()
     {
          return "\n\n|----" + getOriginAddress() + " WROTE " +
                  "YOU---------------------------------------------------------|\n" + "|     " + message.toUpperCase(Locale.ROOT) + " \n" + "|-------------------------------------------------------------------------|\n";
     }

     public String getEncodedMessage()
     {
          return getEncodedMessage();
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

     public byte getMessageSequenceNumber()
     {
          return messageSequenceNumber;
     }

     public void setMessageSequenceNumber(byte destinationSequenceNumber)
     {
          this.messageSequenceNumber = destinationSequenceNumber;
     }
}
