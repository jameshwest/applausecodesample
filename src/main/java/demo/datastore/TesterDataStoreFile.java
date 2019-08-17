package demo.datastore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import demo.dao.Bug;
import demo.dao.Device;
import demo.dao.Tester;

@Component
public class TesterDataStoreFile implements TesterDataStore
{
   private static final Logger logger = LoggerFactory.getLogger(TesterDataStoreFile.class);
   private static final String BUGS_FILE            = "bugs.csv";
   private static final String DEVICES_FILE         = "devices.csv";
   private static final String TESTERS_FILE         = "testers.csv";
   private static final String TESTER_2_DEVICE_FILE = "tester_device.csv";
   
   private Map<String,  Set<Tester>>  countries2Testers = new HashMap<>();
   private Map<Integer, Tester>       id2Testers = new HashMap<>();
   
   private Map<Integer, Integer>      testerId2DeviceId = new HashMap<>();
   private Map<Integer, Set<Integer>> deviceId2Testers = new HashMap<>();
   
   private Map<Integer, String>       deviceId2Descr    = new HashMap<>();
   private Map<String, Integer>       deviceDescr2Id    = new HashMap<>();
   
   private Map<Integer, Bug>          bugId2Bug         = new HashMap<>();
   private Map<Integer, Set<Bug>>     tester2Bugs       = new HashMap<>();
   
   
   /**
    * Spring will create a single instace of this class and this CTOR will be called once.
    * When it's called, we need to load our data files
    */
   public TesterDataStoreFile()
   {
      // When created, we need to load the necessary data files
      final List<String> bugs = loadDataFile(BUGS_FILE);
      addBugs(bugs);
      final List<String> devices = loadDataFile(DEVICES_FILE);
      addDevices(devices);
      final List<String> testers = loadDataFile(TESTERS_FILE);
      addTesters(testers);
      final List<String> tester2Devices = loadDataFile(TESTER_2_DEVICE_FILE);
      addTester2Devices(tester2Devices);
   }
   
   @Override
   public Bug findBug(Integer bugId)
   {
      return bugId2Bug.get(bugId);
   }
   
   @Override
   public Set<Integer> bugIds()
   {
      return bugId2Bug.keySet();
   }
   
   @Override
   public Integer bugsForTester(Integer candidate, Set<Integer> deviceIds)
   {
      int value = 0;
      Set<Bug> bugsForTester = tester2Bugs.get(candidate);
      if (bugsForTester != null)
      {
         for (Bug bug : bugsForTester)
         {
            if (deviceIds.contains(Integer.valueOf(bug.getDeviceId())) == true)
            {
               value++;
            }
         }
      }
      return Integer.valueOf(value);
   }

   @Override
   public Tester findTester(Integer testerId)
   {
      return id2Testers.get(testerId);
   }
   
   @Override
   public Set<Integer> testerMatches(Set<String> countryIds, Set<Integer> deviceIds)
   {
      Set<Integer> testersByDevice  = new HashSet<>();
      Set<Integer> testersByCountry = new HashSet<>();
      for (Integer deviceId : deviceIds)
      {
         Set<Integer> testIdSet = deviceId2Testers.get(deviceId);
         if (testIdSet != null)
         {
            testersByDevice.addAll(testIdSet);
         }
      }
      for (String countryId : countryIds)
      {
         Set<Tester> canidateSet = countries2Testers.get(countryId);
         if (canidateSet != null)
         {
            for (Tester tester : canidateSet)
            {
               testersByCountry.add(tester.getId());
            }
         }
      }
      // Now we need to take the set intersection of the two sets
      Set<Integer> result = new HashSet<>();
      for (Integer testerId : testersByDevice)
      {
         if (testersByCountry.contains(testerId) == true)
         {
            result.add(testerId);
         }
      }
      return result;
   }
   
   @Override
   public String  deviceId2Description(Integer devId)
   {
      return deviceId2Descr.get(devId);
   }

   @Override
   public Integer findDevice(String currentDevice)
   {
      Integer result = null;
      // First check if the device is an ID
      int intVal = str2int(currentDevice);
      if (intVal > 0)
      {
         // This is a device ID.  Do we know this ID?
         Integer candidate = Integer.valueOf(intVal);
         if (deviceId2Descr.containsKey(candidate) == true)
         {
            result = candidate;
         }
      }
      else
      {
         // This isn't a number.  Is it a device name?
         result = deviceDescr2Id.get(currentDevice);
      }
      return result;
   }
   @Override
   public Set<Integer> getAllDevices()
   {
      Set<Integer> result = new HashSet<>();
      result.addAll(deviceId2Descr.keySet());
      return result;
   }
   
   @Override
   public Set<String> getAllCountries()
   {
      Set<String> result = new HashSet<>();
      result.addAll(countries2Testers.keySet());
      return result;
   }
   
   @Override
   public boolean isKnownCountry(String currentCountry)
   {
      String cnty = currentCountry == null ? "" : currentCountry.trim();
      return countries2Testers.containsKey(cnty);
   }
   
