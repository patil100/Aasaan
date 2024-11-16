
package Scripts;
import Functions.ElementEnable;
import Read_Write_Files.ReadFromCSV;
import Read_Write_Files.ReadFromXlsFile;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.google.gson.Gson;
import io.qameta.allure.*;
import org.apache.commons.io.FileUtils;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;


//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;



import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import net.lingala.zip4j.ZipFile;
import org.testng.asserts.SoftAssert;

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;
import java.util.concurrent.TimeUnit;
import org.bson.Document;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static Functions.ClickElement.*;
import static Functions.Driver.driverAllocation;
import static Functions.ScrollToView.*;
import static Functions.SelectRandomFile.createRandomNum;
import static Functions.UploadFile.uploadByXpathRobo;
import static Read_Write_Files.WriteToCSV.*;
import static Reports.AllureReport.*;
import static Functions.CreateNameByTimestamp.*;
import static org.apache.poi.hssf.record.FtPioGrbitSubRecord.length;
public class Admin_portal {

	 static WebDriver driver;
	    static WebDriverWait wait;
	    SoftAssert softAssert = new SoftAssert();

	    @BeforeTest
	    public void DriverAllocation() throws IOException {
//	        FileUtils.cleanDirectory(Paths.get(System.getProperty("user.dir"), "\\downloadFiles").toFile());
	        driver = driverAllocation("chrome");  //Allocates the driver
	        DelPreviousReport();  //Deletes previous allure report
	        wait = new WebDriverWait(driver, 40);
	    }

	    @Step("Opening Safexpay website")
	    public void openUrl(String url, String message) {
	        saveTextLog(message);
	        String URL = url;
	        driver.get(URL);  //Opening the link
	        saveTextLog("Opening URL: " + URL);  //Saving log for allure report
	        Screenshot(driver, "Opening URL");  //Saving Screenshot for allure report

	    }
	    
	   String Activation_pending;
	   String UPCOMING_PAYMENT;
	   String DORMANT;
	   String UNINSTALLED;
	   
	   @Test(priority = 0,enabled = true)
	   
	    public void merchant_management () throws InterruptedException {
	    	openUrl("https://admin.aasaancheckout.com/", "Admin portal open");
	    	Thread.sleep(5000);
	    	sendKeysByXpath(driver,"//*[@id=\"root\"]/div/div[2]/div/div[2]/div[2]/input","swapnilp@safexpay.com");  
	        Thread.sleep(1000);
	        Screenshot(driver, "Admin user name"); 
	        sendKeysByXpath(driver,"//*[@id=\"root\"]/div/div[2]/div/div[2]/div[3]/input","Test@123");  
	        Thread.sleep(1000);
	        Screenshot(driver, "Admin password "); 
	        
	        waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div[2]/div/div[2]/div[4]/div[2]/button"); 
	        //click on submit button
	    	saveTextLog("click on submit button");
	        Thread.sleep(1000);
	        Screenshot(driver, "merchant management "); 
	       
	        waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div/div[2]/div[2]/div/div[3]/div[1]/div[2]");  //click on activation pending button
	        saveTextLog("click on activation pending  button");
	        Thread.sleep(1000);
	        Screenshot(driver, "activation pending button ");
	        Activation_pending=driver.findElement(By.xpath(" //*[@id=\"root\"]/div/div/div[2]/div[2]/div/div[3]/div[1]/div[2]/div[1]")).getText();
	      System.out.println(Activation_pending);
	      Thread.sleep(1000);
	    
	      
	      waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div/div[2]/div[2]/div/div[3]/div[2]");  //click on UPCOMING PAYMENT button
	        saveTextLog("click on UPCOMING PAYMENT button");
	        Thread.sleep(1000);
	        Screenshot(driver, "UPCOMING PAYMENT");
	        UPCOMING_PAYMENT=driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div[2]/div/div[3]/div[2]/div[2]/div[1]")).getText();
	      System.out.println(UPCOMING_PAYMENT);
	      
	      waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div/div[2]/div[2]/div/div[3]/div[3]");  //click on uninsttaled button
	        saveTextLog("click on UNINSTALLED button");
	        Thread.sleep(1000);
	        Screenshot(driver, "UNINSTALLED");
	        UNINSTALLED=driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div[2]/div/div[3]/div[3]/div[2]/div[1]")).getText();
	      System.out.println("UNINSTALLED");
	  
	      waitAndClickByXpath(driver, "  //*[@id=\"root\"]/div/div/div[2]/div[2]/div/div[3]/div[4]");  //click on DORMANT  button
	        saveTextLog("click on DORMANT button");
	        Thread.sleep(1000);
	        Screenshot(driver, "DORMANT");
	        DORMANT=driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div[2]/div/div[3]/div[4]/div[2]/div[1]")).getText();
	      System.out.println("DORMANT");
	   
	   
	   }
	    
	    }


	  

