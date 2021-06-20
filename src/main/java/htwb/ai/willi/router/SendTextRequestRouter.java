package htwb.ai.willi.router;

import htwb.ai.willi.SendService.Dispatcher;
import htwb.ai.willi.controller.Address;
import htwb.ai.willi.message.Acks.HopAck;
import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RouteRequest;
import htwb.ai.willi.message.SendTextRequest;
import htwb.ai.willi.message.Acks.SendTextRequestAck;
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
          ack.setMessageSequenceNumber(((SendTextRequest)request).getMessageSequenceNumber());
          Dispatcher.getInstance().dispatch(ack);
     }


     @Override
     protected void anyCase(Request request)
     {
     }

     @Override
     protected void requestFromMe(Request request)
     {
          if (RoutingTable.getInstance().hasFittingRoute(request))
          {
               LOG.info("Found Route");
               RoutingTable.Route route = RoutingTable.getInstance().getRouteTo(request.getDestinationAddress());
               request.setNextHopInRoute(route.getNextInRoute());
               ((SendTextRequest) request).setOriginAddress(Address.getInstance().getAddress());
               ((SendTextRequest) request).setMessageSequenceNumber(SequenceNumberManager.getInstance().getCurrentSequenceNumberAndIncrement());
               Dispatcher.getInstance().dispatchWithAck(request);
          }
          else
          {
               LOG.info("No matching route found");
               RouteRequest routeRequest = buildRequest((SendTextRequest) request);
               Dispatcher.getInstance().dispatchWithAck(routeRequest);
          }
     }

     @Override
     protected void requestToForward(Request request)
     {
          dispatchAck(request);
          if(RoutingTable.getInstance().hasFittingRoute(request))
          {
               RoutingTable.Route route = RoutingTable.getInstance().getRouteTo(request.getDestinationAddress());
               request.setNextHopInRoute(route.getNextInRoute());
               Dispatcher.getInstance().dispatchWithAck(request);
          }
          else
          {
               // Todo send Error
          }
     }

     @Override
     protected void requestForMe(Request request)
     {
          SendTextRequestAck sendTextRequestAck = new SendTextRequestAck();
          sendTextRequestAck.setMessageSequenceNumber(((SendTextRequest)request).getMessageSequenceNumber());
          sendTextRequestAck.setDestinationAddress(request.getOriginAddress());
          sendTextRequestAck.setOriginAddress(Address.getInstance().getAddress());
          Dispatcher.getInstance().dispatch(sendTextRequestAck);
          // Output message
          SendTextRequest sendTextRequest = (SendTextRequest) request;
          LOG.info(sendTextRequest.getReadableMessage());
     }


     private RouteRequest buildRequest(SendTextRequest sendTextRequest)
     {
          RouteRequest routeRequest = new RouteRequest();

          Byte sequenceNum = RoutingTable.getInstance().getNewesKnownSequenceNumberFromNode(sendTextRequest.getOriginAddress());
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
          routeRequest.setOriginAddress(Address.getInstance().getAddress());
          routeRequest.setDestinationAddress(sendTextRequest.getDestinationAddress());
          routeRequest.setOriginSequenceNumber(SequenceNumberManager.getInstance().getCurrentSequenceNumberAndIncrement());
          routeRequest.setHopCount((byte) 0);
          sendTextRequest.setMessageSequenceNumber(SequenceNumberManager.getInstance().getCurrentSequenceNumberAndIncrement());
          routeRequest.setSendTextRequest(sendTextRequest);
          return routeRequest;
     }
}
