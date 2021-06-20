package htwb.ai.willi.routing;

import htwb.ai.willi.controller.Address;
import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RouteReply;
import htwb.ai.willi.message.RouteRequest;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RoutingTable
{

     //--------------static variables--------------//

     /*
      * Logger for the Routing table
      */
     public static final Logger LOG = Logger.getLogger(RoutingTable.class.getName());
     /**
      * The instance following the singleton pattern
      */
     private static RoutingTable instance;


     //--------------instance variables--------------//

     /**
      * A list of {@link Route}
      */
     private final ArrayList<Route> routes;


     //--------------constructors and init--------------//


     /**
      * Private constructor (Singleton)
      */
     private RoutingTable()
     {
          routes = new ArrayList<>();
     }


     /**
      * getter for the instance (Singleton)
      *
      * @return A instance of the Routing table
      */
     public static RoutingTable getInstance()
     {
          if (instance == null)
          {
               instance = new RoutingTable();
          }

          return instance;
     }


     //--------------public methods--------------//

     /*
      * Adds a new route to the RouteList3
      */
     public void addRoute(Route route)
     {

          if (route.getDestinationAddress() != Address.getInstance().getAddress())
          {
               return;
          }
          for (Route r: routes)
          {
               if (r.equals(route))
               {
                    if(r.getDestinationSequenceNumber() > route.getDestinationSequenceNumber())
                    {
                         r.updateRoute(route);
                    }
                    else if(r.getDestinationSequenceNumber() == route.getDestinationSequenceNumber() && route.getHops() +1 < r.getHops())
                    {
                         r.updateRoute(route);
                    }
                    if(r.getDestinationSequenceNumber() == -1)
                    {
                         r.updateRoute(route);
                    }
                    if(r.getHops() == route.getHops() && r.getDestinationSequenceNumber() == r.getDestinationSequenceNumber() && r.getNextInRoute() == route.getNextInRoute())
                    {
                         // TTL reset
                         r.updateRoute(route);
                    }
                    return;
               }
          }
          addRoute(route);
          removeOldRouts();
     }

     /**
      * Generates a new Route based on a incoming {@link RouteReply}
      * or {@link RouteRequest} and adds it to the route list
      *
      * @param request
      *         A route Reply or RouteRequest
      */
     public void addRoute(Request request)
     {
          removeOldRouts();
          LOG.info("Add Route to " + request.getDestinationAddress());
          if (request instanceof RouteReply)
          {
               Route route = new RoutingTable.Route(request.getOriginAddress(), request.getLastHopInRoute(),
                       ((RouteReply) request).getHopCount(), ((RouteReply) request).getOriginSequenceNumber());
               addRoute(route);
          }
          else if (request instanceof RouteRequest)
          {
               LOG.info("Add Route from RouteRequest " + request.getDestinationAddress());
               Route route = new RoutingTable.Route(request.getOriginAddress(), request.getLastHopInRoute(),
                       ((RouteRequest) request).getHopCount(), ((RouteRequest) request).getOriginSequenceNumber());
               addRoute(route);
          }
     }

     /**
      * Returns the next hop / next nodes Address on the shortest (Hop Count)
      * route to destination address
      *
      * @param destinationAddress
      *
      * @return
      */
     public byte getNextInRouteTo(byte destinationAddress)
     {
          removeOldRouts();

          Route routeToDestination = getRouteTo(destinationAddress);
          if (routeToDestination != null)
          {
               return routeToDestination.getNextInRoute();
          }
          return -1;
     }

     /*
      * Returns the Route with the newest Sequence number an the smalled
      * hop count
      */
     public Route getRouteTo(byte destinationAddress)
     {
          removeOldRouts();
          Optional<Route> routeOptional =
                  routes.stream().filter(r -> r.getDestinationAddress() == (destinationAddress)).collect(Collectors.toList()).stream().max(Comparator.comparing(Route::getDestinationSequenceNumber)).stream().min(Comparator.comparing(Route::getHops));

          Route route;
          if (routeOptional.isPresent())
          {
               route = routeOptional.get();
               return route;
          }
          return null;
     }

     @Override
     public String toString()
     {
          removeOldRouts();

          String table =
                         "|----ROUTE REQUEST----------------------------------------------|\n"+
                          "| destination     | hops    | next hop   |  destination sequence      | \n" +
                           "|-----------------|---------|------------|----------------------------| \n";

          for (Route r : routes)
          {
               table = table + r.toString();
          }


          return table;
     }

     public boolean hasFittingRoute(Request request)
     {
          byte destination = request.getDestinationAddress();
          LOG.info("Searching route to" + destination);
          for (Route r : routes)
          {
               LOG.info("Comparing" + destination + " / " + r.getDestinationAddress());
               if (r.getDestinationAddress() == destination)
               {
                    return true;
               }
          }
          return false;
     }

     /**
      * Removes all routes with the given destination address
      * * @param destinationAddress
      */
     public void removeRoute(byte destinationAddress)
     {
          routes.removeIf(r -> r.getDestinationAddress() == (destinationAddress));
     }

     /**
      * Returns the newest known (highest) Sequence number of the
      * destination node, if no sequence number / route
      * is found returns -1
      *
      * @param destinationAddress
      *
      * @return
      */
     public byte getNewesKnownSequenceNumberFromNode(byte destinationAddress)
     {
          Optional<Route> routeOptional =
                  routes.stream().filter(r -> r.getDestinationAddress() == (destinationAddress)).collect(Collectors.toList()).stream().max(Comparator.comparing(Route::getDestinationSequenceNumber));

          if (routeOptional.isPresent())
          {
               return routeOptional.get().getDestinationSequenceNumber();
          }
          return -1;
     }

     /**
      * Removes all routes withe a remaining lifetime of
      * less then and equal to zero
      */
     private void removeOldRouts()
     {
          routes.removeIf(r -> r.getRemainingLifeTime() <= (0));
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
          public static final long LIFETIME = 180;


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

          /**
           * Timestamp of the routs creation
           */
          private long timeStamp;

          /**
           * List of Nodes which have send to this destination
           */
          private ArrayList<Byte> precursors;


          //--------------constructors and init--------------//

          public Route(byte destinationAddress, byte nextInRoute, byte hops, byte destinationSequenceNumber)
          {
               this.destinationSequenceNumber = destinationSequenceNumber;
               this.destinationAddress = destinationAddress;
               this.nextInRoute = nextInRoute;
               this.hops = hops;
               this.timeStamp = System.currentTimeMillis();
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

          public int getRemainingLifeTime()
          {
               long elapsedSeconds = ((System.currentTimeMillis() - timeStamp) / 1000);

               return (int) (LIFETIME - elapsedSeconds);
          }

          public void updatePrecursorList(byte address)
          {
               if(!this.precursors.contains(address))
               {
                    precursors.add(address);
               }
          }

          public byte getDestinationSequenceNumber()
          {
               return destinationSequenceNumber;
          }

          @Override
          public String toString()
          {
               StringBuilder sbuf = new StringBuilder();
               Formatter fmt = new Formatter(sbuf);
               fmt.format("|%17d|%9d|%12d|%28d| \n", this.destinationAddress, this.hops, this.nextInRoute,
                       this.destinationSequenceNumber);
               return sbuf.toString();
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
               Route route = (Route) o;
               return destinationAddress == route.destinationAddress;
          }

          @Override
          public int hashCode()
          {
               return Objects.hash(destinationAddress, destinationSequenceNumber, nextInRoute, hops, timeStamp, precursors);
          }

          public void updateRoute(Route route)
          {
               this.nextInRoute = route.nextInRoute;
               this.destinationSequenceNumber = route.destinationSequenceNumber;
               this.hops= route.hops;
               this.timeStamp =  System.currentTimeMillis();
          }
     }
}
