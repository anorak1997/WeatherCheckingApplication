import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui  extends JFrame {

    private JSONObject weatherData;
    public WeatherAppGui(){
        // setup GUI and add titile
        super("Weather App");

        // Configure GUI to end all the program's process once it has  been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set the size of the GUI in pixel
        setSize(450,650);

        // load out GUI at the centre fo the screen
        setLocationRelativeTo(null);

        // Make out layout manager null to manually position our components within the GUI
        setLayout(null);

        // Prevent any resize of our GUI
        setResizable(false);

        addGuiComponents();
    }
    private void addGuiComponents(){
        // Search Field
        JTextField searchTextField = new JTextField();

        // Set the location and add size of our component
        searchTextField.setBounds(15, 15,351,45);

        // Change the font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);

        // Weather Image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // Temperature text
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0,350,450,54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        // Centre the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // Weather condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // Humidity Image
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15,500,74,66);
        add(humidityImage);

        // Humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100% </html>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // Windspeed Image
        JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedImage.setBounds(220,500,74,66);
        add(windspeedImage);

        // Windspeed Text
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windspeedText.setBounds(310, 500, 98, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        // Search button
        JButton searchButton  = new JButton(loadImage("src/assets/search.png"));

        // Change cursor to hand cursor when hovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get location from user
                String userInput = searchTextField.getText();

                // Validate input - remove whitespace to ensure non-empty text
                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }

                // Retrieve weather data
                weatherData =  WeatherApp.getWeatherData(userInput);

                // Update GUI

                // Update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");

                // Depending on the condition, we will update the weather image that corresponds with the condition
                switch (weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                }

                // Update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                // Update weather condition text
                weatherConditionDesc.setText(weatherCondition);

                // Update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                // Update wind speed text
                double windSpeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> " + windSpeed + "km/h</html>");

            }
        });
        add(searchButton);
    }


    // Used to create images in out gui components
    private ImageIcon loadImage(String  resourcePath){
        try{
            // Read the image file from the path given
            BufferedImage image = ImageIO.read(new File(resourcePath));

            // return the image icon so our component can render it
            return new ImageIcon(image);
        }catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
    }
}
