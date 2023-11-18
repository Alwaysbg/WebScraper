package exampleCode;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author barun
 */
public class CyclesUkScrapper extends Thread {

    //showStatus method to check if the connection is secured or not
    public void showStatus() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cst3130_cw1", "root", "");
            if (connection != null && !connection.isClosed()) {
                System.out.println("Connection to the database is successful.");
                connection.close(); // Close the connection when done
            } else {
                System.out.println("Failed to make a connection to the database.");
            }
        } catch (SQLException e) {
            // Handle SQL exceptions appropriately
            System.out.println("Connection to the database failed. Error caused due to: " + e.getMessage());
        }
    }

    public void run() {

        // j is goiing to represent pageNumber 
        for (int j = 1; j < 5; j++) {

            try {

                String URL = "https://www.cyclesuk.com/shop/bikes/?page=" + j;

                //connecting with the website and fetching Html content with get() method
                Document doc = Jsoup.connect(URL).get();

                //Section where the bikes are layed out
                Elements mainSection = doc.select(".product-card-custom");

                int price;

                //Display the page number
                System.out.println("Page ." + j);
                System.out.println("");
                for (int i = 0; i < mainSection.size(); i++) {

                    //Data needed: productName, description, price, 
                    //Individual section
                    Elements eachSection = mainSection.get(i).select(".product-card-custom");

                    //Product name
                    Elements productName = eachSection.select(".product-title");
                    String name = productName.text();

//                  product price
//                  In this website the prices are wrapped with discount so just extracting the current price
                    Elements priceSection = eachSection.select(".product-price");
                    String[] prices = priceSection.text().split(" ");

                    //in allmost all the price sections the first word is the price if not we use condotion
                    String stringPrice = prices[0];

                    if (stringPrice.equals("From")) {
                        stringPrice = prices[1];
                    }

                    // Removing non-numeric characters
                    String numString = stringPrice.replaceAll("[^0-9]", "");
                    // Converting string to a BigDecimal
                    price = Integer.parseInt(numString) / 100;

                    //img element
                    Elements imgElement = eachSection.select("img");
                    //img url
                    String imgUrl = imgElement.attr("src");

                    //This is the Url that takes to the page where you get other data like description size color etc
                    Elements nextPageUrlElement = eachSection.select("a");
                    String nextPageUrl = "  https://www.cyclesuk.com" + nextPageUrlElement.attr("href");

                    Document nextPageDoc = Jsoup.connect(nextPageUrl).get();

                    Elements descriptionContainer = nextPageDoc.select("#product-description");
                    Elements paragraph = descriptionContainer.select("p");
                    //there are list of paragraph so we just take the paragraph we need by index
                    String description = paragraph.get(1).text();
                    
                    //extracting size
                    Elements sizeSection = nextPageDoc.select(".breadcrumbs");
                    Elements UnList = sizeSection.select("a");
                    int lastElement = UnList.size()-1;
                    Elements li = UnList.get(lastElement).select("a");
                    
                    

                    int idBikes = 0;  //primary key for bikes table                 
                    int idBikeModel = 0; //primary key for bike_model table
                    int bikes_id; //foreign key for bike_models table 
                    int bike_models_id;  //foreign key for prices table

                    //For bikes table
                    try {
                        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cst3130_cw1", "root", "");

                        if (connection != null && !connection.isClosed()) {

                            // Update the INSERT statement to retrieve generated keys
                            String insertQuery = "INSERT INTO bikes (name, description) VALUES (?, ?)";
                            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);

                            // Set the scraped data as values for the placeholders in the INSERT statement
                            preparedStatement.setString(1, name);
                            preparedStatement.setString(2, description);

                            // Execute the INSERT statement
                            preparedStatement.executeUpdate();

                            // Retrieve the generated keys
                            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

                            if (generatedKeys.next()) {
                                // Get the auto-generated ID
                                idBikes = generatedKeys.getInt(1);
                                //System.out.println("Generated ID: " + id);
                                // Now you can use the generatedId as needed
                            } else {
                                System.out.println("Failed to retrieve generated ID.");
                            }

                            // Close resources
                            generatedKeys.close();
                            preparedStatement.close();
                            connection.close();

                        } else {
                            System.out.println("Failed to make a connection to the database.");
                        }

                    } catch (SQLException e) {
                        // Handle SQL exceptions appropriately
                        System.out.println("Connection to the database failed. Error message: " + e.getMessage());
                    }

                    bikes_id = idBikes;//after data is inserted the id is assigned to foreign key of bike_model table
                    //System.out.println("primary key for bikes table is " + bikes_id);

                    //For bike_models table
                    try {
                        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cst3130_cw1", "root", "");

                        if (connection != null && !connection.isClosed()) {

                            // Update the INSERT statement to retrieve generated keys
                            String insertQuery = "INSERT INTO bike_models (bikes_id, image) VALUES (?,?)";
                            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);

                            // Set the scraped data as values for the placeholders in the INSERT statement
                            preparedStatement.setInt(1, bikes_id);
                            preparedStatement.setString(2, imgUrl);

                            // Execute the INSERT statement
                            preparedStatement.executeUpdate();

                            // Retrieve the generated keys
                            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

                            if (generatedKeys.next()) {
                                // Get the auto-generated ID
                                idBikeModel = generatedKeys.getInt(1);
                                //System.out.println("Generated ID: " + idBikeModel);
                                // Now you can use the generatedId as needed
                            } else {
                                System.out.println("Failed to retrieve generated ID.");
                            }

                            // Close resources
                            generatedKeys.close();
                            preparedStatement.close();
                            connection.close();

                        } else {
                            System.out.println("Failed to make a connection to the database.");
                        }

                    } catch (SQLException e) {
                        // Handle SQL exceptions appropriately
                        System.out.println("Connection to the database failed. Error message: " + e.getMessage());
                    }

                    bike_models_id = idBikeModel;
                    //System.out.println("primary key for bike_models table is "+bike_models_id);

                    //For prices table
                    try {
                        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cst3130_cw1", "root", "");
                        if (connection != null && !connection.isClosed()) {

                            // Perform database operations here if the connection is successful
                            // Update the INSERT statement to use placeholders
                            String insertQuery = "INSERT INTO prices (bike_models_id, urls, priceS) VALUES (?, ?, ?)";
                            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

                            // Set the scraped data as values for the placeholders in the INSERT statement
                            preparedStatement.setInt(1, bike_models_id);
                            preparedStatement.setString(2, nextPageUrl);
                            preparedStatement.setInt(3, price);

                            preparedStatement.executeUpdate();
                            preparedStatement.close();

                            connection.close(); // Close the connection when done
                        } else {
                            System.out.println("Failed to make a connection to the database.");
                        }
                    } catch (SQLException e) {
                        //Handle SQL exceptions appropriately
                        System.out.println("Connection to the database failed. Error message: " + e.getMessage());
                    }
                }//End of for loop over the page's list items

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Error:" + e.getMessage());
                }

            } catch (IOException e) {
                System.out.println("Exception caused: " + e.getMessage());
            }
        } //End of all available pages (page number: 69)
    }
}
