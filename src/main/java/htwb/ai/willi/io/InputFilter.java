package htwb.ai.willi.io;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * This is a Singleton class
 * which makes it possible to filter out fast repeating incoming messages
 *
 */
public class InputFilter
{

     public static final Logger LOG = Logger.getLogger(InputFilter.class.getName());

     /**
      * List of Hashes from recently received messages
      */
     private ArrayList<MessageHash> hashes;

     /**
      * Instance
      */
     private static InputFilter instance;

     private InputFilter()
     {
          this.hashes = new ArrayList<>();
     }

     public static InputFilter getInstance()
     {
          if(instance == null)
          {
               instance = new InputFilter();
          }
          return instance;
     }

     public boolean wasRecentlyReceived(String msg)
     {
          String hash = getHash(msg);
          removeOldHashes();
          if(hashes.contains(new MessageHash(hash)))
          {
               LOG.info("message was received in the last 2 minutes, and will be ignored");
               return  true;
          }
          LOG.info("message was not received recently");
          hashes.add(new MessageHash(hash));
          return false;
     }

     /**
      * Calculates  a SHA-256 hash of a message string
      * @param msg
      * @return
      */
     private String getHash(String msg)
     {
          MessageDigest digest = null;
          try
          {
               digest = MessageDigest.getInstance("SHA-256");
          }
          catch (NoSuchAlgorithmException e)
          {
               e.printStackTrace();
          }
          byte[] encodedhash = digest.digest(msg.getBytes(StandardCharsets.UTF_8));

          StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
          for (int i = 0; i < encodedhash.length; i++) {
               String hex = Integer.toHexString(0xff & encodedhash[i]);
               if(hex.length() == 1)
               {
                    hexString.append('0');
               }
               hexString.append(hex);
          }
          return hexString.toString();
     }

     /**
      * Removes hashes with a negative TTL from the list
      */
     private void removeOldHashes()
     {
               hashes.removeIf(r -> r.getRemainingLifeTime() <= (0));
     }

     private class MessageHash
     {
          private final int TTL = 50;

          private String hash;

          private long timeStamp;

          public MessageHash(String hash)
          {
               this.hash = hash;
               timeStamp = System.currentTimeMillis();
          }

          public String getHash()
          {
               return hash;
          }

          public void setHash(String hash)
          {
               this.hash = hash;
          }

          public long getTimeStamp()
          {
               return timeStamp;
          }

          public void setTimeStamp(long timeStamp)
          {
               this.timeStamp = timeStamp;
          }

          public int getRemainingLifeTime()
          {
               long elapsedSeconds = ((System.currentTimeMillis() - timeStamp) / 1000);

               return (int) (TTL - elapsedSeconds);
          }

          @Override
          public boolean equals(Object o)
          {
               if (this == o)
               {
                    return true;
               }
               if (o == null || getClass() != o.getClass())
               {
                    return false;
               }
               MessageHash that = (MessageHash) o;
               return Objects.equals(hash, that.hash);
          }
     }
}
