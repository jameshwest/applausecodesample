package demo.dao;


public class Bug implements Comparable<Bug>
{
   private int  bugId;
   private int  deviceId;
   private int  testerId;
   public Bug()
   {
      super();
   }
   public Bug(final int id, final int did, final int tid)
   {
      bugId    = id;
      deviceId = did;
      testerId = tid;
   }
   public int getId()
   {
      return bugId;
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
      sb.append("Bug: ").append(bugId).append(", ").append(deviceId).append(", ").append(testerId);
      return sb.toString();
   }
   
   @Override
   public int compareTo(Bug other)
   {
      int result = Integer.MIN_VALUE;
      if (other != null)
      {
         result = bugId - other.getId();
      }
      return result;
   }

   @Override 
   public boolean equals(Object o)
   {
      boolean result = false;
      if (o != null && o instanceof Tester)
      {
         result = (compareTo((Bug) o) == 0);
      }
      return result;
   }

   @Override 
   public int hashCode()
   {
      return bugId;
   }
   
}
