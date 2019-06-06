# adjuster
Final adjuster hw project

Created with IntelliJ, used Apache.HTTP, JSON, and OpenCSV libraries, as well as java.util and io.

Outputs data requested in the assignment into output.csv. Assumed there were no space/time requirements, so I went about it quickly because I started super late already.

The idea behind it was: 

go through creatives once, and record impressions and clicks (as an array of size 2, index 0 is clicks, index 1 is impressions) in a hashmap that is keyed with parentId. 

Then, go through campaigns and organize out all the data for each campaign in a string array, along with their impressions and clicks (which can be obtained through the hashmap using campaign ID). Add string array for each campaign into an array of string arrays for printing later. 

Then, print out the data in a csv.

Ran with intelliJ, should work by running Main.java.
