package htwb.ai.willi.message.Acks;

import htwb.ai.willi.message.Request;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class SendTextRequestAck extends Request
{
     private byte originAddress;
     private byte destinationAddress;
     private byte messageSequenceNumber;

     public void setOriginAddress(byte originAddress)
     {
          this.originAddress = originAddress;
     }

     public void setDestinationAddress(byte destinationAddress)
     {
          this.destinationAddress = destinationAddress;
     }

     public byte getMessageSequenceNumber()
     {
          return messageSequenceNumber;
     }

     public void setMessageSequenceNumber(byte messageSequenceNumber)
     {
          this.messageSequenceNumber = messageSequenceNumber;
     }

     public SendTextRequestAck()
     {
          this.setTimeout((int) (Math.random() * (ACK_TIMEOUT_MAX - ACK_TIMEOUT_MIN + 1) + ACK_TIMEOUT_MIN));
          this.setType(SEND_TEXT_REQUEST_ACK);
     }


     public SendTextRequestAck(String encoded)
     {
          this.setType(SEND_TEXT_REQUEST_ACK);
          this.setUpInstanceFromString(encoded);
     }

     public static SendTextRequestAck getInstanceFromEncodedString(String encoded)
     {
          return new SendTextRequestAck(encoded);
     }


     private void setUpInstanceFromString(String encoded)
     {
          this.setType(SEND_TEXT_REQUEST_ACK);
          byte[] bytes = encoded.getBytes(StandardCharsets.US_ASCII);
          this.destinationAddress = bytes[1];
          this.originAddress = bytes[2];
          this.messageSequenceNumber = bytes[3];
     }

     @Override
     public String getAsReadable()
     {
          return "\n\n|----SEND TEXT ACK--------------------------------------------------------|\n" + "| Ty: " + this.getType() + "  | Oa: " + originAddress + "  | Da: " + destinationAddress + "  | Ms: " + messageSequenceNumber + "\n" + "|-------------------------------------------------------------------------|\n\n";
     }


     @Override
     public String encode()
     {
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          //Message type
          byteArrayOutputStream.write(this.getType());
          //origin address
          byteArrayOutputStream.write(this.destinationAddress);
          byteArrayOutputStream.write(this.originAddress);
          byteArrayOutputStream.write(this.messageSequenceNumber);
          return byteArrayOutputStream.toString();
     }


     @Override
     public byte getDestinationAddress()
     {
          return this.destinationAddress;
     }

     @Override
     public byte getOriginAddress()
     {
          return originAddress;
     }
}
