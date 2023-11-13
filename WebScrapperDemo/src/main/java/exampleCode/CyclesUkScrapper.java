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
        for (int j = 1; j < 2; j++) {

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
                for (int i = 0; i < 5; i++) {

                    //Data needed: productName, description, price, 
                    //Individual section
                    Elements eachSection = mainSection.get(i).select(".product-card-custom");

                    //Product name
                    Elements productName = eachSection.select(".product-title");

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
                     
                  
                                   
                   //For bikes table
                    try {
                        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cst3130_cw1", "root", "");
                        if (connection != null && !connection.isClosed()) {

                            // Perform database operations here if the connection is successful
                            // Update the INSERT statement to use placeholders
                            String insertQuery = "INSERT INTO bikes (name, description) VALUES ( ?, ?)";
                            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

                            // Set the scraped data as values for the placeholders in the INSERT statement
                            preparedStatement.setString(1, productName.text());
                          //  preparedStatement.setInt(2, price);
                            preparedStatement.setString(2, description);

                            //preparedStatement.setString(3, price.text());
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
                    
                    
                    //For bike_models table
                     try {
                        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cst3130_cw1", "root", "");
                        if (connection != null && !connection.isClosed()) {

                            // Perform database operations here if the connection is successful
                            // Update the INSERT statement to use placeholders
                            String insertQuery = "INSERT INTO bike_models (image) VALUES (?)";
                            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

                            // Set the scraped data as values for the placeholders in the INSERT statement
                            preparedStatement.setString(1, imgUrl);
                            
//                            preparedStatement.setSting(2, size);
//                            preparedStatement.setString(2, color);

                            //preparedStatement.setString(3, price.text());
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
                     
                     
                     //For prices table
                      try {
                        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cst3130_cw1", "root", "");
                        if (connection != null && !connection.isClosed()) {

                            // Perform database operations here if the connection is successful
                            // Update the INSERT statement to use placeholders
                            String insertQuery = "INSERT INTO prices (urls, priceS) VALUES ( ?, ?)";
                            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

                            // Set the scraped data as values for the placeholders in the INSERT statement
                            preparedStatement.setString(1, nextPageUrl);
                            preparedStatement.setInt(2, price);
                                
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
