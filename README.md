Sample code for coding challenge

"User" Interface:
  - There is no GUI/WebUI for this sample code.
  - It is driven completely on HTTP GET requests.
  - When started, the web service listens on port 8080
  - The URL to access the code is:  http://localhost:8080/match
     + This will perform a match for ALL countries and ALL devices
     + The output can be reduced by passing in values for the "country" and "device" as query parameters
       EXAMPLE:
          http://localhost:8080/match?country=ALL&device=ALL

       The returns the same value as the default query.  Other examples:
 
            http://localhost:8080/match?country=US
            http://localhost:8080/match?country=US,JP
            http://localhost:8080/match?country=US,JP&device=1
            http://localhost:8080/match?device=1
            http://localhost:8080/match?device=1,Nexus 4
                      - Yes, devices can be input by ID or by description

       On error (invalid device or country, you get an error message)
   - All data returned is in a JSON structure.  When there is no error, "rankings" contains an ordered array of tester and the number of bugs
     they've found that match the criteria

Code Structure
   src/main/java
      demo           :  Two classes that drive this Spring Application
      demo.dao       :  The POJOs send back as a response and the data objects we model.  This is a little messy and I'd refactor this for production
      demo.datastore :  The "datastore".  It's all file based (for production, I'd use a RDBMS)
   
   src/main/resources
      application.properties :  Set these to control behavior
      data                   :  The directory where the data is stored and read
      
