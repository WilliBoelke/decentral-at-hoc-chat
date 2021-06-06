package htwb.ai.willi.routing;

import java.util.ArrayList;

public class RoutingTable
{
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


     public ArrayList<String> gteKnowDevices()
     {
          return knownDevices;
     }

     public void addAddress(String address)
     {
          if(!knownDevices.contains(address))
          {
               knownDevices.add(address);
          }
     }

}
