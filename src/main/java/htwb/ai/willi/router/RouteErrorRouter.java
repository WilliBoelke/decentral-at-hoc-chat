package htwb.ai.willi.router;

import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RouteError;
import htwb.ai.willi.routing.RoutingTable;

import java.util.ArrayList;

public class RouteErrorRouter extends Router
{

     @Override
     protected void anyCase(Request request)
     {
          // Removing the Route to the unreachable address
          RouteError routeError = (RouteError) request;
          byte address = routeError.getUnreachableDestinationAddress();
          byte sequenceNumber = routeError.getUnreachableDestinationSequenceNumber();
          RoutingTable.getInstance().removeWithSequenceNumberCheck(address, sequenceNumber);

          ArrayList<Byte> addresses = routeError.getAdditionalAddresses();
          ArrayList<Byte> sequenceNumbers = routeError.getAdditionalSequenceNumber();

          for (int i = 0; i < addresses.size(); i++)
          {
               RoutingTable.getInstance().removeWithSequenceNumberCheck(addresses.get(i), sequenceNumbers.get(i));
          }
     }

     @Override
     protected void requestFromMe(Request request)
     {
          //not needed here
     }

     @Override
     protected void requestToForward(Request request)
     {

     }

     @Override
     protected void requestForMe(Request request)
     {

     }


     @Override
     protected void dispatchAck(Request request)
     {

     }
}
