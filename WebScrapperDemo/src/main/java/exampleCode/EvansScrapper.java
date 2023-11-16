package exampleCode;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EvansScrapper {

    EvansScrapper() {
        ScrapeEvans();
    }

    public static void ScrapeEvans() {
        for (int j = 1; j <= 5; j++) {
            try {

                Document doc = Jsoup.connect("https://www.evanscycles.com/searchresults?descriptionfilter=bikes#dcp=" + j + "&dppp=60&OrderBy=rank").get();
                //  Document doc = Jsoup.connect(s).get();

                //section where bikes are layed out
                Elements allSection = doc.select(".s-productthumbbox");

                System.out.println(allSection.size());

                for (int i = 0; i <= 5; i++) {

                    //div for the individual product
                    Elements eachSection = allSection.get(i).select(".s-productthumbbox");

                    //brand name
                    Elements brandName = eachSection.select(".brandWrapTitle");

                    //product name
                    Elements productName = eachSection.select(".productdescriptionname");

                    //product price
                    Elements productPrice = eachSection.select(".s-largered");

// ...
//                try {
//                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cst3130_cw1","root","");
//                    if (connection != null && !connection.isClosed()) {
//                        System.out.println("Connection to the database is successful.");
//
//                        // Perform database operations here if the connection is successful
//                        // Update the INSERT statement to use placeholders
//                        String insertQuery = "INSERT INTO bikes (brandName, name, price) VALUES (?, ?, ?)";
//                        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
//
//                        // Set the scraped data as values for the placeholders in the INSERT statement
//                        preparedStatement.setString(1, brandName.text());
//                        preparedStatement.setString(2, productName.text());
//                        preparedStatement.setString(3, productPrice.text());
//                        preparedStatement.executeUpdate();
//                        preparedStatement.close();
//
//                        connection.close(); // Close the connection when done
//                    } else {
//                        System.out.println("Failed to make a connection to the database.");
//                    }
//                } catch (SQLException e) {
//                    // Handle SQL exceptions appropriately
//                    System.out.println("Connection to the database failed. Error message: " + e.getMessage());
//                }
// ...
                    System.out.println("Cycle name: " + productName.text() + " from " + brandName.text() + " Priced at: " + productPrice.text());
                    System.out.println("");
                }

            } catch (IOException exc) {
                System.out.println("Could'nt get the data");

            }
        }//End of loop of 64 available pages 
    }
}
