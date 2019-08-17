package demo.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tester implements Comparable<Tester>
{
   @JsonProperty("id")
   private Integer testerId;
   @JsonProperty("firstName")
   private String firstName;
   @JsonProperty("lastName")
   private String lastName;
   @JsonProperty("country")
   private String country;
   
   public Tester()
   {
      super();
   }
   public Tester(final int id, final String fn, final String ln, final String ctry, final String ll)
   {
      testerId  = Integer.valueOf(id);
      firstName = fn;
      lastName  = ln;
      country   = ctry;
   }
   public Integer getId()
   {
      return testerId;
   }
   public String getFirstName()
   {
      return firstName;
   }
   public String getLastName()
   {
      return lastName;
   }
   public String getCountry()
   {
      return country;
   }
   
   @Override
   public String toString()
   {
      final StringBuilder sb = new StringBuilder();
      sb.append("Tester: ").append(testerId).append(", ").append(firstName).append(" ").append(lastName);
      sb.append(", ").append(country); //.append(", ").append(lastLogin);
      return sb.toString();
   }
   
   @Override
   public int compareTo(Tester other)
   {
      int result = Integer.MIN_VALUE;
      if (other != null)
      {
         result = testerId.intValue() - other.getId().intValue();
      }
      return result;
   }

   @Override 
   public boolean equals(Object o)
   {
      boolean result = false;
      if (o != null && o instanceof Tester)
      {
         result = (compareTo((Tester) o) == 0);
      }
      return result;
   }

   @Override 
   public int hashCode()
   {
      return testerId;
   }
   
}
