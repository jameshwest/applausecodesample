package demo.dao;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class Report
{
   @JsonProperty("rankings")
   private List<ReportEntry> rankings;
   @JsonProperty("countries")
   private List<String> countries;
   @XmlElement
   @JsonProperty("devices")
   private List<String> devices;
   @JsonProperty("error")
   private String error = "NONE";
   
   public Report()
   {
      super();
   }
   public Report(String errorMsg)
   {
      countries = null;
      devices = null;
      rankings = null;
      error = errorMsg;
   }
   
   public Report(List<String> countriesList, List<String> devicesList, List<ReportEntry> rankingList)
   {
      countries = countriesList;
      devices = devicesList;
      rankings = rankingList;
      error = "NONE";
   }
}
