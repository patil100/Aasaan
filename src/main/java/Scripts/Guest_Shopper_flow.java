
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
public class Guest_Shopper_flow {

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
	    String orderid;
	   @Test(priority = 1,enabled = false)
	   public void shoppage() throws InterruptedException {
	    	openUrl("https://wordpress4.aasaancheckout.com/shop/", "Aasaan open");
	    	waitAndClickByXpath(driver, "//*[@id=\"main\"]/ul/li[3]/div/div/div/a"); 
	    	waitAndClickByXpath(driver, "//*[@id=\"main\"]/ul/li[3]/div/div/div/a[2]");
	    	waitAndClickByXpath(driver, "//*[@id=\"post-7\"]/div/div/div[2]/div/a/img");                                                              
	    	
	    	 saveTextLog("shopper details page ");
	        Thread.sleep(2000);
	        Screenshot(driver, "shopper details page ");  //Saving Screenshot for allure report
	        WebElement frame=driver.findElement(By.xpath("//*[@id=\"print_frame\"]"));
	        driver.switchTo().frame(frame);
	        Thread.sleep(4000);
	        
	        
	        Random random=new Random();
	        int randomNumber=0;
	        String mobileNoStr ="";
	        boolean loop=true;
	        while(loop) {
	            randomNumber=random.nextInt();
	            if(Integer.toString(randomNumber).length()==8 && !Integer.toString(randomNumber).startsWith("9")) {
	                loop=false;
	            }
	        }
	        mobileNoStr = "98"+randomNumber;
	        driver.findElement(By.id("billing_phone")).click();
	        driver.findElement(By.id("billing_phone")).clear();
	        driver.findElement(By.id("billing_phone")).sendKeys(mobileNoStr);
	        Thread.sleep(5000);
	        saveTextLog("Enter Mobile Number:"+ mobileNoStr);
	        
	        sendKeysByXpath(driver,"//*[@id=\"billing_phone\"]","9921001212");  //Mobile Number added
	        Thread.sleep(1000);
	        saveTextLog("Enter MOBILE NUMBER-9921001212");
	        sendKeysByXpath(driver,"//*[@id=\"billing_email\"]","spxx@gmail.com");  //Email id enter 
	        Thread.sleep(1000);
	        saveTextLog("Enter Email id -spxx@gmail.com\"");
	        sendKeysByXpath(driver,"//*[@id=\"billing_first_name\"]","amar");  //Name  enter 
	        Thread.sleep(1000);
	        saveTextLog("Enter Name-amar");
	        sendKeysByXpath(driver,"//*[@id=\"billing_address_1\"]","thane");  //Address  enter 
	        Thread.sleep(1000);
	        saveTextLog("Enter Address-thane");
	        sendKeysByXpath(driver,"//*[@id=\"billing_address_2\"]","wagle ");  //Landmark  enter 
	        Thread.sleep(1000);
	        saveTextLog("Enter Land mark-wagle");
	        sendKeysByXpath(driver,"//*[@id=\"billing_postcode\"]","416311 ");  //Pin  enter  state and city wil auto 
	        Thread.sleep(8000);
	        saveTextLog("Enter PIN Code -416311");
	        
	        waitAndClickByXpath(driver, "//*[@id=\"wizard\"]/div[3]/ul/li[2]/a"); 
	        //Click on Continue button
	    	saveTextLog("Click on Continue button");
	        Thread.sleep(1000);
	        
	        waitAndClickByXpath(driver, "//*[@id=\"shipping_method_0_flat_rate5\"]"); 
	        //Click on checkbox 
	    	saveTextLog("Priority Delivery for Rs 5 ; 2-5 days: ₹5.00");
	        Thread.sleep(1000);
	        waitAndClickByXpath(driver, "//*[@id=\"wizard\"]/div[3]/ul/li[2]/a"); 
	        //Click on Continue button
	    	saveTextLog("click on continue button");
	        Thread.sleep(1000);
	        waitAndClickByXpath(driver, "//*[@id=\"payment_method_cod\"]"); 
	        //Click on Continue button
	    	saveTextLog("click on paymode radio button");
	        Thread.sleep(1000);
	        waitAndClickByXpath(driver, "//*[@id=\"create_account\"]"); 
	        //Click on Continue button
	    	saveTextLog("click on Aasaan checkout checkbox  radio button");
	        Thread.sleep(1000);
	        waitAndClickByXpath(driver, "//*[@id=\"aasaan_btn_payment_save\"]/button"); 
	        //Click on pay button
	    	saveTextLog("click on pay  button");
	        Thread.sleep(10000);
	        Screenshot(driver, "Order details page "); 
	       // waitAndClickByXpath(driver, "//*[@id=\"cfw-totals-list\"]/div/button"); 
	        //Click Continue to more shopping 
	    	//saveTextLog("Click Continue to more shopping ");
	       //Thread.sleep(1000);
	    
	    		   orderid = driver.findElement(By.xpath("//*[@id=\"ac_billing_fields\"]/p[1]/span")).getText();
	    		   System.out.println(orderid);
	    		   //driver.close();
	    		  // merchant();
	    		  }
	 
	  
	   
	   @Test(priority = 2,enabled = false)
	   