   List<String> loadDataFile(final String fileName)
   {
      List<String> data = null;
      try
      {
         final String pathInJar = "data/" + fileName;
         // Try to find our resources file
         File resource = new ClassPathResource(pathInJar).getFile();
         //data = new String(Files.readAllBytes(resource.toPath()));
         data = Files.readAllLines(resource.toPath());
         
      }
      catch (Exception e)
      {
         // Unable to find in the ClassPathResources.  Maybe we can find in file system?
         File resAlt = new File("target/classes/data/" + fileName);
         if (resAlt.canRead() == true)
         {
            try
            {
               data = Files.readAllLines(resAlt.toPath());
            } 
            catch (IOException e1)
            {
               logger.warn("Unable to load data file: {}", fileName, e);
            }
         }
         else
         {
            logger.warn("Unable to locate data file: {}", fileName, e);
         }
      }
      return data;
   }
   private void addTester2Devices(List<String> tester2Devices)
   {
      // need to skip the first row, its a header row
      Iterator<String> iter = tester2Devices.iterator();
      iter.next();
      while (iter.hasNext() == true)
      {
         String row = iter.next();
         if (row != null)
         {
            String [] data = row.split(",");
            if (data != null && data.length >= 2)
            {
               int testerId  = str2int(data[0]);
               int deviceId  = str2int(data[1]);
               if (testerId > 0 && deviceId > 0)
               {
                  addTester2Device(testerId, deviceId);
               }
               else
               {
                  logger.warn("Invalid tester_device data row: '" + row + "'");
               }
            }
         }
      }  
   }
   private void addTester2Device(int testerId, int deviceId)
   {
      Integer testerObj = Integer.valueOf(testerId);
      Integer deviceObj = Integer.valueOf(deviceId);
      testerId2DeviceId.put(testerObj, deviceObj);
      Set<Integer> testers = deviceId2Testers.get(deviceObj);
      if (testers == null)
      {
         testers = new HashSet<>();
         deviceId2Testers.put(deviceObj, testers);
      }
      testers.add(testerObj);
   }
   private void addTesters(List<String> testers)
   {
      // need to skip the first row, its a header row
      Iterator<String> iter = testers.iterator();
      iter.next();
      while (iter.hasNext() == true)
      {
         String row = iter.next();
         if (row != null)
         {
            String [] data = row.split(",");
            if (data != null && data.length >= 5)
            {
               int    testerId  = str2int(data[0]);
               String firstName = trimQuite(data[1]);
               String lastName  = trimQuite(data[2]);
               String country   = trimQuite(data[3]);
               String lastLogin = trimQuite(data[4]);
               if (testerId > 0 && country != null && country.trim().length() > 0)
               {
                  addTester(testerId, firstName, lastName, country, lastLogin);
               }
               else
               {
                  logger.warn("Invalid tester data row: '" + row + "'");
               }
            }
         }
      } 
   }   
   private void addTester(int testerId, String firstName, String lastName, String country, String lastLogin)
   {
      Tester tester = new Tester(testerId, firstName, lastName, country, lastLogin);
      if (country != null)
      {
         country = country.trim();
         Set<Tester> testerSet = countries2Testers.get(country);
         if (testerSet == null)
         {
            testerSet = new HashSet<>();
            countries2Testers.put(country, testerSet);
         }
         testerSet.add(tester);
      }
      id2Testers.put(Integer.valueOf(testerId), tester);
      
   }
   private void addDevices(List<String> devices)
   {
      // need to skip the first row, its a header row
      Iterator<String> iter = devices.iterator();
      iter.next();
      while (iter.hasNext() == true)
      {
         String row = iter.next();
         if (row != null)
         {
            String [] data = row.split(",");
            if (data != null && data.length >= 2)
            {
               int deviceId = str2int(data[0]);
               String descr = trimQuite(data[1]);
               if (deviceId > 0 && descr != null && descr.trim().length() > 0)
               {
                  addDevice(deviceId, descr);
               }
               else
               {
                  logger.warn("Invalid device data row: '" + row + "'");
               }
            }
         }
      } 
   }
   private void addDevice(int deviceId, String descr)
   {
      Integer devIdObj = Integer.valueOf(deviceId);
      deviceId2Descr.put(devIdObj, descr);
      deviceDescr2Id.put(descr, devIdObj);
   }
   private void addBugs(List<String> bugsList)
   {
      // need to skip the first row, its a header row
      Iterator<String> iter = bugsList.iterator();
      iter.next();
      while (iter.hasNext() == true)
      {
         String row = iter.next();
         if (row != null)
         {
            String [] data = row.split(",");
            if (data != null && data.length >= 3)
            {
               int bugId = str2int(data[0]);
               int deviceId = str2int(data[1]);
               int testerId = str2int(data[2]);
               if (bugId > 0 && deviceId > 0 && testerId > 0)
               {
                  addBug(bugId, deviceId, testerId);
               }
               else
               {
                  logger.warn("Invalid bug data row: '" + row + "'");
               }
            }
         }
      } 
   }
   
   private void addBug(int bugId, int deviceId, int tester)
   {
      Integer testerObj = Integer.valueOf(tester);
      Bug bug = new Bug(bugId, deviceId, tester);
      bugId2Bug.put(Integer.valueOf(bugId), bug);
      
      Set<Bug> bugs = tester2Bugs.get(testerObj);
      if (bugs == null)
      {
         bugs = new HashSet<>();
         tester2Bugs.put(testerObj, bugs);
      }
      bugs.add(bug);
   }
   
   static int str2int(final String val)
   {
      
      int result = -1;
      if (val != null)
      {
         String strpVal = val.replace("\"", "");
         try
         {
            result = Integer.parseInt(strpVal);
         }
         catch (NumberFormatException nfe)
         {
            result = -1;
         }
      }
      return result;
   }
   static String trimQuite(final String val)
   {
      
      String result = null;
      if (val != null)
      {
         result = val.replace("\"", "");
      }
      return result;
   }
}
