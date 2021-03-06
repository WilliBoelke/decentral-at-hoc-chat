package htwb.ai.willi.routing;

/*
* Singleton  to manage the Sequence number of this Specific node
 */
public class SequenceNumberManager
{

     //--------------static variable--------------//

     /**
      * The current sequence number
      */
     private static byte currentSequenceNumber;

     /**
      * instance following the singleton design pattern
      */
     private static SequenceNumberManager instance;


     //--------------constructors and init--------------//

     /**
      * Private constructor
      */
     private SequenceNumberManager()
     {
          currentSequenceNumber = 0;
     }

     public static SequenceNumberManager getInstance()
     {
          if (instance == null)
          {
               instance = new SequenceNumberManager();
          }
          return instance;
     }

     //--------------public methods--------------//

     public byte getCurrentSequenceNumber()
     {
          return currentSequenceNumber;
     }

     public byte getCurrentSequenceNumberAndIncrement()
     {
          currentSequenceNumber++;
          return currentSequenceNumber;
     }

}
