package htwb.ai.willi.router;

import htwb.ai.willi.SendService.Dispatcher;
import htwb.ai.willi.SendService.Transmission;
import htwb.ai.willi.controller.Address;
import htwb.ai.willi.message.Acks.HopAck;
import htwb.ai.willi.message.Acks.SendTextRequestAck;
import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RouteRequest;
import htwb.ai.willi.message.SendTextRequest;
import htwb.ai.willi.routing.RoutingTable;
import htwb.ai.willi.routing.SequenceNumberManager;

import java.util.logging.Logger;

public class SendTextRequestRouter extends Router
{
     public static final Logger LOG = Logger.getLogger(SendTextRequestRouter.class.getName());


     @Override
     protected void dispatchAck(Request request)
     {
          HopAck ack = new HopAck();
          ack.setNextHopInRoute(request.getLastHopInRoute());
          ack.setMessageSequenceNumber(((SendTextRequest) request).getMessageSequenceNumber());
          Dispatcher.getInstance().dispatch(ack);
     }


     @Override
     protected void anyCase(Request request)
     {
          //not needed here
     }

     @Override
     protected void requestFromMe(Request request)
     {
          if (RoutingTable.getInstance().hasFittingRoute(request)) // a route to the destination is known
          {
               LOG.info("Found Route");
               RoutingTable.Route route = RoutingTable.getInstance().getRouteTo(request.getDestinationAddress());
               request.setNextHopInRoute(route.getNextInRoute());
               ((SendTextRequest) request).setOriginAddress(Address.getInstance().getAddress());
               ((SendTextRequest) request).setOriginAddress(Address.getInstance().getAddress());
               ((SendTextRequest) request).setForwarded(false);
               ((SendTextRequest) request).setMessageSequenceNumber(SequenceNumberManager.getInstance().getCurrentSequenceNumberAndIncrement());
               Transmission transmission = new Transmission(request);
               transmission.setHops(route.getHops());
               Dispatcher.getInstance().dispatchWithAck(transmission);
          }
          else  // no route to the destination is known
          {
               LOG.info("No matching route found");
               RouteRequest routeRequest = buildRequest((SendTextRequest) request);
               Transmission transmission = new Transmission(routeRequest);
               Dispatcher.getInstance().dispatchWithAck(transmission);
          }
     }

     @Override
     protected void requestToForward(Request request)
     {
          dispatchAck(request);
          if (RoutingTable.getInstance().hasFittingRoute(request))  // no route to the destination is known
          {
               RoutingTable.Route route = RoutingTable.getInstance().getRouteTo(request.getDestinationAddress());
               request.setNextHopInRoute(route.getNextInRoute());
               ((SendTextRequest) request).setForwarded(true);
               Transmission transmission = new Transmission(request);
               transmission.setHops(route.getHops());
               Dispatcher.getInstance().dispatchWithAck(transmission);
          }
          else
          {
               // Todo send Error
          }
     }

     @Override
     protected void requestForMe(Request request)
     {
          //Send HopAck
          HopAck ack = new HopAck();
          ack.setNextHopInRoute(request.getLastHopInRoute());
          Dispatcher.getInstance().dispatch(ack);

          // Send SendTextRequestAck
          SendTextRequestAck sendTextRequestAck = new SendTextRequestAck();
          sendTextRequestAck.setMessageSequenceNumber(((SendTextRequest) request).getMessageSequenceNumber());
          sendTextRequestAck.setDestinationAddress(request.getOriginAddress());
          sendTextRequestAck.setOriginAddress(Address.getInstance().getAddress());
          sendTextRequestAck.setNextHopInRoute(request.getOriginAddress());
          Dispatcher.getInstance().dispatch(sendTextRequestAck);

          // Output message
          SendTextRequest sendTextRequest = (SendTextRequest) request;
          System.out.println(sendTextRequest.getReadableMessage());
     }


     private RouteRequest buildRequest(SendTextRequest sendTextRequest)
     {
          RouteRequest routeRequest = new RouteRequest();

          Byte sequenceNum =
                  RoutingTable.getInstance().getNewesKnownSequenceNumberFromNode(sendTextRequest.getOriginAddress());
          if (RoutingTable.getInstance().getNewesKnownSequenceNumberFromNode(sendTextRequest.getDestinationAddress()) == -1)
          {
               routeRequest.setuFlag((byte) 1);
               routeRequest.setDestinationSequenceNumber((byte) 0);
          }
          else
          {
               routeRequest.setuFlag((byte) 1);
               routeRequest.setDestinationSequenceNumber(sequenceNum);
          }

          routeRequest.setBroadcastID((byte) 1);
          routeRequest.setOriginAddress(Address.getInstance().getAddress());
          routeRequest.setDestinationAddress(sendTextRequest.getDestinationAddress());
          routeRequest.setOriginSequenceNumber(SequenceNumberManager.getInstance().getCurrentSequenceNumberAndIncrement());
          routeRequest.setHopCount((byte) 0);
          sendTextRequest.setMessageSequenceNumber(SequenceNumberManager.getInstance().getCurrentSequenceNumberAndIncrement());
          routeRequest.setSendTextRequest(sendTextRequest);
          return routeRequest;
     }
}