	    public void merchant() throws InterruptedException {
	    	openUrl("https://aasaanmerchant.aasaancheckout.com/", "merchant portal open");
	    	Thread.sleep(8000);
	    	sendKeysByXpath(driver,"//*[@id=\"root\"]/div/div[2]/div/div/div[2]/div[3]/input","7620787374");  //Mobile Number added
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

	        waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div[2]/div[2]/div[1]/div/ul/li[2]/a"); 
	        //Click on order tab
	    	saveTextLog("Click on order tab");
	        Thread.sleep(1000);
	        Screenshot(driver, "Click on order tab"); 
	        
	        waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div[2]/div[2]/div[2]/div/div[3]/div[2]"); 
	        //Click on order tab
	    	saveTextLog("Click on Detailed View tab");
	        Thread.sleep(1000);
	        Screenshot(driver, "Click on Detailed View tab"); 
	        waitAndClickByXpath(driver, "//*[@id=\"search\"]/option[2]"); 
	        
	        //Click on order tab
	        waitAndClickByXpath(driver, "//*[@id=\"search\"]"); 
	    	saveTextLog("Click on order tab");
	        Thread.sleep(6000);
	        Screenshot(driver, "Click on order tab"); 
	        //Select  on order tab
	        waitAndClickByXpath(driver, "//*[@id=\"search\"]/option[2]"); 
	    	saveTextLog("Select dropdown option order id");
	        Thread.sleep(6000);
	        Screenshot(driver, "Select dropdown option order id"); 
	        
	        //waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div[2]/div[2]/div[2]/div/div[4]/div[1]/div[1]/div/div[2]/div/div/div/input"); 
	        sendKeysByXpath(driver,"//*[@id=\"root\"]/div/div[2]/div[2]/div[2]/div/div[4]/div[1]/div[1]/div/div[2]/div/div/div/input",orderid);  //enter order id
	        Thread.sleep(8000);
	        Screenshot(driver, "Enter order id ");
	        
	        waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div[2]/div[2]/div[2]/div/div[4]/div[1]/div[1]/div/div[2]/div/div/div[2]/ul/li"); 
	        //select order id
	    	saveTextLog("select order id");
	        Thread.sleep(8000);
	        Screenshot(driver, "select order id"); 
	        
	        waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div[2]/div[2]/div[2]/div/div[4]/div[1]/div[1]/div/div[3]/button"); 
	        //Click on search button
	    	saveTextLog("Click on search button");
	        Thread.sleep(7000);
	        Screenshot(driver, "Click on search button"); 
	        
	      
	    }
	   @Test(priority = 3,enabled = true)
	   public void Admin() throws InterruptedException, AWTException {
		   openUrl("https://aasaanadmin.aasaancheckout.com/", "merchant portal open");
	    	Thread.sleep(5000);
	    	
	    	 sendKeysByXpath(driver,"//*[@id=\"root\"]/div/div[2]/div/div[2]/div[2]/input","swapnilp@safexpay.com");  //Enter username
	    	 saveTextLog("Enter user name -swapnilp@safexpay.com");
		        Thread.sleep(1000);
		       // Screenshot(driver, "Enter user name -swapnilp@safexpay.com"); 
		        sendKeysByXpath(driver,"//*[@id=\"root\"]/div/div[2]/div/div[2]/div[3]/input","Test@123");  //Enter passwaord
		        saveTextLog("Enter password -Test@123");
		        Thread.sleep(1000);
		       
		        waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div[2]/div/div[2]/div[4]/div[2]/button"); 
		        //Click on search button
		    	saveTextLog("Click on submit button");
		        Thread.sleep(1000);
		    
		        waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div/div[1]/div[2]/div/a[3]/a/div"); 
		        //Click on order management tab
		    	saveTextLog("Click on order management tab");
		        Thread.sleep(2000);
		       
		        scrollToViewXpath(driver, "//*[@id=\"root\"]/div/div/div[2]/div[2]/div/div[4]/div[2]/div[1]/div[1]/select");
		        Thread.sleep(3000);
		        
		        waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div/div[2]/div[2]/div/div[4]/div[3]/div/div/a"); 
		        Thread.sleep(6000);
		        waitAndClickByXpath(driver, "//*[@id=\"root\"]/div/div/div[2]/div[2]/div/div[4]/div[2]/div[1]/div[1]/select/option[2]"); 
		        //Click on order management tab
		    	saveTextLog("select dropdown option order id");
		        Thread.sleep(3000);
		     
		        sendKeysByXpath(driver,"//*[@id=\"root\"]/div/div/div[2]/div[2]/div/div[4]/div[2]/div[1]/div[2]/div[1]/div/div/div/input",orderid);  //Mobile Number added
		        Thread.sleep(5000);
		        saveTextLog("Enter order id");
		      //*[@id="root"]/div/div/div[2]/div[2]/div/div[4]/div[2]/div[1]/div[2]/div[1]/div/div/div[2]/ul/li/div
		        waitAndClickByXpath(driver,"//*[@id=\"root\"]/div/div/div[2]/div[2]/div/div[4]/div[2]/div[1]/div[2]/div[1]/div/div/div[2]/ul/li/div\r\n");  //Mobile Number added
		        Thread.sleep(5000);		        saveTextLog("select order id");
//		        Robot robot = new Robot();
//		        robot.keyPress(KeyEvent.VK_DOWN);//
//		        Thread.sleep(10000);
//		        robot.keyPress(KeyEvent.VK_ENTER);
//		        saveTextLog("AI SELECTED");
//		        robot.keyPress(KeyEvent.VK_ENTER);
//		        saveTextLog("Order ID  SELECTED");
		        
		        waitAndClickByXpath(driver, "/html/body/div/div/div/div[2]/div[2]/div/div[4]/div[2]/div[1]/div[2]/div[2]/button"); 
		     	saveTextLog("Click on search button");
		        Thread.sleep(2000);
		       
		      
	   }
	    
	    }


	  

