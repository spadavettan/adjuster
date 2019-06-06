import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.util.*;
import com.opencsv.CSVWriter;


public class Main {


    public static void writeToCSV(String filePath, String[][] data)
    {
        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filePath);
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            // adding header to csv
            String[] header = { "Advertiser", "CPM", "End Date", "ID", "Name", "Start Date", "Total Clicks", "Total Impressions" };
            writer.writeNext(header);

            // add data to csv
            for(int i=0; i<data.length; i++) {
                writer.writeNext(data[i]);
            }

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static String sendRequest(String url) {
        String result = "";

        //harvest data
        try {

            //set up client
            HttpClient client = HttpClientBuilder.create().build();


            //send get request for data
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);


            //convert response to string
            result = org.apache.commons.io.IOUtils.toString(response.getEntity().getContent());
        }

        //if connection fails
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

    public static String[][] parseJSON(String campaigns, String creatives)
    {
        //empty array if try/catch fails
        String [][] data1 = new String[1][1];

        try
        {
            //create json arrays for campaigns and creatives
            org.json.JSONArray campArray = new org.json.JSONArray(campaigns);
            org.json.JSONArray creArray = new org.json.JSONArray(creatives);


            //initialize the hashmap with key=campaign id and value=clicks/imps in form of int array
            HashMap<Integer, int[]> campaignMap = new HashMap<Integer, int[]>();
            String[][] data = new String[campArray.length()][8];



            //go through the creatives, update clicks/impressions in the map for their respective campaigns
            for(int i = 0; i<creArray.length(); i++) {

                //make JSON object out of creative and set the key as its parentId
                JSONObject object = creArray.getJSONObject(i);
                int key = object.getInt("parentId");

                //if key doesnt have click/impression array yet, initialize it and assign values from first creative
                if(campaignMap.containsKey(key)==false){
                    int[] clickimp = new int[2];
                    clickimp[0] = object.getInt("clicks");
                    clickimp[1] = object.getInt("impressions");
                    campaignMap.put(key, clickimp);
                }

                //otherwise, get the click/imp array from the campaign and update values
                else {
                    int[] clickimp = campaignMap.get(key);
                    clickimp[0] += object.getInt("clicks");
                    clickimp[1] += object.getInt("impressions");

                    //store updated values back into the map
                    campaignMap.put(key, clickimp);
                }
            }


            //store the clicks and imps from the map to the data array
            for(int i = 0; i<campArray.length(); i++){
                JSONObject object = campArray.getJSONObject(i);
                int [] clickimp = campaignMap.get(object.getInt("id"));

                //write data to data string array to be added to CSV later
                data[i][0] = object.getString("advertiser");
                data[i][1] = object.getString("cpm");
                data[i][2] = object.getString("endDate");
                data[i][3] = String.valueOf(object.getInt("id"));
                data[i][4] = object.getString("name");
                data[i][5] = object.getString("startDate");
                data[i][6] = String.valueOf(clickimp[0]);
                data[i][7] = String.valueOf(clickimp[1]);
            }

            //now we have all the data we need, return
            return data;
        }

        catch (JSONException e) {
            e.printStackTrace();
        }

        //return empty array if method fails
        return data1;
    }


    public static void main(String[] args)
    {
        //set up strings for each api to be processed
        String campaigns = Main.sendRequest("http://homework.ad-juster.com/api/campaigns");
        String creatives = Main.sendRequest("http://homework.ad-juster.com/api/creatives");

        //parse the strings
        String[][] result = Main.parseJSON(campaigns, creatives);
        writeToCSV("output.csv", result);
    }

}
