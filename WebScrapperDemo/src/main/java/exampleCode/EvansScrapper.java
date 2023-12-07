package exampleCode;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class EvansScrapper {

    EvansScrapper() {
        ScrapeEvans();
    }

    public static void ScrapeEvans() {
        for (int j = 1; j <= 5; j++) {
            try {

                Document doc = Jsoup.connect("https://www.evanscycles.com/searchresults?descriptionfilter=bikes#dcp="
                        + j + "&dppp=60&OrderBy=rank").get();
                // Document doc = Jsoup.connect(s).get();

                // section where bikes are layed out
                Elements allSection = doc.select(".s-productthumbbox");

                System.out.println(allSection.size());

                for (int i = 0; i <= 5; i++) {

                    // div for the individual product
                    Elements eachSection = allSection.get(i).select(".s-productthumbbox");

                    // brand name
                    Elements brandName = eachSection.select(".brandWrapTitle");

                    // product name
                    Elements productName = eachSection.select(".productdescriptionname");

                    // product price
                    Elements productPrice = eachSection.select(".s-largered");
                    System.out.println("Cycle name: " + productName.text() + " from " + brandName.text()
                            + " Priced at: " + productPrice.text());
                    System.out.println("");
                }

            } catch (IOException exc) {
                System.out.println("Could'nt get the data");

            }
        } // End of loop of 64 available pages
    }
}
