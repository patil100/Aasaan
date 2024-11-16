
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
public class Merchant_portal {

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
	    
	   String Conversion_Rate;
	   String  New_Shopper;
	   String  Average_Order_Value;
	   String  Prepaid_Order;
	   String Repeat_shopper;
	   @Test(priority = 0,enabled = true)
	   
	    public void merchant() throws InterruptedException {
	    	openUrl("https://aasaanmerchant.aasaancheckout.com/", "merchant portal open");
	    	Thread.sleep(5000);
	    	sendKeysByXpath(driver,"//*[@id=\"root\"]/div/div[2]/div/div/div[2]/div[3]/input","9921415238");  //Mobile Number added
	        Thread.sleep(1000);
	        Screenshot(driver, "Merchant login number"); 
	        waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div[2]/div/div/div[2]/div[4]/div/button"); 
	        //click on next button
	    	saveTextLog("click on next button");
	        Thread.sleep(1000);
	        
	        sendKeysByXpath(driver,"//*[@id=\"first\"]","6");  //Enter ! OTP 
	        Thread.sleep(1000);
	        Screenshot(driver, "OTP 1 st Digit "); 
	      
	        sendKeysByXpath(driver,"//*[@id=\"second\"]","5");  //Enter 2 OTP 
	        Thread.sleep(1000);
	        Screenshot(driver, "OTP 2 st Digit "); 
	        
	        sendKeysByXpath(driver,"//*[@id=\"third\"]","4");  //Enter 3 OTP 
	        Thread.sleep(1000);
	        Screenshot(driver, "OTP 3 st Digit "); 
	        sendKeysByXpath(driver,"//*[@id=\"fourth\"]","3");  //Enter 4 OTP 
	        Thread.sleep(1000);
	        Screenshot(driver, "OTP 4 st Digit "); 
	        sendKeysByXpath(driver,"//*[@id=\"fifth\"]","2");  //Enter 5 OTP 
	        Thread.sleep(1000);
	        Screenshot(driver, "OTP 5 st Digit "); 
	        sendKeysByXpath(driver,"//*[@id=\"sixth\"]","1");  //Enter 6 OTP 
	        Thread.sleep(1000);
	        Screenshot(driver, "OTP 6 st Digit "); 
	    


	    /*    Bson filter = eq("mobile_no", "9921415238");
	        Bson sort = eq("created_on", -1L);


	        MongoClient mongoClient = new MongoClient(
	                new MongoClientURI(
	                        "mongodb://swapnilp:Safex%402022@10.50.3.193:27018/?authSource=AASAAN&readPreference=primary&ssl=false&directConnection=true")
	        );

	        MongoDatabase database = mongoClient.getDatabase("Aasaan");
	        MongoCollection<Document> collection = database.getCollection("OTP_LOG");

	        Iterator<Document> result = collection.find(filter)
	                .sort(sort)
	                .limit((int)1L).iterator();

	        // for integer otp
	         // String otp = String.format("%d", result.next().get("otp", Integer.class));

	        // for string otp
	        String otp = String.format("%s", result.next().get("otp", String.class));
	        System.out.println("OTP : "+otp);

	        sendKeysByXpath(driver, "//*[@id=\"first\"]", otp.charAt(0)+"");
	        sendKeysByXpath(driver, "//*[@id=\"second\"]]", otp.charAt(1)+"");
	        sendKeysByXpath(driver, "//*[@id=\"third\"]", otp.charAt(2)+"");
	        sendKeysByXpath(driver, "//*[@id=\"fourth\"]", otp.charAt(3)+"");
	        sendKeysByXpath(driver, "//*[@id=\"fifth\"]", otp.charAt(4)+"");
	        sendKeysByXpath(driver, "//*[@id=\"sixth\"]", otp.charAt(5)+"");

	        */
	        waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div[2]/div/div/div[2]/div[4]/div/button"); 
	        saveTextLog("Click on next button");
	        Screenshot(driver, "Click on next button");  //Saving Screenshot for allure report
	        Thread.sleep(6000);
	      
	        Conversion_Rate =driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div[2]/div/div[3]/div[1]/div[2]")).getText();
	        System.out.println(Conversion_Rate);
	        saveTextLog("Conversion Rate");
	        New_Shopper =driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div[2]/div/div[3]/div[2]/div[2]")).getText();
	        System.out.println( New_Shopper);
	        saveTextLog(" New_Shopper");
	        Average_Order_Value =driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div[2]/div/div[3]/div[3]/div[2]")).getText();
	        System.out.println(Average_Order_Value);
	        saveTextLog("Average_Order_Value");
	        Prepaid_Order=driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div[2]/div/div[3]/div[4]/div[2]")).getText();
	        System.out.println( Prepaid_Order);
	        saveTextLog(" Prepaid_Order");
	        Repeat_shopper=driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div[2]/div/div[3]/div[5]/div[2]")).getText();
	        System.out.println( Repeat_shopper);
	        saveTextLog("Repeat_shopper");
	        
	        waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div[2]/div[2]/div[2]/div/div[2]/div[2]/div[2]/div[2]"); 
	        saveTextLog("Click on week date range button");
	        Screenshot(driver, "Click on week date range  button");  //Saving Screenshot for allure report
	        Thread.sleep(6000);
	        waitAndClickByXpath(driver, " //*[@id=\"root\"]/div/div[2]/div[2]/div[2]/div/div[2]/div[2]/div[2]/div[3]"); 
	        saveTextLog("Click on month date range button");
	        Screenshot(driver, "Click on month  date range  button");  //Saving Screenshot for allure report
	        Thread.sleep(6000);
	        waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div[2]/div[2]/div[2]/div/div[2]/div[2]/div[2]/div[5]"); 
	        saveTextLog("Click on lifetime date range button");
	        Screenshot(driver, "Click on lifetime  date range  button");  //Saving Screenshot for allure report
	        Thread.sleep(6000);
	        /*waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div[2]/div[2]/div[2]/div/div[2]/div[2]/div[2]/div[4]"); 
	        saveTextLog("Click on custom date range button");
	        Screenshot(driver, "Click on custom  date range  button");  //Saving Screenshot for allure report
	        Thread.sleep(6000);
	        waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div[2]/div[2]/div[2]/div/div[5]/div[1]/div[2]/div[3]/div[1]/div[3]/button[28]/span[2]/span/text()"); 
	        saveTextLog("Click on select custom date range button");
	        Screenshot(driver, "Click on  select custom  date range  button");  //Saving Screenshot for allure report
	        */Thread.sleep(6000);
	      
	        waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div[2]/div[2]/div[2]/div/div[4]/div[1]/div[2]/div[2]"); 
	        saveTextLog("Click on count button");
	        Screenshot(driver, "Click on count  button");  //Saving Screenshot for allure report
	        Thread.sleep(6000);
	        
	        waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div[2]/div[1]/div[2]/div[1]/div[2]"); 
	        saveTextLog("Click on safexpay_PG button");
	        Screenshot(driver, "Click on safexpay_PG button");  //Saving Screenshot for allure report
	        Thread.sleep(6000);
	      
	    
	      
	   }
	    
	    }


	  

