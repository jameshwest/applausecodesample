package demo.dao;


public class TesterDevice
{
   private int  testerId;
   private int  deviceId;

   public TesterDevice()
   {
      super();
   }
   public TesterDevice(final int tid, final int did)
   {
      deviceId = did;
      testerId = tid;
   }
   public int getDeviceId()
   {
      return deviceId;
   }
   public int getTesterId()
   {
      return testerId;
   }
   
   @Override
   public String toString()
   {
      final StringBuilder sb = new StringBuilder();
      sb.append("TesterDevice: ").append(deviceId).append(", ").append(testerId);
      return sb.toString();
   }

   @Override 
   public boolean equals(Object o)
   {
      boolean result = false;
      if (o != null && o instanceof TesterDevice)
      {
         TesterDevice other = (TesterDevice) o;
         if ((testerId == other.testerId) && (deviceId == other.deviceId))
         {
            result = true;
         }
      }
      return result;
   }

   @Override 
   public int hashCode()
   {
      return testerId;
   }
   
}
