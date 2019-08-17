package demo;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import demo.dao.Tester;
import demo.dao.Report;
import demo.dao.ReportEntry;
import demo.datastore.TesterDataStore;

@RestController
public class DemoRestController
{
   private static final Logger logger = LoggerFactory.getLogger(DemoRestController.class);
   @Autowired
   private TesterDataStore dataStore;
   
   /*
   public void setTesterDataStore(TesterDataStore ds)
   {
      dataStore = ds;
   }
   */
   /**
    * The main entry point for this example code.  The user makes a Web Service call to:
    *  http:{{hostname}}:8080/match?country=X&device=Y
    * X and Y can be comma separated lists of country codes and device names or device ID's
    * 
    * @param country Optional.  Defaults to all.  Otherwise 1 or more 2 character country codes 
    *                           (multiple codes separated by comma)
    * @param device  Optional   Defaults to all.  Otherwise 1 or more device names or ID's
    *                           (multiple codes separated by comma)
    * @return
    */
   @RequestMapping(method=RequestMethod.GET, value="match", produces = "application/json")
   public ResponseEntity<Report> match(@RequestParam(value="country", defaultValue="ALL") final String countries,
                                       @RequestParam(value="device",  defaultValue="ALL") final String devices)
   {
      // Step 1:  Validate the inputs.  Both can be a comma separated list
      //          The validation method will return a set of CountryID's and DeviceId's
      Set<String>  countryIds = validateCountries(countries);
      Set<Integer> deviceIds  = validateDevices(devices);
      if (countryIds == null)
      {
         // The countries data is invalid
         logger.warn("Invalid countries data: {}", countries);
         Report rpt = new Report("Invalid countries data = '" + countries + "'");
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rpt);
      }
      if (deviceIds == null)
      {
         logger.warn("Invalid devices data: {}", devices);
         Report rpt = new Report("Invalid devices data = '" + devices + "'");
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rpt);
         // The devices data is invalid
      }
      // At this point the data is good.  We can start to match.  We need to find a set of testers
      // who have experience with the device and who reside in the country
      Set<Integer> candidateTesters = findTesters(countryIds, deviceIds);
      if (candidateTesters == null || candidateTesters.size() == 0)
      {
         ResponseEntity.status(HttpStatus.OK).body("No Matches");
      }
      final Report rpt = generateReport(candidateTesters, countryIds, deviceIds);
      return ResponseEntity.status(HttpStatus.OK).body(rpt);
   }

   private Report generateReport(Set<Integer> candidateTesters, Set<String> countryIds, Set<Integer> deviceIds)
   {
      TreeMap<Integer, Set<Tester>> resultMap = new TreeMap<>(); // Index is the number of bugs found
      for (Integer candidate : candidateTesters)
      {
         Tester tester = dataStore.findTester(candidate);
         // Find the bugs for each tester
         Integer numBugs = dataStore.bugsForTester(candidate, deviceIds);
         Set<Tester> testersWithThisNumBugs = resultMap.get(numBugs);
         if (testersWithThisNumBugs == null)
         {
            testersWithThisNumBugs = new HashSet<>();
            resultMap.put(numBugs, testersWithThisNumBugs);
         }
         testersWithThisNumBugs.add(tester);
      }
      // The TreeMap is in Order (but reverse Order).  Need to Interate over the Treemap and create
      // the ordered list
      List<ReportEntry> entries = new LinkedList<>();
      
      for (Entry<Integer, Set<Tester>> entry : resultMap.entrySet())
      {
         int numBugs = entry.getKey().intValue();
         Set<Tester> testersWithThisNumBugs = entry.getValue();
         for (Tester tester : testersWithThisNumBugs)
         {
            ReportEntry rptEntry = new ReportEntry(numBugs, tester);
            entries.add(0, rptEntry);
         }
      }
      List<String> countryList = new LinkedList<>();
      countryList.addAll(countryIds);
      List<String> deviceList = new LinkedList<>();
      for (Integer deviceId : deviceIds)
      {
         String dev = dataStore.deviceId2Description(deviceId);
         deviceList.add(dev == null ? deviceId.toString() : dev);
      }
      Report result = new Report(countryList, deviceList, entries);
      return result;
   }

   private Set<Integer> findTesters(Set<String> countryIds, Set<Integer> deviceIds)
   {
      return dataStore.testerMatches(countryIds, deviceIds);
   }



   private Set<Integer> validateDevices(String devices)
   {
      HashSet<Integer> deviceIds = new HashSet<>();
      String [] devicesAsArray = splitString(devices);
      if (devicesAsArray != null)
      {
         for (int ii = 0; ii < devicesAsArray.length; ii++)
         {
            final String currentDevice = devicesAsArray[ii];
            if (isAll(currentDevice) == true)
            {
               addAllDevices(deviceIds);
               break;
            }
            final Integer deviceId = deviceString2DeviceId(currentDevice); // This also validates data
            if (deviceId == null)
            {
               deviceIds = null; // Invalid device, return an error
               logger.warn("Invalid device id='{}'", currentDevice);
               break;
            }
            deviceIds.add(Integer.valueOf(deviceId));
         }
      }
      return deviceIds;
   }

   private Integer deviceString2DeviceId(String currentDevice)
   {
      Integer result = dataStore.findDevice(currentDevice);
      return result;
   }

   private void addAllDevices(Set<Integer> deviceIds)
   {
      final Set<Integer> allDevices = dataStore.getAllDevices();
      deviceIds.addAll(allDevices);
   }

   private Set<String> validateCountries(String countries)
   {      
      HashSet<String> countryIds = new HashSet<>();;
      String [] countriesAsArray = splitString(countries);
      if (countriesAsArray != null)
      {
         for (int ii = 0; ii < countriesAsArray.length; ii++)
         {
            final String currentCountry = countriesAsArray[ii];
            if (isAll(currentCountry) == true)
            {
               addAllCountries(countryIds);
               break;
            }
            boolean validCountry = isValidCountry(currentCountry);
            if (validCountry == false)
            {
               countryIds = null; // Invalid device, return an error
               logger.warn("Invalid country id='{}'", currentCountry);
               break;
            }
            countryIds.add(currentCountry);
         }
      }
      return countryIds;
   }
   
   private void addAllCountries(HashSet<String> countryIds)
   {
      final Set<String> allCountries = dataStore.getAllCountries();
      countryIds.addAll(allCountries);     
   }

   private boolean isValidCountry(String currentCountry)
   {
      return dataStore.isKnownCountry(currentCountry);
   }

   private String [] splitString(final String input)
   {
      String [] result = null;
      if (input != null)
      {
         result = input.split(",");
         if (result != null)
         {
            for (int ii = 0; ii < result.length; ii++)
            {
               result[ii] = result[ii].trim();
            }
         }
      }
      return result;
   }
   
   private static boolean isAll(final String value)
   {
      boolean result = false;
      if ("ALL".equals(value) == true)
      {
         result = true;
      }
      return result;
   }
}
