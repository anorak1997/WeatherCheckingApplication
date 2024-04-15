// reterive weather data from API  - this backend logic will fetch the latest weather
// data form the external API and return it. The  GUI will
// display this data to the user

import jdk.security.jarsigner.JarSigner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Scanner;
import java.util.SimpleTimeZone;

public class WeatherApp {
    // Fetch the weather data for the given location
    public static JSONObject getWeatherData(String locationName){
        // get location coordinated using the geolocation API
        JSONArray locationData = getlocationData(locationName);

        // Extract latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // Build API request URL with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                            "latitude=" + latitude +  "&longitude=" + longitude +
                            "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";

        try{
            // Call api and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // Check for response status
            // 200 - means that the connection was a success
            if(conn.getResponseCode() != 200){
                System.out.println("Error could not connect to API");
                return null;
            }

            // Storing result in json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());

            while(scanner.hasNext()){
                // Read and store into the string builder
                resultJson.append(scanner.nextLine());
            }

            // Close scannner
            scanner.close();

            // Close URL connection
            conn.disconnect();

            // Parse the obtained data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // Retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            // We want to get the current hour's data
            // So we need to get the index of our current hour
            JSONArray time = (JSONArray) hourly.get("time");
            int index =  findIndexOfCurrentTime(time);

            // Get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // Get weather code
            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            // Get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // Get wind speed
            JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windSpeed = (double) windSpeedData.get(index);

            // Build the weather json data object that we are going to access in our frontend
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windSpeed);

            return weatherData;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    // Retries geographical coordinates for the given location name
    public static JSONArray getlocationData(String locationName){
        // replace any white space to + to adhere to the API's request format
        locationName = locationName.replaceAll(" ", "+");

        // Build API url with the location parameter
         String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName + "&count=10&language=en&format=json";

         try {
            // Call api and get a response
             HttpURLConnection conn = fetchApiResponse(urlString);

             // Check response status
             // 200 means successful connection
             if(conn.getResponseCode() != 200){
                 System.out.println("Could not connect to API");
                 return  null;
             }else {
                 // Store API result
                 StringBuilder resultJson = new StringBuilder();
                 Scanner scanner = new Scanner(conn.getInputStream());

                 // Read and store the resulting json data into our string builder
                 while(scanner.hasNext()){
                     resultJson.append(scanner.nextLine());
                 }

                 // Close scanner
                 scanner.close();

                 // Close URL Conncection
                 conn.disconnect();

                 // Parse the json string into the json object
                 JSONParser parser = new JSONParser();
                 JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                 // Get the list of location data the API generated from the location name
                 JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                 return locationData;

             }
         }catch (Exception e){
             e.printStackTrace();
         }

         // Couldn't find location
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            // Attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection)  url.openConnection();

            // Set request method to get
            conn.setRequestMethod("GET");

            // Connect to our API
            conn.connect();

            return conn;
        }catch (IOException e){
            e.printStackTrace();

        }

        // Could not make a connection
        return  null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        // Iterate through the time list and see which one matches our current time
        for(int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                // Return the index
                return i;
            }
        }


        return 0;
    }

    private static String getCurrentTime(){
        // get current data and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Format data to be 2024-09-02T00:00 (This is how data is read in the API)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // Format and print the current date and time
        String formatDateTime = currentDateTime.format(formatter);

        return formatDateTime;
    }

    // Convert the weather code to something more readable
    private static String convertWeatherCode(long weatherCode){
        String weatherCondition = "";
        if (weatherCode== 0L){
            weatherCondition = "Clear";
        }else if(weatherCode <= 3L && weatherCode > 0L){
            weatherCondition = "Cloudy";
        }else if ((weatherCode >= 51L && weatherCode <= 67L) ||
                (weatherCode >= 80L && weatherCode <= 99L) ){
            weatherCondition = "Rain";
        }else if (weatherCode >= 71L && weatherCode <= 77L){
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }
}
