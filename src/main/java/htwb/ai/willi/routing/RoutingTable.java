package htwb.ai.willi.routing;

import java.io.Serializable;
import java.util.ArrayList;

public class RoutingTable
{

     private static RoutingTable instance;

     private ArrayList<Route> routes;


     private RoutingTable()
     {
          routes = new ArrayList<>();
     }


     public static RoutingTable getInstance()
     {
          if(instance == null)
          {
               instance = new RoutingTable();
          }

          return instance;
     }

     public void addRoute(Route route)
     {

     }

     /**
      * The Route class
      *
      * A route in our AODV implementation has a lifetime of 3 minutes
      * Also it contains the destination nodes Address,
      * the next node on the route and the all in all hop count.
      *
      */
     public  class Route implements Serializable
     {

          //--------------static variables--------------//

          /**
           *The lifetime in seconds
           */
          public static final  int LIFETIME = 180;


          //--------------instance variables--------------//


          /**
           * address of the destination node
           */
          private byte destinationAddress;

          /**
           * The address of the next node
           * on this route
           */
          private byte nextInRoute;

          /**
           * Hops needed for the routing on this route
           * ------------------
           *  (i think it also wont be more then a byte / 256)
           * so lets stay with it
           */
          private byte hops;



          //--------------constructors and init--------------//

          public Route(byte destinationAddress, byte nextInRoute, byte hops)
          {
               this.destinationAddress = destinationAddress;
               this.nextInRoute =  nextInRoute;
               this.hops = hops;
          }



          //--------------getter and setter--------------//


          public byte getDestinationAddress()
          {
               return destinationAddress;
          }

          public void setDestinationAddress(byte destinationAddress)
          {
               this.destinationAddress = destinationAddress;
          }

          public byte getNextInRoute()
          {
               return nextInRoute;
          }

          public void setNextInRoute(byte nextInRoute)
          {
               this.nextInRoute = nextInRoute;
          }

          public byte getHops()
          {
               return hops;
          }

          public void setHops(byte hops)
          {
               this.hops = hops;
          }
     }


     /**
      *

     // TODO to be deleted
     private ArrayList<String> knownDevices;

     private static RoutingTable instance;

     private RoutingTable()
     {
          knownDevices = new ArrayList<>();
     }

     public static RoutingTable getInstance()
     {
          if(instance == null)
          {
               instance = new RoutingTable();
          }
          return instance;
     }


     public ArrayList<String> getKnownDevices()
     {
          return knownDevices;
     }

     public boolean addAddress(String address)
     {
          if(!knownDevices.contains(address))
          {
               knownDevices.add(address);
               return true;
          }
          return false;
     }
      */


}
