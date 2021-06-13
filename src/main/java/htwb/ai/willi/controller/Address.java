package htwb.ai.willi.controller;

/**
 * Since the Address is needed on many places
 * it gets it own singleton class , so it can be accessed from
 * everywhere
 */
public class Address
{
     private static Address instance;
     private byte address;

     private Address()
     {
          //set standard Address = 13;
          address = 13;
     }

     public static Address getInstance()
     {
          if(instance == null)
          {
               instance = new Address();
          }
          return instance;
     }

     public byte getAddress()
     {
          return address;
     }

     public void setAddress(byte address)
     {
          this.address = address;
     }
}
