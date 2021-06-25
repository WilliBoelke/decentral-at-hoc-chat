package htwb.ai.willi.SendService;

import htwb.ai.willi.controller.Address;
import htwb.ai.willi.io.SerialOutput;
import htwb.ai.willi.message.Acks.HopAck;
import htwb.ai.willi.message.Acks.RouteReplyAck;
import htwb.ai.willi.message.Acks.SendTextRequestAck;
import htwb.ai.willi.message.*;
import htwb.ai.willi.routing.BlackList;
import htwb.ai.willi.routing.BroadcastIDManager;
import htwb.ai.willi.routing.RoutingTable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.logging.Logger;


public class TransmissionCoordinator implements PropertyChangeListener, Runnable
{
     public static final Logger LOG = Logger.getLogger(TransmissionCoordinator.class.getName());

     private final Transmission transmission;

     private boolean finished;
     /**
      * indicates if a HopAck was received
      */
     private boolean receivedHopHack;

     public TransmissionCoordinator(Transmission transmission)
     {
          this.finished = false;
          this.receivedHopHack = false;
          this.transmission = transmission;
     }


     @Override
     public void run()
     {
          for (int i = 0; i < Transmission.STD_RETRIES; i++)
          {
               if (!finished)
               {
                    LOG.info("Trying to send, try number : " + i);
                    if (transmission.getRequest() instanceof RouteRequest)
                    {
                         ((RouteRequest) transmission.getRequest()).setBroadcastID(BroadcastIDManager.getInstance().getCurrentSequenceNumberAndIncrement());
                         SerialOutput.getInstance().broadcast(transmission.getRequest());
                         waitForAck();
                    }
                    else
                    {
                         SerialOutput.getInstance().sendRequest(transmission.getRequest());
                    }
                    waitForAck();
               }
          }
          if (!finished)
          {
               LOG.info("Transmission failed, out of retries");
               onTransmissionFailed();
          }
     }


     public void waitForAck()
     {
          int times = transmission.getHops();
          LOG.info("Waiting for answer");
          for (int i = 0; i < times; i++)
          {
               try
               {
                    Thread.sleep(transmission.getRequest().getTimeout() * 1000);
               }
               catch (InterruptedException e)
               {
                    e.printStackTrace();
               }
          }
          LOG.info("Finished waiting");
     }


     private void onRouteRequestSuccess()
     {
          if (transmission.getRequest() instanceof RouteRequest)
          {
               LOG.info("Received reply for Route Request, your message will now be send ");
               RouteRequest routeRequest = (RouteRequest) transmission.getRequest();
               if (routeRequest.getSendTextRequest() != null)
               {
                    SendTextRequest sendTextRequest = routeRequest.getSendTextRequest();
                    //Setting the next hop
                    sendTextRequest.setNextHopInRoute(RoutingTable.getInstance().getNextInRouteTo(sendTextRequest.getDestinationAddress()));
                    Transmission transmission = new Transmission(routeRequest.getSendTextRequest());
                    transmission.setHops(RoutingTable.getInstance().getRouteTo(sendTextRequest.getDestinationAddress()).getHops());
                    Dispatcher.getInstance().dispatchWithAck(transmission);
               }
          }
     }


     private void onTransmissionFailed()
     {
          LOG.info("The message was send unsuccessfully" + Transmission.STD_RETRIES + " times. Consider a different " + "destination address");
          RoutingTable.getInstance().removeRoute(transmission.getRequest().getDestinationAddress());
          Dispatcher.getInstance().unregisterPropertyChangeListener(this);
          blockList();
          sendErrorRequest();
     }


