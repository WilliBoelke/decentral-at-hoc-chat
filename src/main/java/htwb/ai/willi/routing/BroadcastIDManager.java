package htwb.ai.willi.routing;

public class BroadcastIDManager
{

     //--------------static variable--------------//

     /**
      * The current sequence number
      */
     private static byte currentBroadcastID;

     /**
      * instance following the singleton design pattern
      */
     private static BroadcastIDManager instance;


     //--------------constructors and init--------------//

     /**
      * Private constructor
      */
     private BroadcastIDManager()
     {
          currentBroadcastID = 0;
     }

     public static BroadcastIDManager getInstance()
     {
          if (instance == null)
          {
               instance = new BroadcastIDManager();
          }
          return instance;
     }

     //--------------public methods--------------//


     public byte getCurrentSequenceNumber()
     {
          return currentBroadcastID;
     }

     public byte getCurrentSequenceNumberAndIncrement()
     {

          if (currentBroadcastID == 127)
          {
               currentBroadcastID = 0;
          }
          else
          {
               currentBroadcastID++;
          }
          return currentBroadcastID;
     }

}
