package demo.dao;

public class Device implements Comparable<Device>
{
   private int    deviceId;
   private String description;
   
   public Device()
   {
      super();
   }
   public Device(final int id, final String descr)
   {
      deviceId    = id;
      description = descr;
   }
   public int getId()
   {
      return deviceId;
   }
   public String getDescription()
   {
      return description;
   }
   
   @Override
   public String toString()
   {
      final StringBuilder sb = new StringBuilder();
      sb.append("Device: ").append(deviceId).append(", ").append(description);
      return sb.toString();
   }
   
   @Override
   public int compareTo(Device other)
   {
      int result = Integer.MIN_VALUE;
      if (other != null)
      {
         result = deviceId - other.getId();
      }
      return result;
   }

   @Override 
   public boolean equals(Object o)
   {
      boolean result = false;
      if (o != null && o instanceof Device)
      {
         result = (compareTo((Device) o) == 0);
      }
      return result;
   }

   @Override 
   public int hashCode()
   {
      return deviceId;
   }
   
}
