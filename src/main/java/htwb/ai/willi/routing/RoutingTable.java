package htwb.ai.willi.routing;

import htwb.ai.willi.controller.Controller;
import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RouteReply;
import htwb.ai.willi.message.RouteRequest;
import htwb.ai.willi.message.SendTextRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RoutingTable
{
     public static final Logger LOG = Logger.getLogger(RoutingTable.class.getName());
     private static RoutingTable instance;

     private final ArrayList<Route> routes;


     private RoutingTable()
     {
          routes = new ArrayList<>();
     }


     public static RoutingTable getInstance()
     {
          if (instance == null)
          {
               instance = new RoutingTable();
          }

          return instance;
     }

     public void addRoute(Route route)
     {
          routes.add(route);
     }

     public void addRoute(Request request)
     {
          if(request instanceof RouteReply)
          {
               Route route = new RoutingTable.Route(request.getOriginAddress(), request.getLastHopInRoute(), ((RouteReply) request).getHopCount(), (byte) -1);
               addRoute(route);
          }
          else if(request instanceof RouteRequest)
          {
               Route  route = new RoutingTable.Route(request.getOriginAddress(), request.getLastHopInRoute(), ((RouteRequest) request).getHopCount(), ((RouteRequest) request).getOriginSequenceNumber());
               addRoute(route);
          }
     }


     /**
      * Returns the next hop / next nodes Address on the shortest (Hop Count)
      * route to destination address
      *
      * @param destinationAddress
      * @return
      */
     public byte getNextInRouteTo(byte destinationAddress)
     {
          Optional<Route> routeOptional = routes.stream()
                  .filter(r -> r.getDestinationAddress() == (destinationAddress)).collect(Collectors.toList())
                  .stream().min(Comparator.comparing(Route::getHops));

          Route route;
          if(routeOptional.isPresent())
          {
               route = routeOptional.get();
               return route.getNextInRoute();
          }
          return -1;
     }


     @Override
     public String toString()
     {
          String table = "| destination       | hops     | next hop   |  destination sequence      | \n" +
                                   "|-----------------|---------|------------|----------------------------| \n" ;


          return table;
     }

     public boolean hasFittingRoute(Request request)
     {
          byte destination = request.getDestinationAddress();
          LOG.info("Searching route to" + destination);
          for (Route r: routes)
          {
               LOG.info("Comparing" + destination + " / " + r.getDestinationAddress() );
               if(r.getDestinationAddress() == destination)
               {
                    return true;
               }
          }
          return false;
     }

     public void removeRoute(byte destinationAddress)
     {
     }

     /**
      * The Route class
      * <p>
      * A route in our AODV implementation has a lifetime of 3 minutes
      * Also it contains the destination nodes Address,
      * the next node on the route and the all in all hop count.
      */
     public class Route implements Serializable
     {

          //--------------static variables--------------//

          /**
           * The lifetime in seconds
           */
          public static final int LIFETIME = 180;


          //--------------instance variables--------------//


          /**
           * address of the destination node
           */
          private byte destinationAddress;

          /**
           * The last known destination sequence number
           */
          private byte destinationSequenceNumber;

          /**
           * The address of the next node
           * on this route
           */
          private byte nextInRoute;

          /**
           * Hops needed for the routing on this route
           * ------------------
           * (i think it also wont be more then a byte / 256)
           * so lets stay with it
           */
          private byte hops;


          //--------------constructors and init--------------//

          public Route(byte destinationAddress, byte nextInRoute, byte hops, byte destinationSequenceNumber)
          {
               this.destinationSequenceNumber = destinationSequenceNumber;
               this.destinationAddress = destinationAddress;
               this.nextInRoute = nextInRoute;
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