     private void blockList()
     {
          if(this.transmission.getRequest() instanceof  SendTextRequest)
          {
               if(receivedHopHack == false)
               {
                    BlackList.getInstance().addNodeToBlackLis(transmission.getRequest().getNextHopInRoute());
               }
          }
     }
     /**
      * Send a RouteError if the transmission wasn't successful
      */
     private void sendErrorRequest()
     {
          if(this.transmission.getRequest() instanceof SendTextRequest && receivedHopHack == false )
          {
               LOG.info("Send link break error to other nodes, unreachable : " + this.transmission.getRequest().getNextHopInRoute());
               try
               {
                    RoutingTable.Route failedRoute = RoutingTable.getInstance().getRouteTo(this.transmission.getRequest().getDestinationAddress());
                    ArrayList<Byte> precursorAddresses = RoutingTable.getInstance().getRouteTo(this.transmission.getRequest().getDestinationAddress()).getPrecursors();
                    ArrayList<RoutingTable.Route> additionalRoutes = RoutingTable.getInstance().getRoutesWithNextHop(this.transmission.getRequest().getNextHopInRoute());

                    RouteError routeError = new RouteError();
                    routeError.setUnreachableDestinationAddress(this.transmission.getRequest().getNextHopInRoute());
                    routeError.setUnreachableDestinationSequenceNumber(failedRoute.getDestinationSequenceNumber());
                    routeError.setDestinationCount((byte) additionalRoutes.size());
                    Dispatcher.getInstance().dispatchBroadcast(routeError);
                    for (RoutingTable.Route r: additionalRoutes)
                    {
                         routeError.addAdditionalAddress(r.getDestinationAddress());
                         routeError.addAdditionalSequenceNumber(r.getDestinationSequenceNumber());
                    }

                    if(precursorAddresses.size() == 1) // Unicast reicht
                    {
                         routeError.setNextHopInRoute(precursorAddresses.get(0));
                         Dispatcher.getInstance().dispatch(routeError);
                    }
                    else // Broadcast if several precursors
                    {
                         Dispatcher.getInstance().dispatchBroadcast(routeError);
                    }
               }
               catch (NullPointerException e)
               {
                    LOG.info("Couldn't send error");
               }
          }
     }


     /**
      * Incoming Replies will end here, here it will be decided if
      * the reply is a reply to the Request send from this Thread.
      * if thats the case, the finished boolean will be set to true and the
      * Thread ends
      *
      * @param event
      */
     @Override
     public void propertyChange(PropertyChangeEvent event)
     {

          Request incomingReply = (Request) event.getNewValue();
          LOG.info("Received a Reply of type: " + incomingReply.getType());

          // Outgoing SendTextRequest
          if (this.transmission.getRequest() instanceof SendTextRequest)
          {
               LOG.info(" This a SendTextRequest waiting for a SendTextAck or a HopAck , got a reply of type " + incomingReply.getType());
               //from me
               if (this.transmission.getRequest().getOriginAddress() == Address.getInstance().getAddress())
               {
                    if (incomingReply instanceof SendTextRequestAck && ((SendTextRequestAck) incomingReply).getMessageSequenceNumber() == ((SendTextRequest) transmission.getRequest()).getMessageSequenceNumber())
                    {
                         this.finished = true;
                    }
               }
               // If forwarded
               else if (this.transmission.getRequest().getOriginAddress() != Address.getInstance().getAddress())
               {
                    if (incomingReply instanceof HopAck && ((HopAck) incomingReply).getMessageSequenceNumber() == ((SendTextRequest) transmission.getRequest()).getMessageSequenceNumber())
                    {
                        this.receivedHopHack = true;
                    }
               }
          }
          // RouteRequest
          else if (this.transmission.getRequest() instanceof RouteRequest && incomingReply instanceof RouteReply)
          {
               if (incomingReply.getOriginAddress() == this.transmission.getRequest().getDestinationAddress())
               {
                    this.finished = true;
                    onRouteRequestSuccess();
               }
          }
          else if (this.transmission.getRequest() instanceof RouteReply && incomingReply instanceof RouteReplyAck)
          {
               LOG.info("This a RouteReply waiting receiving a RouteReplyAck");
               if (this.transmission.getRequest().getNextHopInRoute() == incomingReply.getLastHopInRoute())
               {
                    LOG.info("Got an ACk for the RouteReply");
                    this.finished = true;
               }
          }
          else
          {
               LOG.info("Reply not for this request");
          }
     }
}
