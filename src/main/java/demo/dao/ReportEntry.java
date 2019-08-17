package demo.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReportEntry
{
   @JsonProperty("numberOfBugs")
   private int bugCount;
   @JsonProperty("tester")
   private Tester tester;
   
   public ReportEntry()
   {
      super();
   }
   public ReportEntry(int count, Tester tstr)
   {
      bugCount = count;
      tester = tstr;
   }
   
   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append(bugCount).append(" - ").append(tester);
      return sb.toString();
   }
}
