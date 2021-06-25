package htwb.ai.willi.routing;

import htwb.ai.willi.io.InputFilter;
import htwb.ai.willi.message.RouteRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class BlackList
{
     private static BlackList instance;

     public ArrayList<BlacklistEntry> blackListedAddresses;

     private BlackList()
     {
          this.blackListedAddresses = new ArrayList<>();
     }

     public static BlackList getInstance()
     {
          if(instance == null)
          {
               instance = new BlackList();
          }
          return instance;
     }


     public void addNodeToBlackLis(byte address)
     {
          removeOldEntries();
          BlacklistEntry  newEntry = new BlacklistEntry(address);
          if(!blackListedAddresses.contains(newEntry))
          {
               blackListedAddresses.add(newEntry);
          }
     }

     @Override
     public String toString()
     {
          String response =  "\n\n|----BLACK LIST-----------------------------------------------------------|\n";
          for (BlacklistEntry e: blackListedAddresses)
          {
               response = response + e.toString();
          }
          response = response+  "\\n|-------------------------------------------------------------------------|\n\n";
          return response;

     }

     public boolean isBlackListed(byte address)
     {
          removeOldEntries();
          for (BlacklistEntry e: blackListedAddresses)
          {
               if(e.getAddress() == address)
               {
                    return true;
               }
          }
          return false;
     }

     private void removeOldEntries()
     {
          blackListedAddresses.removeIf(blacklistEntry -> blacklistEntry.getRemainingLifeTime() <= 0);
     }


     private class  BlacklistEntry
     {
          private final  byte TTL = (byte) 180;
          private byte address;
          private long timeStamp;

          public BlacklistEntry(byte address)
          {
               this.address = address;
               this.timeStamp = System.currentTimeMillis();
          }

          @Override
          public String toString()
          {
               return "|  Ad: " + address + "   |   ";
          }

          public int getRemainingLifeTime()
          {
               long elapsedSeconds = ((System.currentTimeMillis() - this.timeStamp) / 1000);
               return (int) (TTL - elapsedSeconds);
          }

          public byte getAddress()
          {
               return address;
          }

          public void setAddress(byte address)
          {
               this.address = address;
          }
     }

}
