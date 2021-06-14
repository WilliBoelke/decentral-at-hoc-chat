package htwb.ai.willi.router;

import htwb.ai.willi.SendService.Dispatcher;
import htwb.ai.willi.controller.Address;
import htwb.ai.willi.message.Acks.HopAck;
import htwb.ai.willi.message.Acks.RouteAck;
import htwb.ai.willi.message.Acks.SendTextRequestAck;
import htwb.ai.willi.message.Request;
import htwb.ai.willi.message.RouteReply;
import htwb.ai.willi.message.RouteRequest;
import htwb.ai.willi.message.SendTextRequest;
import htwb.ai.willi.routing.RoutingTable;


/**
 * A router instance takes a incoming or outgoing request
 * and decides what to do next wih it based on its parameters
 */
public abstract class Router
{


     public abstract void route(Request request);

     public abstract void requestFromMe(Request request);


     public void requestToForward(Request request)
     {
        //  RoutingTable.getInstance().addRoute(request);

          if (request instanceof SendTextRequest)
          {
               if (RoutingTable.getInstance().getNextInRouteTo(request.getDestinationAddress()) == -1)
               {
                    return;
               }
               Dispatcher.getInstance().dispatchWithAck(prepareForwardRequest(request));
          }
          if(request instanceof RouteRequest)
          {
               Dispatcher.getInstance().dispatchBroadcast(prepareForwardRequest(request));
          }
     }


     void requestForMe(Request request)
     {

     }

     protected boolean isRequestForMe(Request request)
     {
          return request.getDestinationAddress() == Address.getInstance().getAddress();
     }

     protected boolean isRequestFromMe(Request request)
     {
          return request.getOriginAddress() == Address.getInstance().getAddress();
     }

     protected boolean isRequestToForward(Request request)
     {
          if (request.getOriginAddress() != Address.getInstance().getAddress())
          {
               return true;
          }
          return false;
     }

     public static Request prepareForwardRequest(Request request)
     {
          byte nextHop = RoutingTable.getInstance().getNextInRouteTo(request.getDestinationAddress());

          if (request instanceof RouteReply)
          {
               RouteReply preparedRequest = (RouteReply) request;
               byte hopCount = ((RouteReply) request).getHopCount();
               hopCount++;
               preparedRequest.setHopCount(hopCount);
               if (nextHop != -1)
               {
                    (preparedRequest).setNextHopInRoute(nextHop);
               }
               return preparedRequest;
          }

          if (request instanceof SendTextRequest)
          {
               SendTextRequest preparedRequest = (SendTextRequest) request;
               if (nextHop != -1)
               {
                    preparedRequest.setNextHopInRoute(nextHop);
               }

               return preparedRequest;
          }

          if (request instanceof RouteRequest)
          {
               RouteRequest preparedRequest = (RouteRequest) request;
               byte hopCount = ((RouteRequest) request).getHopCount();
               hopCount++;
               preparedRequest.setHopCount(hopCount);
               if (nextHop != -1)
               {
                    preparedRequest.setNextHopInRoute(nextHop);
               }
               return preparedRequest;
          }

          return null;
     }

     protected void dispatchHopAck(SendTextRequest requestToAck)
     {
          HopAck ack = new HopAck();
          ack.setNextHopInRoute(requestToAck.getLastHopInRoute());
          ack.setMessageSequenceNumber(requestToAck.getMessageSequenceNumber());
          Dispatcher.getInstance().dispatch(ack);
     }

     protected void dispatchRouteReplyAc(byte destination)
     {
          RouteAck ack = new RouteAck();
          ack.setNextHopInRoute(destination);
          Dispatcher.getInstance().dispatch(ack);
     }

     protected void  dispatchSendTextAck(SendTextRequest requestToAck)
     {
          SendTextRequestAck ack = new SendTextRequestAck();
          ack.setNextHopInRoute(RoutingTable.getInstance().getNextInRouteTo(requestToAck.getOriginAddress()));
          ack.setOriginAddress(Address.getInstance().getAddress());
          ack.setDestinationAddress(requestToAck.getDestinationAddress());
          ack.setMessageSequenceNumber(requestToAck.getMessageSequenceNumber());
          Dispatcher.getInstance().dispatch(ack);
     }
}
