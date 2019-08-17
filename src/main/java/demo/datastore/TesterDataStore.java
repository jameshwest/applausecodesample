package demo.datastore;

import java.util.Set;

import demo.dao.Bug;
import demo.dao.Tester;

public interface TesterDataStore
{
   Bug      findBug(final Integer bugId);
   Tester   findTester(final Integer testerId);
   Set<Integer> getAllDevices();
   Integer findDevice(String currentDevice);
   String  deviceId2Description(Integer devId);
   Set<String> getAllCountries();
   boolean isKnownCountry(String currentCountry);
   Set<Integer> bugIds();
   Set<Integer> testerMatches(Set<String> countryIds, Set<Integer> deviceIds);
   Integer bugsForTester(Integer candidate, Set<Integer> deviceIds);
}
