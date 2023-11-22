/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exampleCode;



import java.io.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;


/** Example code showing how you can use CSS selectors with JSoup.  */
public class Examples {
    
    /** You may need to adjust the path to the HTML files to get this example running. */
    private String htmlFilePath = "src/html/";
    
    
    /** Constructor */
    Examples(){
        try{
            //jSoup Exercises
            exercise1();
            exercise2();
         
        }
        catch(Exception ex){
            System.out.println("Exercise Exception: " + ex.getMessage());
        }
    }
    
    
    /** Extracts some simple test data from example1.html */
    private void exercise1() throws Exception{
        File input = new File(htmlFilePath + "example1.html");
        Document doc = Jsoup.parse(input, "UTF-8", "");
            
        //Use CSS selectors to extract element with ID 'div1'
        Elements div1 = doc.select("#div1");
        System.out.println("CSS selector. div1 contents: " + div1.text());
        
        //Use DOM method to get element with ID 'div2'
        Element div2 = doc.getElementById("div2");
        System.out.println("DOM method. div2 contents: " + div2.text());
    }
    
    
    /** Extracts part and all of the football results from example2.html. */
    private void exercise2() throws Exception{
        File input = new File(htmlFilePath + "example2.html");
        Document doc = Jsoup.parse(input, "UTF-8", "");
            
        //Select elements with class football-info
        Elements h1 = doc.select("h1.football-info");
        System.out.println("h1.football-info contents: " + h1.text());
        
        //Select all h1 tags
        Elements h1Elements = doc.select("h1");
        for(int i=0; i<h1Elements.size(); ++i)
            System.out.println("h1 contents " + i + ": " + h1Elements.get(i).text());
    }
    
    
    /** Extracts current weather conditions from example3.html. */
    private void exercise3() throws Exception{
        File input = new File(htmlFilePath + "example3.html");
        Document doc = Jsoup.parse(input, "UTF-8", "");
            
        //First select all elements with the class 'weather'
        Elements weathElem = doc.select(".weather");
        Elements curWeathElem = weathElem.select(".current");
        Elements curWeathCondElem = curWeathElem.select(".conditions");
        
        //Then select all elements with the class 'current'
        System.out.println("Current weather conditions: " + curWeathCondElem.text());
    }

}
