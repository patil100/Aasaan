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
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static Functions.ClickElement.*;
import static Functions.ClickElement.clickByXpath;
import static Functions.Driver.driverAllocation;
import static Functions.ScrollToView.*;
import static Functions.SelectRandomFile.createRandomNum;
import static Functions.UploadFile.uploadByXpathRobo;
import static Read_Write_Files.WriteToCSV.*;
import static Reports.AllureReport.*;
import static Functions.CreateNameByTimestamp.*;
import static org.apache.poi.hssf.record.FtPioGrbitSubRecord.length;

public class AdminFlow {
    static WebDriver driver;
    static WebDriverWait wait;
    SoftAssert softAssert = new SoftAssert();

    @BeforeTest
    public void DriverAllocation() throws IOException {
//        FileUtils.cleanDirectory(Paths.get(System.getProperty("user.dir"), "\\downloadFiles").toFile());
        driver = driverAllocation("chrome");  //Allocates the driver
        DelPreviousReport();  //Deletes previous allure report
        wait = new WebDriverWait(driver, 30);
    }

    @Step("Opening Safexpay website")
    public void openUrl(String url, String message) {
        saveTextLog(message);
        String URL = url;
        driver.get(URL);  //Opening the link
        saveTextLog("Opening URL: " + URL);  //Saving log for allure report
        Screenshot(driver, "Opening URL");  //Saving Screenshot for allure report

    }
    

    @Step("Logging in")

    public void login(String username, String password) throws InterruptedException {

        saveTextLog("Username: " + username);
        Thread.sleep(8000);
        Screenshot(driver, "Username");  //Saving Screenshot for allure report

        saveTextLog("Password: " + password);
        Thread.sleep(8000);
        Screenshot(driver, "Password");  //Saving Screenshot for allure report


        sendKeysByXpath(driver, "//input[@id='mui-1']", username);  //Typing username into the box
        clickByXpath(driver, "//body/div[@id='__next']/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/form[1]/div[2]/button[1]");
        sendKeysByXpath(driver, "//input[@id='mui-3']", password);  //Typing password into the box
        clickByXpath(driver, "//body/div[@id='__next']/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/form[1]/div[3]/button[2]");
        saveTextLog("Submit button clicked");
        Thread.sleep(10000);


        Bson filter = eq("mobile_no", "9773809761");
        Bson sort = eq("created_on", -1L);


        MongoClient mongoClient = new MongoClient(
                new MongoClientURI(
                        "mongodb://AppUser:AppUs%24r%232022@10.20.0.79:27018/?authSource=BBPS&readPreference=primary&appname=MongoDB%20Compass&directConnection=true&ssl=false")
        );

        MongoDatabase database = mongoClient.getDatabase("BBPS");
        MongoCollection<Document> collection = database.getCollection("OTP_LOG");

        Iterator<Document> result = collection.find(filter)
                .sort(sort)
                .limit((int)1L).iterator();

        // for integer otp
         // String otp = String.format("%d", result.next().get("otp", Integer.class));

        // for string otp
        String otp = String.format("%s", result.next().get("otp", String.class));
        System.out.println("OTP : "+otp);

        sendKeysByXpath(driver, "//input[@id='otp_0']", otp.charAt(0)+"");
        sendKeysByXpath(driver, "//input[@id='otp_1']", otp.charAt(1)+"");
        sendKeysByXpath(driver, "//input[@id='otp_2']", otp.charAt(2)+"");
        sendKeysByXpath(driver, "//input[@id='otp_3']", otp.charAt(3)+"");
        saveTextLog("Entered OTP");
        Screenshot(driver, "Entered OTP");  //Saving Screenshot for allure report

        clickByXpath(driver, "//*[@id=\"__next\"]/div/div[2]/div/div/div/div[2]/div[2]/button[2]");
        saveTextLog("Submit OTP button clicked");


        waitForElementXpath(driver, "//*[@id=\"__next\"]/div[2]/div/ul/a[1]/li/div/div[2]/span");
        Thread.sleep(15000);
        Screenshot(driver, "Login Successful");  //Saving Screenshot for allure report
    }


    //------------------------Transaction Flow Creation------------------------------
    String[] dataCreateMerchant;

    @Test(priority = 0, description = "BBPS Creation Flow")
    @Severity(SeverityLevel.CRITICAL)
    @Description("BBPS Creation Flow")
    public void createMerchant() throws Exception {
        boolean failCase = false;
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\BBPS_Credentials.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to BBPS COU");
        login(credential[1], credential[3]);


        dataCreateMerchant = new String[10];
        openCreateMerchant();
        String allSessionsWritePath = System.getProperty("user.dir") + "\\Output_Files\\Create_AI_All_Sessions.csv";
        String currentSessionWritePath = System.getProperty("user.dir") + "\\Output_Files\\Create_AI_Last_Session.csv";
        deleteContentsOfCsv(currentSessionWritePath);

        String businessDetailsCsvPath = System.getProperty("user.dir") + "\\Configuration_Files\\Create_Merchant_Data\\Business_Details.csv";
        ReadFromCSV csvBusiness = new ReadFromCSV(businessDetailsCsvPath);
        String[] dataBusiness;

        String pricingDetailsCsvPath = System.getProperty("user.dir") + "\\Configuration_Files\\Create_Merchant_Data\\Pricing_Details.csv";
        ReadFromCSV csvPricing = new ReadFromCSV(pricingDetailsCsvPath);
        String[] dataPricing;
        for (int i = 1; i < csvBusiness.SizeOfFile(); i++) {
            dataBusiness = csvBusiness.ReadLineNumber(i);  //Reading data from csv

            for (int j = 1; j < csvPricing.SizeOfFile(); j++) {
                try {
                    dataPricing = csvPricing.ReadLineNumber(j);//Reading data from csv
                    createMerchantFormFill(dataBusiness, dataPricing);

                    initializeCsvWriter(allSessionsWritePath);
                    writeNextLineCsv(dataCreateMerchant);

                    initializeCsvWriter(currentSessionWritePath);
                    writeNextLineCsv(dataCreateMerchant);

                    waitForElementXpath(driver, "/html/body/div[1]/div/div/div/div[4]/div/div[1]/form/div[1]/div[2]/div/input");
                    Thread.sleep(10000);
                } catch (Exception e) {
                    driver.navigate().refresh();
                    waitForPageToLoad(driver);
                    waitForElementXpath(driver, "/html/body/div[1]/div/div/div/div[4]/div/div[1]/form/div[1]/div[2]/div/input");
                    Thread.sleep(10000);
                    softAssert.fail();
                    failCase = true;
                }
            }
        }
        if (failCase) {
            Assert.fail("Create AI Failed");
        }
    }

    //------------------------Create AI & Agent Management Clicked------------------------------

    @Step("Opening Create AI & Agent Management")
    public void openCreateMerchant() throws Exception {


        waitAndClickByXpath(driver, "//*[@id=\"__next\"]/div[2]/div/ul/a[4]/li/div");  //Clicks Merchant Management
        saveTextLog("Clicked  AI & Agent Management");//Clicks AI & Agent Management
        Thread.sleep(10000);
        Screenshot(driver, "AI & Agent Management");  //Saving Screenshot for allure report

        waitAndClickByXpath(driver, "//body/div[@id='__next']/div[1]/div[1]/div[1]/div[1]/div[2]/button[2]");  //Clicks All  Merchant
        saveTextLog("Clicked Onboard");//Clicks   Onboard
        Thread.sleep(10000);
        Screenshot(driver, "Clicked Onboard");  //Saving Screenshot for allure report


        waitAndClickByXpath(driver, "//body/div[4]/div[3]/ul[1]/li[1]");  //Clicks   Agent Institute
        Thread.sleep(10000);
        saveTextLog("Clicked Agent Institute");//Clicks    Agent Institute
        Thread.sleep(10000);
        Screenshot(driver, "Clicked Agent Institute");  //Saving Screenshot for allure report
        //@Step("FILL AI CREATION DATA")\

        driver.findElement(By.xpath("//*[@id=\"aiType\"]/label[1]/span[1]")).click();
        Thread.sleep(5000);
        saveTextLog("Clicked Bank radio Button");//Clicks    Agent Institute
        driver.findElement(By.xpath("//div[@id='aiType']/label/span[2]")).click();
        Thread.sleep(5000);


       String testNumber=getTimestampShort();
        String testName="";
        //String description="";
        testName+="BBPS AI"+testNumber;  //Make Unique name
        driver.findElement(By.id("aiName")).click();
        driver.findElement(By.id("aiName")).clear();
        sendKeysById(driver,"aiName", testName);  //Enter Merchant
        saveTextLog("New AI Name: "+testName);
        dataCreateMerchant[0]=testName;
        saveTextLog("Enter AI name ");//Clicks    Agent Institute


      driver.findElement(By.id("aiAliasName")).click();
        driver.findElement(By.id("aiAliasName")).clear();
        driver.findElement(By.id("aiAliasName")).sendKeys("ABHISHEK");
        saveTextLog("Enter AI Alias name ");//Clicks    Agent Institute
        Thread.sleep(5000);


        driver.findElement(By.id("typeOfBusiness")).click();
        driver.findElement(By.id("typeOfBusiness-option-0")).click();
        saveTextLog("Select Business Type");//Clicks    Agent Institute
        Thread.sleep(5000);
        driver.findElement(By.id("isParticipatingOnline")).click();
        driver.findElement(By.id("isParticipatingOnline-option-0")).click();
        Thread.sleep(5000);
        saveTextLog("Select Is Participating Online?");//Clicks    Agent Institute

        scrollToViewXpath(driver, "//*[@id=\"companyTANNumber\"]");
        driver.findElement(By.id("companyTANNumber")).click();
        driver.findElement(By.id("companyTANNumber")).clear();
        driver.findElement(By.id("companyTANNumber")).sendKeys("BQGP58454F");
        Thread.sleep(5000);
        saveTextLog("Enter Company TAN Number");//Clicks    Agent Institute

        scrollToViewXpath(driver, "//*[@id=\"uAAadhaarNumber\"]");
        Thread.sleep(5000);

        waitAndClickByXpath(driver, "//input[@id='uAAadhaarNumber']");  //Clicks All  Merchant
        sendKeysByXpath(driver, "//input[@id='uAAadhaarNumber']", "212112121221");//Merchant Name
        Thread.sleep(5000);
        saveTextLog("Enter Udyog Aadhaar Number");//Clicks    Agent Institute

        scrollToViewXpath(driver, "//*[@id=\"rOCIN\"]");

        driver.findElement(By.id("paymentChannel")).click();
        driver.findElement(By.id("paymentChannel-option-0")).click();
        Thread.sleep(5000);
        saveTextLog("Select Payment Channel");//Clicks    Agent Institute

        driver.findElement(By.id("rOCIN")).click();
        driver.findElement(By.id("rOCIN")).clear();
        driver.findElement(By.id("rOCIN")).sendKeys("U72200MH2009PLC123456");
        Thread.sleep(5000);
        saveTextLog("Enter ROC IN ");//Clicks    Agent Institute

        scrollToViewXpath(driver, "//*[@id=\"registeredAddress\"]");

        driver.findElement(By.xpath("//div[@id='__next']/div/div/div/form/div/div[2]/div[2]/div[10]/div")).click();
        Thread.sleep(5000);
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Effective from'])[1]/following::*[name()='svg'][1]")).click();
        Thread.sleep(5000);
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='S'])[2]/following::button[01]")).click();
        Thread.sleep(5000);
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Effective To'])[1]/following::*[name()='svg'][1]")).click();
        Thread.sleep(5000);
        driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='S'])[2]/following::button[30]")).click();
        Thread.sleep(5000);
        saveTextLog("select date");//Clicks    Agent Institute

        driver.findElement(By.id("registeredAddress")).click();
        driver.findElement(By.id("registeredAddress")).clear();
        driver.findElement(By.id("registeredAddress")).sendKeys("INIDA");
        Thread.sleep(5000);
        saveTextLog("Enter  Registered Address");//Clicks    Agent Institute

        scrollToViewXpath(driver, " //*[@id=\"pinCode\"]");

        driver.findElement(By.id("state")).click();
        driver.findElement(By.id("state")).clear();
        driver.findElement(By.id("state")).sendKeys("Maharashtra");
        Thread.sleep(5000);
        saveTextLog("Enter  State");//Clicks    Agent Institute


        driver.findElement(By.id("city")).click();
        driver.findElement(By.id("city")).clear();
        driver.findElement(By.id("city")).sendKeys("MUMBAI");
        Thread.sleep(5000);
        saveTextLog("Enter  City");//Clicks    Agent Institute

        driver.findElement(By.id("pinCode")).click();
        driver.findElement(By.id("pinCode")).clear();
        driver.findElement(By.id("pinCode")).sendKeys("400033");
        Thread.sleep(9000);
        saveTextLog("Enter  Pin Code");//Clicks    Agent Institute
        scrollToViewXpath(driver, " //*[@id=\"flcLastName\"]");


        driver.findElement(By.id("communicationAddress")).click();
        driver.findElement(By.id("communicationAddress-option-0")).click();
        driver.findElement(By.id("communicationAddressRegistered")).click();
        driver.findElement(By.id("communicationAddressCity")).click();
        Thread.sleep(9000);


//-------------------------------1ST Level Communication----------------
        scrollToViewXpath(driver, " //*[@id=\"flcDepartment\"]");
        Thread.sleep(9000);

        driver.findElement(By.id("flcFisrtName")).click();
        driver.findElement(By.id("flcFisrtName")).clear();
        driver.findElement(By.id("flcFisrtName")).sendKeys("abhishek");
        Thread.sleep(9000);
        saveTextLog("Enter  First Name");//Clicks    Agent Institute
        driver.findElement(By.id("flcLastName")).click();
        driver.findElement(By.id("flcLastName")).clear();
        driver.findElement(By.id("flcLastName")).sendKeys("gole");
        Thread.sleep(9000);
        saveTextLog("Enter  Last Name");//Clicks    Agent Institute
        driver.findElement(By.id("flcDesignation")).clear();
        driver.findElement(By.id("flcDesignation")).sendKeys("QA");
        Thread.sleep(9000);
        saveTextLog("Enter  Designation Name");//Clicks    Agent Institute
        driver.findElement(By.id("flcDepartment")).click();
        driver.findElement(By.id("flcDepartment")).clear();
        driver.findElement(By.id("flcDepartment")).sendKeys("it");
        Thread.sleep(9000);
        saveTextLog("Enter  Department Name");//Clicks    Agent Institute
        Thread.sleep(9000);
        scrollToViewXpath(driver, " //*[@id=\"flcEmailId\"]");


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
        driver.findElement(By.id("flcMobileNo")).click();
        driver.findElement(By.id("flcMobileNo")).clear();
        driver.findElement(By.id("flcMobileNo")).sendKeys(mobileNoStr);
        Thread.sleep(5000);
        saveTextLog("Enter Mobile Number:"+ mobileNoStr);//Clicks    Agent Institute

        String allowedChars = "abcdefghijklmnopqrstuvwxyz" + "1234567890" + ".";
        String email = "";
        String temp = RandomStringUtils.random(20, allowedChars);
        email = temp.substring(0, temp.length() - 9) + "@testdata.com";

        driver.findElement(By.id("flcEmailId")).click();
        driver.findElement(By.id("flcEmailId")).clear();
        driver.findElement(By.id("flcEmailId")).sendKeys(email);
        Thread.sleep(5000);
        saveTextLog("Enter Email ID:" +email);//Clicks    Agent Institute



//-------------------------------2nd Level Communication----------------
        WebElement firstname2=driver.findElement(By.xpath("//*[@id=\"slcFisrtName\"]"));
        firstname2.sendKeys("priyanka");
        Thread.sleep(5000);
        saveTextLog("Enter  First Name");//Clicks    Agent Institute


        WebElement Lastname2=driver.findElement(By.xpath("//*[@id=\"slcLastName\"]"));
        Lastname2.sendKeys("waje");
        Thread.sleep(5000);
        saveTextLog("Enter  Last Name");//Clicks    Agent Institute

        WebElement Degisnation2=driver.findElement(By.xpath("//*[@id=\"slcDesignation\"]"));
        Degisnation2.sendKeys("QA");
        Thread.sleep(5000);
        saveTextLog("Enter  Degisnation");//Clicks    Agent Institute


        WebElement Department2=driver.findElement(By.xpath("//*[@id=\"slcDepartment\"]"));
        Department2.sendKeys("IT");
        Thread.sleep(5000);
        saveTextLog("Enter  Department");//Clicks    Agent Institute


        Random randoms=new Random();
        randomNumber = 0;
        mobileNoStr = "";
        loop = true;
        while(loop) {
            randomNumber=random.nextInt();
            if(Integer.toString(randomNumber).length()==8 && !Integer.toString(randomNumber).startsWith("9")) {
                loop=false;
            }
        }
        mobileNoStr = "88"+randomNumber;

        WebElement mobile2=driver.findElement(By.xpath("//*[@id=\"slcMobileNo\"]"));
        mobile2.sendKeys(mobileNoStr);
        Thread.sleep(5000);
        saveTextLog("Enter Mobile Number:"+ mobileNoStr);//Clicks    Agent Institute

        allowedChars = "abcdefghijklmnopqrstuvwxyz" + "1234567890" + ".";
        email = "";
        temp = RandomStringUtils.random(20, allowedChars);
        email = temp.substring(0, temp.length() - 9) + "@testdata.com";

        WebElement email2=driver.findElement(By.xpath("//*[@id=\"slcEmailId\"]"));
        email2.sendKeys(email);
        Thread.sleep(8000);
        saveTextLog("Enter Email ID:" +email);//Clicks    Agent Institute

        scrollToViewXpath(driver, "/html[1]/body[1]/div[1]/div[1]/div[1]/div[1]/form[1]/div[1]/div[4]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/input[1]");

        WebElement upload_file = driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/div[1]/div[1]/form[1]/div[1]/div[4]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/input[1]"));
        Thread.sleep(9000);
        upload_file.sendKeys("D:/Test Abhishek/Abhishek Gole/PG 2.0/BBPS/BBPS AUTOMATION/Configuration_Files/BBPS_Files/CanceledCheck.png");
        Thread.sleep(5000);
        saveTextLog("Business Authorization Letter document uploaded");

        WebElement upload_file1 = driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/div[1]/div[1]/form[1]/div[1]/div[4]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/input[1]"));
        Thread.sleep(9000);
        upload_file1.sendKeys("D:/Test Abhishek/Abhishek Gole/PG 2.0/BBPS/BBPS AUTOMATION/Configuration_Files/BBPS_Files/CanceledCheck.png");
        Thread.sleep(5000);
        saveTextLog("Licence to Business Letter document uploaded");

        WebElement upload_file2 = driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/div[1]/div[1]/form[1]/div[1]/div[4]/div[1]/div[3]/div[1]/div[1]/div[1]/div[1]/input[1]"));
        Thread.sleep(9000);
        upload_file2.sendKeys("D:/Test Abhishek/Abhishek Gole/PG 2.0/BBPS/BBPS AUTOMATION/Configuration_Files/BBPS_Files/CanceledCheck.png");
        Thread.sleep(5000);
        saveTextLog("Residential Address Proof document uploaded");

        /*scrollToViewXpath(driver, "//button[contains(text(),'Submit')]");*/

        WebElement upload_file4 = driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/div[1]/div[1]/form[1]/div[1]/div[4]/div[1]/div[4]/div[1]/div[1]/div[1]/div[1]/input[1]"));
        Thread.sleep(9000);
        upload_file4.sendKeys("D:/Test Abhishek/Abhishek Gole/PG 2.0/BBPS/BBPS AUTOMATION/Configuration_Files/BBPS_Files/CanceledCheck.png");
        Thread.sleep(5000);
        saveTextLog("Aadhaar Card document uploaded");

        WebElement upload_file5 = driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/div[1]/div[1]/form[1]/div[1]/div[4]/div[1]/div[5]/div[1]/div[1]/div[1]/div[1]/input[1]"));
        Thread.sleep(9000);
        upload_file5.sendKeys("D:/Test Abhishek/Abhishek Gole/PG 2.0/BBPS/BBPS AUTOMATION/Configuration_Files/BBPS_Files/CanceledCheck.png");
        Thread.sleep(5000);
        saveTextLog("Voter ID Card document uploaded");

        WebElement upload_file6 = driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/div[1]/div[1]/form[1]/div[1]/div[4]/div[1]/div[6]/div[1]/div[1]/div[1]/div[1]/input[1]"));
        Thread.sleep(9000);
        upload_file6.sendKeys("D:/Test Abhishek/Abhishek Gole/PG 2.0/BBPS/BBPS AUTOMATION/Configuration_Files/BBPS_Files/CanceledCheck.png");
        Thread.sleep(5000);
        saveTextLog("Passport document uploaded");

        waitAndClickByXpath(driver, "//button[contains(text(),'Submit')]");  //Clicks All  Merchant
        Thread.sleep(10000);
        Screenshot(driver, "AI CREATED");  //Saving Screenshot for allure report


//------------------------------AI Activation---------------




        waitForElementXpath(driver, " //*[@id=\"__next\"]/div[1]/div/div[1]/div/div[2]/button[1]");
        Thread.sleep(10000);
        saveTextLog("Landing On AI dash board");

        waitAndClickByXpath(driver, "/html[1]/body[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[2]/table[1]/tbody[1]/tr[1]/td[7]/div[1]/div[1]/p[1]/span[1]");  //Clicks All  Merchant
        Thread.sleep(6000);
        saveTextLog("Click On 3 dot");

        waitAndClickByXpath(driver, "//body/div[@id='long-menu']/div[3]/ul[1]/li[1]");  //Clicks All  Merchant
        Thread.sleep(10000);
        saveTextLog("Click On Activate");

        sendKeysByXpath(driver,"//input[@id='bbpsTerminalId']","2345");  //Mobile Number added
        //Clicks All  Merchant
        Thread.sleep(9000);
        saveTextLog("Enter BBPS Terminal ID");

        sendKeysByXpath(driver,"//input[@id='walletMaxLimit']","123456");  //Mobile Number added
        //Clicks All  Merchant
        Thread.sleep(9000);
        saveTextLog("Enter Wallet Max Limit");

        driver.findElement(By.id("thresholdLimitLogic")).click();
            driver.findElement(By.id("thresholdLimitLogic-option-0")).click();
        Thread.sleep(9000);
        saveTextLog("Select Threshold limit logic");


        sendKeysByXpath(driver,"//input[@id='walletThresholdLimit']","10%");  //Mobile Number added
        //Clicks All  Merchant
        Thread.sleep(5000);
        saveTextLog("Enter Wallet Max Limit");


        driver.findElement(By.id("selectedVABank")).click();
        driver.findElement(By.id("selectedVABank-option-0")).click();
        Thread.sleep(9000);
        saveTextLog("Select Select Bank for VA");

        sendKeysByXpath(driver,"//input[@id='accountNumber1']","121212121212");  //Mobile Number added
        //Clicks All  Merchant
        Thread.sleep(9000);
        saveTextLog("Enter Account Number 1");

        sendKeysByXpath(driver,"//input[@id='ifscNumber1']","ICIC0001231");  //Mobile Number added
        //Clicks All  Merchant
        Thread.sleep(9000);
        saveTextLog("Enter Wallet Max Limit");

        scrollToViewXpath(driver, "//body/div[@id='__next']/div[1]/div[1]/div[1]/form[1]/div[1]/form[1]/div[5]/button[1]");

        waitAndClickByXpath(driver, "//*[@id=\"aiActivate\"]/div[5]/button[2]");  //Clicks All  Merchant
        Thread.sleep(10000);
        saveTextLog("Click On Submit");

    }

    //------------------------Filling all forms to create AI------------------------------
    public void createMerchantFormFill(String[] data, String[] paymentModes) throws Exception {





        Thread.sleep(5000);


        String testNumber=getTimestampShort();
        String testName="";
        String description="";
        if(data[0].toLowerCase().contains("aggregator")) {
            testName += "Agg Hosted";
            description+="Aggregator Hosted";
        }
        else if(data[0].toLowerCase().contains("js")) {
            testName += "JS Checkout";
            description+="JS Checkout";
        }

        testName+=" - "+testNumber;  //Make Unique name
        driver.findElement(By.id("aiName")).click();
        driver.findElement(By.id("aiName")).clear();
        sendKeysById(driver,"aiName", testName);  //Enter Merchant
        saveTextLog("New AI Name: "+testName);
        dataCreateMerchant[0]=testName;

        scrollToViewXpath(driver, "//*[@id=\"__next\"]/div[1]/div/div/form/div/div[2]/div[2]/div[3]/div");
        Thread.sleep(5000);

        scrollToCenterXpath(driver, "//input[@id='pincode']");
        driver.findElement(By.id("pincode")).click();
        driver.findElement(By.id("pincode")).clear();
        driver.findElement(By.id("pincode")).sendKeys("400033");
        saveTextLog("Company PINCODE ");
        Thread.sleep(5000);

        driver.findElement(By.id("countrycodename")).click();
        driver.findElement(By.id("countrycodename-option-0")).click();
        Thread.sleep(2000);
        saveTextLog("SELECT COUNTRY AS INDIA ");
        Thread.sleep(5000);

        driver.findElement(By.id("state-select-demo132rd12s")).click();
        driver.findElement(By.id("state-select-demo132rd12s-option-20")).click();
       // driver.findElement(By.id("state-select-demo132rd12s")).clear();
        //driver.findElement(By.id("state-select-demo132rd12s")).sendKeys("Maharashtra");
        saveTextLog("SELECT STATE AS Maharashtra  ");
        Thread.sleep(5000);


        scrollToCenterXpath(driver, "//*[@id=\"sub_category\"]");

       /* driver.findElement(By.id("gstin_number")).click();
        driver.findElement(By.id("gstin_number")).clear();
        driver.findElement(By.id("gstin_number")).sendKeys("2700AAS02010210");
        saveTextLog("ENTER GSTIN NUMBER");
        Thread.sleep(2000);*/

        driver.findElement(By.id("gstin_address")).click();
        driver.findElement(By.id("gstin_address")).clear();
        driver.findElement(By.id("gstin_address")).sendKeys("thane");
        saveTextLog("ENTER GSTIN Address");
        Thread.sleep(2000);

        driver.findElement(By.id("sub_category-select-demo132rds")).click();
        driver.findElement(By.id("sub_category-select-demo132rds-option-0")).click();
        saveTextLog("SELECT Business Category");
        Thread.sleep(5000);

        driver.findElement(By.id("sub_category")).click();
        driver.findElement(By.id("sub_category")).clear();
        driver.findElement(By.id("sub_category")).sendKeys("test");
        saveTextLog("ENTER Business Sub-Category");
        Thread.sleep(2000);


        scrollToCenterXpath(driver, "//*[@id=\"mcc_code\"]");
        driver.findElement(By.name("business_description")).click();
        driver.findElement(By.name("business_description")).clear();
        driver.findElement(By.name("business_description")).sendKeys("test1234");
        saveTextLog("ENTER Business Description");
        Thread.sleep(2000);



        driver.findElement(By.id("merchantType-select-demo132rds")).click();
        driver.findElement(By.id("merchantType-select-demo132rds-option-0")).click();
        //driver.findElement(By.id("state-select-demo132rd12s")).sendKeys("Maharashtra");
        saveTextLog("SELECT Merchant Type  AS Delivery versus Payment  ");
        Thread.sleep(5000);



            driver.findElement(By.id("integration-select-demo132rds")).click();
            driver.findElement(By.id("integration-select-demo132rds-option-0")).click();
            saveTextLog("SELECT Aggregator Hosted Integration Type");
            Thread.sleep(5000);
            dataCreateMerchant[1]="Aggregator Hosted";


        scrollToCenterXpath(driver, "//input[@id='mcc_code']");
        driver.findElement(By.id("mcc_code")).click();
        driver.findElement(By.id("mcc_code")).clear();
        driver.findElement(By.id("mcc_code")).sendKeys("123");
        saveTextLog("ENTER MCC Code");
        Thread.sleep(5000);



        driver.findElement(By.id("turnover")).click();
        driver.findElement(By.id("turnover")).clear();
        driver.findElement(By.id("turnover")).sendKeys("123456");
        saveTextLog("ENTER turnover");
        Thread.sleep(5000);

        driver.findElement(By.id("average_ticket_size")).click();
        driver.findElement(By.id("average_ticket_size")).clear();
        driver.findElement(By.id("average_ticket_size")).sendKeys("1111");
        saveTextLog("ENTER Average Ticket size");
        Thread.sleep(5000);

        scrollToCenterXpath(driver, "//input[@id='director_name']");
        driver.findElement(By.id("director_name")).click();
        driver.findElement(By.id("director_name")).clear();
        driver.findElement(By.id("director_name")).sendKeys("abhishek");
        Thread.sleep(5000);
        saveTextLog("ENTER DIRECTOR NAME");



        driver.findElement(By.id("mui-2")).click();
        driver.findElement(By.id("mui-2")).clear();
        driver.findElement(By.id("mui-2")).sendKeys("abhishekg@safexpay.com");
        Thread.sleep(2000);
        Thread.sleep(5000);
        saveTextLog("ENTER EMAIL ID");


        driver.findElement(By.id("mui-3")).click();
        driver.findElement(By.id("mui-3")).clear();
        driver.findElement(By.id("mui-3")).sendKeys("9773809761");
        Thread.sleep(5000);
        saveTextLog("ENTER DIRECTOR NUMBER");


        driver.findElement(By.id("pan_number")).click();
        driver.findElement(By.id("pan_number")).clear();
        driver.findElement(By.id("pan_number")).sendKeys("BQGPG8454F");
        Thread.sleep(5000);
        saveTextLog("ENTER PAN NUMBER ");


        driver.findElement(By.xpath("//button[@type='submit']")).click();
        Thread.sleep(10000);
        saveTextLog("Clicked on Save and continue and Business details saved successfully ");
        //Thread.sleep(2000);

//-----------------Other Details--------

        //Thread.sleep(3000);

        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,-150)", "");
        saveTextLog("Scroll Up");
        //waitAndClickByXpath(driver, "//button[contains(text(),'Merchant Details')]");  //Clicks  Merchant Details
        Thread.sleep(10000);

        //clickByXpath(driver, "//button[normalize-space()='Select All']");
        //clickByXpath(driver ,"/html/body/div[1]/div/main/div[2]/div[2]/div[1]/div/div/button[1]");
        //Thread.sleep(10000);
        //scrollToCenterXpath(driver, "//input[@id='rolling_reserve_percent']");

        //driver.findElement(By.xpath("//span[contains(text(),'Select All')]")).click();

       /* driver.findElement(By.id("//*[@id=\"mui-10\"]\n")).click();
        driver.findElement(By.id("//*[@id=\"mui-10\"]\n")).clear();
        driver.findElement(By.id("//*[@id=\"mui-10\"]\n")).sendKeys("https://uatportal.safexpay.com/");
        Thread.sleep(5000);

        //driver.findElement(By.name("same_day_settlement")).click();
        Thread.sleep(5000);


        driver.findElement(By.name("rolling_reserve_flag")).click();
        driver.findElement(By.id("rolling_reserve_percent")).click();
        driver.findElement(By.id("rolling_reserve_percent")).clear();
        driver.findElement(By.id("rolling_reserve_percent")).sendKeys("10");
        Thread.sleep(5000);*/

        scrollToCenterXpath(driver, "//*[@id=\"__next\"]/div/main/div[2]/div[3]/div[2]/form/div/div/div[6]/div/textarea[1]");
        driver.findElement(By.name("remarks")).click();
        driver.findElement(By.name("remarks")).clear();
        driver.findElement(By.name("remarks")).sendKeys("test");
        Thread.sleep(10000);

        driver.findElement(By.xpath("//button[@type='submit']")).click();
        // clickByXpath(driver, "//button[normalize-space()='Save & Continue']");
        Thread.sleep(5000);
        saveTextLog("Clicked on Save and continue and Feature details Save Successfully ");
        saveTextLog("Other details save ");
//-----------------User Details--------

        Thread.sleep(5000);
        driver.findElement(By.xpath("//button[normalize-space()='Same as Director 1 details']")).click();
        Thread.sleep(5000);

        String userName="";
        sendKeysByXpath(driver,"/html[1]/body[1]/div[1]/div[1]/main[1]/div[2]/div[3]/div[2]/form[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/input[1]",userName);  //First name added
        saveTextLog("First Name Added: "+userName);
        dataCreateMerchant[2]="abhishek gole";
        String lastName=getRandomString();

        String MobileNumber="";
        sendKeysByXpath(driver,"/html[1]/body[1]/div[1]/div[1]/main[1]/div[2]/div[3]/div[2]/form[1]/div[1]/div[3]/div[1]/div[1]/div[1]/div[1]/input[1]",MobileNumber);  //Mobile Number added
        saveTextLog("Mobile Number Added: 9773809761");
        dataCreateMerchant[3]="9773809761";
        Thread.sleep(2000);


        driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/main[1]/div[2]/div[3]/div[2]/form[1]/div[1]/div[2]/div[2]/div[1]/div[1]/div[1]/input[1]")).click();//Email Added
        saveTextLog("Email Added: abhishekg@safexpay.com");
        dataCreateMerchant[4]="abhishekg@safexpay.com";
        Thread.sleep(2000);

        driver.findElement(By.xpath("//button[@type='submit']")).click();
        Thread.sleep(10000);
        saveTextLog("User details save ");






        //-----------------Bank Details--------

        Thread.sleep(10000);


        scrollToCenterXpath(driver, "//*[@id=\"panel1a-content\"]/div/div[2]/div[1]/div[2]/button");
        driver.findElement(By.xpath("//*[@id=\'mui-7\']")).click();
        WebElement acconut_number = driver.findElement(By.xpath("//*[@id=\'mui-7\']"));
        Integer aa = new Random().nextInt(900000000) + 1000000000;
        acconut_number.sendKeys(aa.toString());
        saveTextLog("Account number added>>>"+aa.toString());
        Thread.sleep(5000);


        driver.findElement(By.xpath("//*[@id=\"mui-8\"]")).click();
        driver.findElement(By.xpath("//*[@id=\"mui-8\"]")).clear();
        driver.findElement(By.xpath("//*[@id=\"mui-8\"]")).sendKeys("ICIC0001234");
        saveTextLog("IFSC code added");
        Thread.sleep(5000);
        driver.findElement(By.xpath("//*[@id=\"panel1a-content\"]/div/div[2]/div[1]/div[2]/button")).click();
        saveTextLog("Verify bank");
        Thread.sleep(10000);

        driver.findElement(By.xpath("//*[@id=\"__next\"]/div/main/div[2]/div[3]/div[2]/form/div[2]/div[2]/div/button")).click();
        Thread.sleep(15000);
        saveTextLog("Bank details save ");

        //-----------------Paymode Details--------



        driver.findElement(By.xpath("//body/div[@id='__next']/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[1]/label[1]/span[1]/input[1]")).click();
        Thread.sleep(15000);
        driver.findElement(By.xpath("//body/div[@id='__next']/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[2]/div[1]/button[1]")).click();
        Thread.sleep(15000);
        //  driver.findElement(By.xpath("//div[@id='__next']/div/main/div[2]/div[3]/div[2]/div/div/div/div/div")).click();
        saveTextLog("Paymode details save ");
        Thread.sleep(10000);

//-----------------Paypode Configuration--------



       /* driver.findElement(By.xpath("//body/div[@id='__next']/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]")).click();
        driver.findElement(By.xpath("//body/div[@id='__next']/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/label[1]/span[1]/input[1]")).click();
        Thread.sleep(5000);
        saveTextLog("Airtel UPI details save ");
        driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/input[1]")).click();
        driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/input[1]")).clear();
        driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/input[1]")).sendKeys("123");
        Thread.sleep(5000);
        saveTextLog("Airtel UPI details save ");
        saveTextLog("UPI details save ");

        driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]")).click();
        Thread.sleep(5000);
        saveTextLog("WALLET   SELECTED ");

        driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[1]/div[2]/div[1]/div[5]/div[1]/div[1]/label[1]/span[1]/input[1]")).click();
        Thread.sleep(5000);
        saveTextLog("OLA MONEY WALLET   SELECTED ");

        driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[1]/div[2]/div[1]/div[5]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/input[1]")).click();
        driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[1]/div[2]/div[1]/div[5]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/input[1]")).clear();
        driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[1]/div[2]/div[1]/div[5]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/input[1]")).sendKeys("123");
        Thread.sleep(10000);
        saveTextLog("Wallet details save ");

        JavascriptExecutor js6 = (JavascriptExecutor) driver;
        js6.executeScript("window.scrollBy(0,-100)", "");
        saveTextLog("Scroll up");
        Thread.sleep(700);

        driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[1]/div[1]/div[3]/div[1]")).click();
        Thread.sleep(5000);
        saveTextLog("CC  SELECTED ");

        driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/button[1]")).click();
        driver.manage().timeouts().implicitlyWait(30,TimeUnit.SECONDS);
        saveTextLog("ADD  SCHEME ");

        driver.findElement(By.xpath("/html[1]/body[1]/div[4]/div[3]/div[1]/div[2]/div[1]/div[2]/div[2]/div[2]/div[1]/div[1]/label[1]/span[1]/input[1]")).click();
        Thread.sleep(5000);
        driver.findElement(By.xpath("/html[1]/body[1]/div[4]/div[3]/div[1]/div[2]/div[1]/div[2]/div[2]/div[2]/div[1]/div[1]")).click();
        Thread.sleep(5000);
        saveTextLog("SELECT VISA  SCHEME ");


        scrollToCenterXpath(driver, "/html[1]/body[1]/div[4]/div[3]/div[1]/div[2]/div[1]/div[2]/div[2]/div[2]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/label[1]/span[1]/input[1]\n");

        driver.findElement(By.xpath("/html[1]/body[1]/div[4]/div[3]/div[1]/div[2]/div[1]/div[2]/div[2]/div[2]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/label[1]/span[1]/input[1]\n")).click();
        Thread.sleep(5000);
        saveTextLog("SELECT ATOM   PG ");

        driver.findElement(By.xpath("/html[1]/body[1]/div[4]/div[3]/div[1]/div[2]/div[1]/div[2]/div[2]/div[3]/div[1]/div[1]/label[1]/span[1]/input[1]")).click();
        Thread.sleep(5000);

        driver.findElement(By.xpath("/html[1]/body[1]/div[4]/div[3]/div[1]/div[2]/div[1]/div[2]/div[2]/div[3]/div[1]/div[1]")).click();
        Thread.sleep(5000);
        saveTextLog("SELECT MASTERCARD  SCHEME");

        scrollToCenterXpath(driver, "/html[1]/body[1]/div[4]/div[3]/div[1]/div[2]/div[1]/div[2]/div[2]/div[3]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/label[1]/span[1]/input[1]");

        driver.findElement(By.xpath("/html[1]/body[1]/div[4]/div[3]/div[1]/div[2]/div[1]/div[2]/div[2]/div[3]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/label[1]/span[1]/input[1]")).click();
        Thread.sleep(5000);
        saveTextLog("SELECT ATOM   PG ");
        driver.findElement(By.xpath(" /html[1]/body[1]/div[4]/div[3]/div[1]/div[3]/button[1]")).click();
        Thread.sleep(5000);
        saveTextLog("CC details save ");*/


        JavascriptExecutor js9 = (JavascriptExecutor) driver;
        js9.executeScript("window.scrollBy(0,-150)", "");
        saveTextLog("Scroll up");
        Thread.sleep(700);


        driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[1]/div[1]/div[4]/div[1]")).click();
        Thread.sleep(10000);
        saveTextLog("SELECT NB ");


        driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/button[1]")).click();
        Thread.sleep(10000);
        saveTextLog("ADD BANK");

        driver.findElement(By.xpath("//*[@id=\"mui-9\"]")).sendKeys("Test Bank");
        Thread.sleep(10000);
        driver.findElement(By.xpath("/html/body/div[4]/div[3]/div/div[2]/div/div[2]/div[2]/div/div[1]/div/label/span[2]")).click();
        Thread.sleep(10000);
        saveTextLog("SELECT TEST BANK");

        driver.findElement(By.xpath("/html[1]/body[1]/div[4]/div[3]/div[1]/div[3]/button[1]")).click();
        Thread.sleep(10000);
        saveTextLog("NB details save ");


       /* driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[1]/div[1]/div[5]/div[1]")).click();
        Thread.sleep(5000);
        saveTextLog("CC  SELECTED ");

        JavascriptExecutor js4 = (JavascriptExecutor) driver;
        js4.executeScript("window.scrollBy(0,-200)", "");
        saveTextLog("Scroll Up");
        driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/button[1]")).click();
        Thread.sleep(5000);
        saveTextLog("ADD  SCHEME ");


        driver.findElement(By.xpath("/html[1]/body[1]/div[4]/div[3]/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[1]/div[1]/label[1]/span[1]/input[1]")).click();
        Thread.sleep(5000);
        driver.findElement(By.xpath("/html[1]/body[1]/div[4]/div[3]/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[1]/div[1]")).click();
        Thread.sleep(5000);
        saveTextLog("SELECT VISA SCHEME ");


        scrollToCenterXpath(driver, "/html[1]/body[1]/div[4]/div[3]/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/label[1]/span[1]/input[1]");

        driver.findElement(By.xpath(" /html[1]/body[1]/div[4]/div[3]/div[1]/div[2]/div[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/label[1]/span[1]/input[1]")).click();
        Thread.sleep(5000);
        saveTextLog("SELECT ATOM PG ");

        driver.findElement(By.xpath("/html[1]/body[1]/div[4]/div[3]/div[1]/div[2]/div[1]/div[2]/div[2]/div[2]/div[1]/div[1]/label[1]/span[1]/input[1]")).click();
        Thread.sleep(5000);
        driver.findElement(By.xpath("/html[1]/body[1]/div[4]/div[3]/div[1]/div[2]/div[1]/div[2]/div[2]/div[2]/div[1]/div[1]")).click();
        Thread.sleep(5000);
        saveTextLog("SELECT MASTERCARD SCHEME");

        scrollToCenterXpath(driver, "/html[1]/body[1]/div[4]/div[3]/div[1]/div[2]/div[1]/div[2]/div[2]/div[2]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/label[1]/span[1]/input[1]");


        driver.findElement(By.xpath("/html[1]/body[1]/div[4]/div[3]/div[1]/div[2]/div[1]/div[2]/div[2]/div[2]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/label[1]/span[1]/input[1]")).click();
        Thread.sleep(5000);
        saveTextLog("SELECT ATOM PG ");
        driver.findElement(By.xpath("/html[1]/body[1]/div[4]/div[3]/div[1]/div[3]/button[1]")).click();
        Thread.sleep(5000);
        saveTextLog("DC details save ");*/

        driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/main[1]/div[2]/div[3]/div[2]/div[1]/div[2]/div[1]/button[1]")).click();
        Thread.sleep(5000);
        saveTextLog("MERCHANT CREATED  DONE");
        Thread.sleep(5000);


        driver.findElement(By.xpath("//*[@id=\"__next\"]/div/div/header/div/div[4]/button")).click();
        Thread.sleep(5000);
        driver.findElement(By.xpath(" //*[@id=\"fade-menu\"]/div[3]/ul")).click();
        Thread.sleep(5000);



    }


    //------------------------Editing merchants created-------------------------------
   /* @Test(priority = 1, description = "Merchant Edit Flow")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Merchant Edit Flow")
    public void editMerchant() throws Exception {
        boolean testFail = false;
        //String path = System.getProperty("user.dir") + "\\Configuration_Files\\Admin_Credentials.csv";  //path to get login details file or credentials file
        //ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        //String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        // System.out.println(Arrays.toString(credential));
        //openUrl(credential[0],"Logging in to maker account");
        //login(credential[1],credential[3]);
        openManageMerchantMaker();
        deleteContentsOfCsv("Output_Files/Merchant_Authorization_Status_Last_Session.csv");
        ReadFromCSV lastRun = new ReadFromCSV(System.getProperty("user.dir") + "\\Output_Files\\Create_Merchant_Last_Session.csv");
        for (int i = 1; i < lastRun.SizeOfFile(); i++) {
            try {
                String[] lastData = lastRun.ReadLineNumber(i);
                editMerchant(lastData);
                Thread.sleep(5000);
            } catch (Exception e) {
                testFail = true;
                softAssert.fail();
            }
        }
        if (testFail) {
            Assert.fail("Edit merchant failed");
        }
    }

    @Step("Opening Manage Merchant")
    public void openManageMerchantMaker() throws InterruptedException {


        // waitAndClickByXpath(driver, "//body/div[@id='__next']/div[1]/nav[1]/div[1]/div[1]/div[1]/nav[1]/div[2]/div[1]\n");  //Clicks Merchant Management
        // saveTextLog("Clicked Merchant Management");//Clicks Merchant Management
        // waitAndClickByXpath(driver, "//html[1]/body[1]/div[1]/div[1]/nav[1]/div[1]/div[1]/div[1]/nav[1]/div[2]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/p[1]");  //Clicks All  Merchant
        // Thread.sleep(10000);
        // saveTextLog("Clicked ALL  Merchant");//Clicks ALL  Merchant
        // Thread.sleep(10000);

        //driver.findElement(By.className("MuiSwitch-thumb css-jsexje-MuiSwitch-thumb")).click();



        waitAndClickByXpath(driver,"//*[@id=\"__next\"]/div/main/div[3]/label/span[1]/span[1]");
        saveTextLog("Clicks On Maker Checker");
        Thread.sleep(10000);



    }

    @Step("Edit Merchant")
    public void editMerchant(String[] merchantData) throws InterruptedException, IOException {
        waitForElementXpath(driver, "//html[1]/body[1]/div[1]/div[1]/nav[1]/div[1]/div[1]/div[1]/nav[1]/div[2]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/p[1]");  //Wait Till Add New   Merchant
        Thread.sleep(1000);

        waitAndClickByXpath(driver, "//html[1]/body[1]/div[1]/div[1]/nav[1]/div[1]/div[1]/div[1]/nav[1]/div[2]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/p[1]");  //Clicks Search  Merchant

        driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/main[1]/div[4]/div[5]/div[1]/div[1]/button[1]")).clear();
        String temp = merchantData[0];
        String stepname = "Editing: " + merchantData[0];
        changeStepName(stepname);
        for (int i = 0; i < merchantData[0].length(); i++) {
            sendKeysByXpath(driver, "//body/div[@id='__next']/div[1]/main[1]/div[4]/div[1]/div[1]/input[1]", Character.toString(temp.charAt(i)));
            Thread.sleep(1000);
        }
        Thread.sleep(10000);


        if (driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div/div/div[4]/div[2]/div/div[2]/table/tbody/tr[2]/td[2]")).getText().equalsIgnoreCase(merchantData[0])) {
            clickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div/div/div[4]/div[2]/div/div[2]/table/tbody/tr[2]/td[13]/button[2]");
            Thread.sleep(5000);
            scrollToCenterXpath(driver, "//*[@id=\"BusinessDetails\"]/form/div[22]/div/div/button");
            Thread.sleep(2000);

            clickByXpath(driver, "//*[@id=\"BusinessDetails\"]/form/div[20]/div[1]/div/div[1]/div/div/label");  //Toggle EPP
            saveTextLog("EPP Turned ON");
            Thread.sleep(500);

            clickByXpath(driver, "//*[@id=\"BusinessDetails\"]/form/div[21]/div/div/div[1]/div/div/label");  //Toggle Refund API
            saveTextLog("Refund API Turned ON");
            Thread.sleep(500);

            clickByXpath(driver, "//*[@id=\"BusinessDetails\"]/form/div[21]/div/div/div[2]/div/div/label"); //Toggle Refund Portal
            saveTextLog("Refund Portal Turned ON");
            Thread.sleep(500);

            clickByXpath(driver, "//*[@id=\"BusinessDetails\"]/form/div[22]/div/div/button");  //Next Button
            saveTextLog("Next button Clicked");
            waitForElementXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div/div/div[2]/p");
            String message = driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div/div/div[2]/p")).getText();
            Screenshot(driver, "SnackBar Message: " + message);
            Thread.sleep(3000);
            clickByXpath(driver, "//*[@id=\"heading-action-wrapper\"]/div/button");  //Back To Merchant Management
            saveTextLog("Back To Merchant Management Clicked");
        } else {
            Screenshot(driver, "Merchant name not available on first index");
        }
    }*/

    //------------------------Authorizing merchants created-------------------------------
    @Test(priority = 2, description = "Checker Authorize Flow")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Merchant Authorization Flow")
    public void checkerAdmin() throws Exception {
        boolean testFail = false;
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\Admin_Credentials.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to checker account");
        login(credential[2], credential[3]);

        openManageMerchantChecker();
        Thread.sleep(5000);
        deleteContentsOfCsv("Output_Files/Merchant_Authorization_Status_Last_Session.csv");
        ReadFromCSV lastRun = new ReadFromCSV(System.getProperty("user.dir") + "\\Output_Files\\Create_Merchant_Last_Session.csv");
        int randomFileToUnauthorize = createRandomNum(1, lastRun.SizeOfFile() - 1);
        for (int i = 1; i < lastRun.SizeOfFile(); i++) {
            String[] lastData = lastRun.ReadLineNumber(i);

            if (i == randomFileToUnauthorize) {
                authorizeMerchant(lastData, false);
                Thread.sleep(5000);
                continue;
            }

            try {
                authorizeMerchant(lastData, true);
            } catch (Exception e) {
                testFail = true;
                softAssert.fail();
            }
            Thread.sleep(5000);
        }
        if (testFail) {
            Assert.fail("Some error in Authorizing merchants");
        }
    }

    @Step("Opening Manage Merchant")
    public void openManageMerchantChecker() throws InterruptedException {
        waitAndClickByXpath(driver, "//body/div[@id='__next']/div[1]/nav[1]/div[1]/div[1]/div[1]/nav[1]/div[2]/div[1]\n");  //Clicks Merchant Management
        saveTextLog("Clicked Merchant Management");//Clicks Merchant Management
        waitAndClickByXpath(driver, "//html[1]/body[1]/div[1]/div[1]/nav[1]/div[1]/div[1]/div[1]/nav[1]/div[2]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/p[1]");  //Clicks All  Merchant
        Thread.sleep(10000);
        saveTextLog("Clicked ALL  Merchant");//Clicks ALL  Merchant
        Thread.sleep(1000);
    }

    @Step("Authorize Merchant")
    public void authorizeMerchant(String[] merchantData, boolean authorizeRandom) throws InterruptedException, IOException {
        waitForElementXpath(driver, "//*[@id=\"__next\"]/div/main/div[4]/div[1]/div/input");
        Thread.sleep(9000);

        waitForElementXpath(driver, "//*[@id=\"__next\"]/div/main/div[4]/div[5]/div/div/button");
        String temp = merchantData[0];
        String stepName = "Authorizing: " + merchantData[0];
        changeStepName(stepName);
        for (int i = 0; i < merchantData[0].length(); i++) {
            sendKeysByXpath(driver, "/html/body/div[1]/div/div/div/div[4]/div[2]/div/div[2]/table/tbody/tr[1]/td[2]/input", Character.toString(temp.charAt(i)));
            Thread.sleep(100);
        }
        Thread.sleep(5000);
        if (driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div/div/div[4]/div[2]/div/div[2]/table/tbody/tr[2]/td[2]")).getText().equalsIgnoreCase(merchantData[0])) {
            clickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div/div/div[4]/div[2]/div/div[2]/table/tbody/tr[2]/td[13]/button[1]");
            Thread.sleep(5000);
            saveTextLog("Authorizing Merchant: " + merchantData[0]);
            String decryptionKey = driver.findElement(By.xpath("/html/body/div[1]/div/div/div/div[3]/div[2]/div/div[1]/form/div[1]/div[2]/div/input")).getAttribute("value");
            saveTextLog("Decryption Key: " + decryptionKey);
            String id = driver.findElement(By.xpath("//*[@id=\"id\"]")).getAttribute("value");
            saveTextLog("Id: " + id);
            Thread.sleep(1000);
            boolean authorized = false;
            if (authorizeRandom) {
                clickByXpath(driver, "/html/body/div[1]/div/div/div/div[3]/div[2]/ul/div/li/select");
                Thread.sleep(1000);
                clickByXpath(driver, "/html/body/div[1]/div/div/div/div[3]/div[2]/ul/div/li/select/option[2]");
                Thread.sleep(2000);

                clickByXpath(driver, "//*[@id=\"resnavtab\"]/div/li/button");

                waitForElementXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div/div/div[2]/p");
                String message = driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div/div/div[2]/p")).getText();
                Screenshot(driver, "Snackr Message: " + message);
                Thread.sleep(2000);
                authorized = true;
            }
            clickByXpath(driver, "//*[@id=\"heading-action-wrapper\"]/div/button");
            String[] newData = new String[10];
            newData[0] = merchantData[0];
            newData[1] = merchantData[1];
            newData[2] = merchantData[2];
            newData[3] = merchantData[3];
            newData[4] = merchantData[7];
            newData[5] = merchantData[8];
            newData[6] = merchantData[9];
            newData[7] = id;
            newData[8] = decryptionKey;
            if (authorized)
                newData[9] = "Yes";
            else newData[9] = "No";
            initializeCsvWriter("Output_Files/Merchant_Authorization_Status_All_Sessions.csv"); // Write Details to File
            writeNextLineCsv(newData);
            initializeCsvWriter("Output_Files/Merchant_Authorization_Status_Last_Session.csv"); // Write Details to File
            writeNextLineCsv(newData);
        } else {
            Screenshot(driver, "Merchant name not available on first index");
        }

    }

    //-----------------User Creation Module------------------
    @Test(priority = 3, description = "User Creation Flow")
    @Severity(SeverityLevel.CRITICAL)
    @Description("User Creation Flow")
    public void createUser() throws Exception {
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\Admin_Credentials.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to maker account");
        login(credential[1], credential[3]);

        ReadFromCSV lastRun = new ReadFromCSV(System.getProperty("user.dir") + "\\Output_Files\\Merchant_Authorization_Status_Last_Session.csv");
        String random_string;
        deleteContentsOfCsv("Output_Files/Create_User_Detail_Last_Session.csv");
        try {
            for (int i = 1; i < lastRun.SizeOfFile(); i++) {
                random_string = getRandomString();
                if (lastRun.ReadLineNumber(i)[9].equalsIgnoreCase("yes")) {
                    String mName = lastRun.ReadLineNumber(i)[0];
                    merchant_userDetails(random_string, mName);
                    Thread.sleep(5000);
                }
            }
            Thread.sleep(2000);
            random_string = getRandomString();
            aggregate_maker(random_string);
            Thread.sleep(2000);
            aggregate_checker(random_string);
            Thread.sleep(2000);
            EditUser();
        } catch (Exception e) {
            Assert.fail("User Creation Failed");
        }
    }

    //------------Entering User Details-----------------
    @Step("Enter Merchant User Details")
    public void merchant_userDetails(String random_strings, String merchantName) throws Exception {
        String[] Merchant_Details_writer = new String[4];
        //---------------------Navigate to User Management-----------------------------
        waitAndClickByXpath(driver, "//*[@id=\"js-side-menu-0\"]"); // user Management
        saveTextLog("Navigate to User Management");
        Thread.sleep(1000);
        //---------------------Navigate to Manage Group-------------------------------
        clickByXpath(driver, "//*[@id=\"js-side-menu-0\"]/ul/li[1]/a");
        saveTextLog("Navigate to Create User");
        Thread.sleep(2000);

        //-------------------------------Merchant Option-------------------------------

        saveTextLog("Creating Merchant User");
        waitAndClickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[1]/div[1]/div/select/option[2]"); // Merchant Option
        Thread.sleep(2000);
        clickByXpath(driver, "/html/body/div[1]/div/form/div[4]/div[2]/div[1]/div[2]/div/div"); // Role Name
        Thread.sleep(2000);
        waitAndClickByXpath(driver, "//*[@id=\"select2-drop\"]/ul/li[4]/div"); // Merchant Admin
        Thread.sleep(2000);
        saveTextLog("Aggeragator  selected");
        Thread.sleep(500);


        //sendKeysByXpath(driver, "//*[@id=\"resellerNameLike\"]", "resetest");  //Enter reseller name
        //saveTextLog("Name Printed");



      /*saveTextLog("Entering Merchant Name Details");
      sendKeysByXpath(driver,"//input[@id='merchantNameLike']","merchantName"+random_strings); // MerchantName
      Merchant_Details_writer[0]="MerchantName"+random_strings;
      clickByXpath(driver,"/html/body/div[1]/div/form/div[4]/div[2]/div[1]/div[3]/div/input");*/


        //--------------------------------Selecting Merchant Name---------------------------
      /*List<WebElement> merchantsList=driver.findElements(By.xpath("//input[@id='merchantNameLike']"));
      for(WebElement element : merchantsList){
          if(element.getAttribute("label").equalsIgnoreCase(merchantName))
          {
              element.click();
              saveTextLog("Merchant selected: "+merchantName);
              Thread.sleep(2000);
          }
      }
        Thread.sleep(2000);*/

        //--------------------------------Enter UserId--------------------------------------------------
        saveTextLog("Entering Merchant User Details");
        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[2]/div[1]/div/input", "merch" + random_strings); // UserId
        Merchant_Details_writer[0] = "merch" + random_strings;
        clickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[2]/div[2]/div/div/label");
        //--------------------------------Enter First Name Last Name and Email--------------------------
        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[3]/div[1]/div/input", "Fname_" + random_strings); // First Name
        Merchant_Details_writer[1] = "Fname_" + random_strings;
        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[3]/div[2]/div/input", "Lname_" + random_strings); // Last name
        Merchant_Details_writer[2] = "Lname_" + random_strings;
        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[4]/div[1]/div/input", "padmawati.taddy@bankfab.com"); // Email
        Merchant_Details_writer[3] = "padmawati.taddy@bankfab.com";
        Thread.sleep(2000);
        initializeCsvWriter("Output_Files/Create_User_Details_All_Sessions.csv"); // Write Details to File
        writeNextLineCsv(Merchant_Details_writer);
        initializeCsvWriter("Output_Files/Create_User_Detail_Last_Session.csv"); // Write Details to File
        writeNextLineCsv(Merchant_Details_writer);
        //--------------------------------Submitting User Merchant Details--------------------------------
        saveTextLog("Submitting Merchant User");
        clickByXpath(driver, "//*[@id=\"heading-action-wrapper\"]/div/div/div[4]");
        Thread.sleep(2000);
        String message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/form/div[2]/p"))).getText();
        saveTextLog(Merchant_Details_writer[0] + " " + message);
        System.out.println(message);
    }

    @Step("Create Aggregate Maker")
    public void aggregate_maker(String random_string) throws InterruptedException, IOException {
        String[] Maker_Details_writer = new String[4];
        clickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[1]/div[1]/div/select/option[2]"); // Aggregate
        Thread.sleep(3000);
        clickByXpath(driver, "/html/body/div[1]/div/form/div[4]/div[2]/div[1]/div[2]/div/div/a"); // Role Name
        waitAndClickByXpath(driver, "//*[@id=\"select2-drop\"]/ul/li[2]/div"); // Maker Option
        Thread.sleep(2000);
        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[2]/div[1]/div/input", "maker" + random_string); // UserId
        Maker_Details_writer[0] = "maker" + random_string;
        clickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[2]/div[2]/div/div/label"); // Is Admin Toggle
        //-----------------------Enter First Name Last Name and Email--------------------------
        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[3]/div[1]/div/input", "Fmaker_" + random_string); // First Name
        Maker_Details_writer[1] = "Fmaker_" + random_string;
        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[3]/div[2]/div/input", "Lmaker_" + random_string); // Last Name
        Maker_Details_writer[2] = "Lmaker" + random_string;
        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[4]/div[1]/div/input", "padmawati.taddy@bankfab.com"); // Email
        Maker_Details_writer[3] = "padmawati.taddy@bankfab.com";
        Thread.sleep(2000);
        initializeCsvWriter("Output_Files/Create_User_Details_All_Sessions.csv"); // Write to File
        writeNextLineCsv(Maker_Details_writer);
        initializeCsvWriter("Output_Files/Create_User_Detail_Last_Session.csv"); // Write Details to File
        writeNextLineCsv(Maker_Details_writer);
        //-------------------------------------Submitting User Maker Details-------------------------------------
        clickByXpath(driver, "//*[@id=\"heading-action-wrapper\"]/div/div/div[4]/button");
        String message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/form/div[2]/p"))).getText();
        saveTextLog(Maker_Details_writer[0] + " " + message);
        System.out.println(message);
    }

    @Step("Create Aggregator Checker")
    public void aggregate_checker(String random_string) throws InterruptedException, IOException {
        String[] Checker_Detail_Writer = new String[4];
        clickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[1]/div[1]/div/select/option[2]"); // Aggregate
        Thread.sleep(2000);
        clickByXpath(driver, "/html/body/div[1]/div/form/div[4]/div[2]/div[1]/div[2]/div/div/a"); // Role Name
        waitAndClickByXpath(driver, "//*[@id=\"select2-drop\"]/ul/li[3]/div");
        Thread.sleep(2000);
        String userId = "check" + random_string;
        Checker_Detail_Writer[0] = userId;
        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[2]/div[1]/div/input", userId); // UserId
        clickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[2]/div[2]/div/div/label"); // Is Admin Toggle
        //-----------------------Enter First Name Last Name and Email--------------------------
        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[3]/div[1]/div/input", "Fchecker_" + random_string); // First Name
        Checker_Detail_Writer[1] = "Fchecker" + random_string;
        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[3]/div[2]/div/input", "Lchecker_" + random_string); // Last Name
        Checker_Detail_Writer[2] = "Lchecker" + random_string;
        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[4]/div[1]/div/input", "padmawati.taddy@bankfab.com"); // Email
        Checker_Detail_Writer[3] = "padmawati.taddy@bankfab.com";
        Thread.sleep(2000);
        initializeCsvWriter("Output_Files/Create_User_Details_All_Sessions.csv"); // Write details to file
        writeNextLineCsv(Checker_Detail_Writer);
        initializeCsvWriter("Output_Files/Create_User_Detail_Last_Session.csv"); // Write Details to File
        writeNextLineCsv(Checker_Detail_Writer);
        //------------------------Submitting User Checker Details-------------------------------
        clickByXpath(driver, "//*[@id=\"heading-action-wrapper\"]/div/div/div[4]/button");
        String message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/form/div[2]/p"))).getText();
        saveTextLog(Checker_Detail_Writer[0] + " " + message);
        System.out.println(message);
    }

    @Step("Edit Users")
    public void EditUser() throws Exception {
        ReadFromCSV read_data = new ReadFromCSV("Output_Files/Create_User_Detail_Last_Session.csv"); // UserIds from file
        String userIds;
//        for(int i=1;i<read_data.SizeOfFile();i++)
        for (int i = 1; i < 2; i++) {
            userIds = read_data.ReadLineNumber(i)[0];
            Thread.sleep(1000);
            sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div/div[2]/div/table/tbody/tr[1]/td[2]/input", userIds); // Search userId
            Thread.sleep(2000);
            clickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div/div[2]/div/table/tbody/tr[2]/td[8]/button[3]/i"); // Delete user
            Alert alert = driver.switchTo().alert(); // alert Delete
            Thread.sleep(2000);
            alert.accept(); // Click on Ok
            String message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/form/div[2]/p"))).getText();
            saveTextLog(userIds + " " + message);
            System.out.println("Delete");
            Thread.sleep(5000);
            clickByXpath(driver, "/html/body/div[1]/div/div/div[2]/div/table/tbody/tr[2]/td[8]/button[5]"); // Reset Password
            alert = driver.switchTo().alert();
            Thread.sleep(2000);
            alert.accept();
            String message1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/form/div[2]/p"))).getText();
            saveTextLog(userIds + ":" + message1);
            Screenshot(driver, "Reset Password");
            System.out.println("Reset Password");
            Thread.sleep(3000);
            clickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div/div[2]/div/table/tbody/tr[2]/td[8]/button[2]/i"); // Edit
            Thread.sleep(1000);
            sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[4]/div[2]/div/input", "9422222222"); // Enter Phone Number
            clickByXpath(driver, "//*[@id=\"heading-action-wrapper\"]/div/div/div[4]/button"); // Submit
            saveTextLog("Edit user" + " " + userIds);
            System.out.println("Edit");
            Screenshot(driver, "User Edit");
            driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div/div[2]/div/table/tbody/tr[1]/td[2]/input")).clear();
        }
    }


    //------------------------EPP----------------------
    @Test(priority = 4, description = "EPP Flow")
    @Severity(SeverityLevel.CRITICAL)
    @Description("EPP Flow")
    public void EPPflow() throws Exception {
        boolean testFail = false;
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\Admin_Credentials.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to Maker account");
        login(credential[1], credential[3]);

        try {
            binUploadFile();
        } catch (Exception e) {
            testFail = true;
            softAssert.fail();
        }
        try {

            merchantEPP();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Screenshot(driver, "");
            saveTextLog("Merchant EPP response is slow");
            testFail = true;
            softAssert.fail(e.getMessage());
        }
        if (testFail) {
            Assert.fail("EPP Failed");
        }
    }

    @Step("Bin Upload File")
    public void binUploadFile() throws AWTException, InterruptedException {
        clickByXpath(driver, "//*[@id=\"js-side-menu-4\"]"); // EPP navigation
        waitAndClickByXpath(driver, "//*[@id=\"js-side-menu-4\"]/ul/li[2]"); // Bin Upload File
        String downloadPath = System.getProperty("user.dir") + "\\downloadFiles";
        File directory = new File(downloadPath);
        int initial_size = directory.list().length;
        //sendKeysByXpath(driver,"//*[@id=\"avantgarde\"]/div[1]/div/div/div/form/div[4]/div/div/div[1]/div/div/button","Configuration_Files/new24emiBinUpload.xlsx");
        waitAndClickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div/div/form/div[4]/div/div/div[3]/div/div/a");
        Thread.sleep(5000);
        if (initial_size == directory.list().length) {
            saveTextLog("File not downloaded");
        } else {
            saveTextLog("Sample File Downloaded");
        }
        Thread.sleep(1000);
        uploadByXpathRobo(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div/div/form/div[4]/div/div/div[1]/div/div/button", System.getProperty("user.dir") + "\\Configuration_Files\\Bin Uploads\\new24emiBinUpload.xlsx");
        Thread.sleep(5000);
        clickByXpath(driver, "//*[@id=\"heading-action-wrapper\"]/div/div/div[2]/button");
        try {
            String message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div/div/form/div[2]/p"))).getText();
            saveTextLog(message);
            Screenshot(driver, "Bin Upload File Successful");
        } catch (Exception e) {
            Screenshot(driver, "BinUploadFailed");
        }
    }

    @Step("Merchant EPP")
    public void merchantEPP() throws Exception {
        boolean testFail = false;
        try {
            waitAndClickByXpath(driver, "//*[@id=\"js-side-menu-4\"]");
            clickByXpath(driver, "//*[@id=\"js-side-menu-4\"]/ul/li[1]/a");  // Navigate Merchant EPP
            String[] name = new String[]{"FAB", "ENDB", "ADCB"};
            ReadFromCSV r = new ReadFromCSV("Output_Files/Merchant_Authorization_Status_Last_Session.csv");

            for (int i = 0; i < name.length; i++) {
                Thread.sleep(10000);
                scrollToViewXpath(driver, "//*[@id=\"s2id_bankName\"]");
                waitAndClickByXpath(driver, "//*[@id=\"s2id_bankName\"]/a");
                sendKeysByXpath(driver, "//*[@id=\"select2-drop\"]/div/input", name[i]); // Input Bank Name
                driver.findElement(By.xpath("//*[@id=\"select2-drop\"]/div/input")).sendKeys(Keys.ENTER);
                saveTextLog("Bank Name: " + name[i]);
                Thread.sleep(4000);
                //--------------------Enter Merchant Names----------------------------
                for (int j = 1; j < r.SizeOfFile(); j++) {
                    Thread.sleep(1000);
                    if (r.ReadLineNumber(j)[9].equalsIgnoreCase("yes")) {   // Check If Merchant is authorised
                        String merchantname = r.ReadLineNumber(j)[0];
                        Thread.sleep(5000);
                        clickWithJavaScriptByXpath(driver, "/html/body/div[1]/div/form/div[4]/div[2]/div[1]/div[2]/div/div/ul"); // Enter Merchant Name
                        for (int k = 0; k < merchantname.length(); k++) {
                            Thread.sleep(50);
                            sendKeysByXpath(driver, "/html/body/div[1]/div/form/div[4]/div[2]/div[1]/div[2]/div/div/ul/li/input", String.valueOf(merchantname.charAt(k)));
                        }
                        saveTextLog("Merchant Name " + merchantname);
                        Thread.sleep(1000);
                        driver.findElement(By.xpath("/html/body/div[1]/div/form/div[4]/div[2]/div[1]/div[2]/div/div/ul/li/input")).sendKeys(Keys.ENTER);
                    }

                    if (j == r.SizeOfFile() - 1) {                                // Enter amount
                        scrollToCenterXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[1]/div[3]/div/input");
                        String amount = Integer.toString(createRandomNum(100, 200));
                        Thread.sleep(2000);
                        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/div[1]/div[3]/div/input", amount);
                        saveTextLog("Amount entered for all Merchants and Bank");
                    }
                }
                Thread.sleep(8000);
                List<WebElement> tenure = driver.findElements(By.xpath("//*[@id=\"noOfMonthsPlan\"]/option")); // Check Tenure
                int index = createRandomNum(1, tenure.size() - 1);
                Thread.sleep(3000);
                tenure.get(index).click();
                saveTextLog("Tenure Month is selected is: " + tenure.get(index).getText());
                String percentageValue = Integer.toString(createRandomNum(1, 5));
                String interestRate = Integer.toString(createRandomNum(5, 15));
                sendKeysByXpath(driver, "//*[@id=\"percValueInput\"]", interestRate); // Enter Interest Rate
                saveTextLog("Interest Rate Entered: " + interestRate);
                Thread.sleep(2000);
                clickByXpath(driver, "//*[@id=\"processingFeeType\"]/option[3]");// Processing Fee type
                saveTextLog("Processing Fee Type is selected as Percentage");
                Thread.sleep(2000);
                sendKeysByXpath(driver, "//*[@id=\"processingFeeValues\"]", percentageValue); // Percentage Value
                saveTextLog("Percentage Value is entered: " + percentageValue);
                Thread.sleep(2000);
                Screenshot(driver, "");
                clickWithJavaScriptByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/button"); // Add Tenure
                saveTextLog("Tenure is added");
                scrollToViewXpath(driver, "//*[@id=\"heading-action-wrapper\"]/div/h1");
                clickWithJavaScriptByXpath(driver, "//*[@id=\"heading-action-wrapper\"]/div/div/div[4]/button");
                Thread.sleep(1000);
                Screenshot(driver, "");
                WebElement flag = waitForTwoElementsByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[1]", "//*[@id=\"avantgarde\"]/div[1]/div/form/div[2]", 30);
                if (flag != null) {
                    if (driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/form/div[2]")).isDisplayed()) {
                        saveTextLog("Success Message: " + driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/form/div[2]/p")).getText());
                    }
                    if (driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/form/div[1]")).isDisplayed()) {
                        saveTextLog("Error Message: " + driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/form/div[1]/p")).getText());
//                        testFail=true;
                    }
                    Screenshot(driver, "");
                } else {
                    System.out.println("EPP Submitted, but not created");
//                    testFail=true;
                }
                driver.navigate().refresh();
                waitAndClickByXpath(driver, "//*[@id=\"js-side-menu-4\"]");
                Thread.sleep(5000);
                clickByXpath(driver, "//*[@id=\"js-side-menu-4\"]/ul/li[1]/a");  // Navigate Merchant EPP
                Thread.sleep(10000);
            }
            Thread.sleep(2000);
            saveTextLog("Merchant EPP added");
            Thread.sleep(5000);


            //ReadFromCSV r = new ReadFromCSV("Output_Files/Merchant_Authorization_Status_Last_Session.csv");
            waitAndClickByXpath(driver, "//*[@id=\"js-side-menu-4\"]");
            clickByXpath(driver, "//*[@id=\"js-side-menu-4\"]/ul/li[1]/a");
            int i = createRandomNum(1, r.SizeOfFile() - 1);
            Thread.sleep(2000);
            String username = r.ReadLineNumber(i)[0];
            //-------------------Merchant Name in Merchant List---------------------------
            if (r.ReadLineNumber(i)[9].equalsIgnoreCase("yes")) {
                sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[2]/div[2]/div/table/tbody/tr[1]/td[2]/input", username);
            } else {
                if (i >= 2)
                    i = createRandomNum(1, i - 1);
                else if (i + 1 <= r.SizeOfFile() - 1)
                    i = createRandomNum(i + 1, r.SizeOfFile() - 1);
                username = r.ReadLineNumber(i)[0];
                sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[2]/div[2]/div/table/tbody/tr[1]/td[2]/input", username);
            }
            Thread.sleep(2000);
            try {
                waitAndClickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[2]/div[2]/div/table/tbody/tr[2]/td[6]/button[3]/i"); // Delete Merchant EPP
            } catch (Exception e) {
                throw new Exception("EPP not created for " + username);
            }
            Thread.sleep(2000);
            System.out.println("Click Delete Button");
            Alert alert = driver.switchTo().alert();
            saveTextLog(alert.getText() + " username: " + username);
            alert.accept();
            Thread.sleep(20000);
            driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div[2]/div[2]/div/table/tbody/tr[1]/td[2]/input")).clear();
            sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[2]/div[2]/div/table/tbody/tr[1]/td[2]/input", username); // Enter Merchant name
            saveTextLog("Merchant name for Edit: " + username);
            Thread.sleep(2000);
            scrollToCenterXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[2]/div[2]/div/table/tbody/tr[2]/td[6]/button[2]");
            clickWithJavaScriptByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[2]/div[2]/div/table/tbody/tr[2]/td[6]/button[2]"); // Merchant EPP Edit
            Thread.sleep(8000);
            List<WebElement> TenureDate;
            TenureDate = driver.findElements(By.xpath("//*[@id=\"tbl_posts_body\"]/tr/td[3]"));
            //----------------------Selecting unique Tenure month in Edit--------------------
            List<String> months = new ArrayList<>();
            for (WebElement w : TenureDate) {
                months.add(w.getText());
                //  System.out.println(months);
            }
            List<String> monthsTenure = Arrays.asList("3", "6", "9", "12", "18", "24", "36", "42", "54");
            // System.out.println("month tenure:"+ monthsTenure);
            int index_tenure;
            while (true) {
                index_tenure = createRandomNum(1, 8);
                if (!months.contains(monthsTenure.get(index_tenure))) {
                    Thread.sleep(2000);
                    String xpath = "//*[@id=\"noOfMonthsPlan\"]/option[@label=\"" + monthsTenure.get(index_tenure) + "\"]";
                    Thread.sleep(4000);
                    List<WebElement> tenure1 = driver.findElements(By.xpath("//*[@id=\"noOfMonthsPlan\"]/option"));
                    tenure1.get(index_tenure).click();
                    break;
                }
            }
            saveTextLog(username + " Edit Tenure Month is selected");
            String percetageValue = Integer.toString(createRandomNum(1, 5));
            String interestRate = Integer.toString(createRandomNum(5, 15));
            sendKeysByXpath(driver, "//*[@id=\"percValueInput\"]", interestRate);
            saveTextLog(username + " Edit Interest Rate is entered");
            Thread.sleep(2000);
            clickByXpath(driver, "//*[@id=\"processingFeeType\"]/option[3]"); // Processing Fee type
            saveTextLog(username + " Edit Processing Fee type is Selected");
            Thread.sleep(2000);
            sendKeysByXpath(driver, "//*[@id=\"processingFeeValues\"]", percetageValue); // Percentage Value
            saveTextLog(username + " Edit Percentage Value is added");
            Thread.sleep(2000);
            Screenshot(driver, "");
            clickWithJavaScriptByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div[2]/button");
            saveTextLog(username + " Edit Add Tenure");
            Thread.sleep(8000);
            List<WebElement> status = driver.findElements(By.xpath("//*[@id=\"heading-action-wrapper\"]/div/div/div[1]/div/select/option"));
            for (WebElement e : status) {
                if (e.getAttribute("value").equalsIgnoreCase("active")) {
                    clickWithJavaScriptByWebElement(driver, e);
                    break;
                }
            }
            //clickWithJavaScriptByXpath(driver,"//*[@id=\"heading-action-wrapper\"]/div/div/div[1]/div/select/option[2]");
            saveTextLog(username + " is Active");
            Thread.sleep(2000);
            Screenshot(driver, "");
            clickWithJavaScriptByXpath(driver, "//*[@id=\"heading-action-wrapper\"]/div/div/div[4]/button");
            saveTextLog(username + " Edit Submit is clicked");
            Thread.sleep(1000);
            Screenshot(driver, "");


        } catch (Exception e) {
            Screenshot(driver, e.getMessage());
            testFail = true;
        } finally {
            if (testFail) {
                throw new Exception("EPP Failed");
            }
        }
    }


    //---------------------------Transaction Management-------------------------
    @Test(priority = 5, description = "Transaction Management")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Transaction Management")
    public void newAdminTransactionManagement() throws Exception {
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\Admin_Credentials.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to maker account");
        login(credential[1], credential[3]);
        try {
            paymentAMCAdjustment();
            transactionStatusHistory();
        } catch (Exception e) {
            Assert.fail("Transaction Management Failed");
        }

    }

    @Step("Transaction Management - Payment Adjustment For AMC ")
    public void paymentAMCAdjustment() throws AWTException, InterruptedException {
        Thread.sleep(100);
        saveTextLog("Clicking on Transaction Management");
        waitAndClickByXpath(driver, "//span[contains(text(),'Transaction Management')]");
        Thread.sleep(400);


        driver.findElement(By.xpath("//*[@id=\"js-side-menu-6\"]/ul/li[1]/a")).click();
        saveTextLog("Payment Adjustment Selected");
        Thread.sleep(300);

        waitForElementXpath(driver, "//span[contains(text(),'AMC')]");
        Thread.sleep(700);

        List<WebElement> opt = driver.findElements(By.xpath("//*[@id=\"select2-drop\"]/ul/li[1]/div"));
        int selectedAdjustmentType = opt.size();
        for (int j = 0; j < opt.size(); j++) {
            if (opt.get(j).getText().equals("AMC")) {
                opt.get(j).click();
                break;
            }
        }
        saveTextLog("AMC selected");
        Thread.sleep(500);

        sendKeysByXpath(driver, "//*[@id=\"merchantNameLike\"]", "Puma123");//Merchant Name
        saveTextLog("Merchant Name is :Safex1234");
        Thread.sleep(500);
        clickByXpath(driver, "//*[@id=\"s2id_autogen3\"]/a");
        saveTextLog("Nodal Account");
        saveTextLog("Account Type Button");
        List<WebElement> nddActypeDD = driver.findElements(By.xpath("//*[@id=\"select2-drop\"]/ul/li"));
        nddActypeDD.remove(0);
        int selectedNddAccountT = createRandomNum(0, nddActypeDD.size() - 1);
        Thread.sleep(500);
        String NddActNew = nddActypeDD.get(selectedNddAccountT).findElement(By.tagName("div")).getText();
        saveTextLog("Selected Ndd Account:" + NddActNew);
        nddActypeDD.get(selectedNddAccountT).click();
        //JavascriptExecutor js27 = (JavascriptExecutor) driver;
        //js27.executeScript("window.scrollBy(0,200)", "");
        Thread.sleep(500);
        saveTextLog("Selected Nodal Account");
        Thread.sleep(500);
        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div/div/div[4]/div/input", "Adding Narration"); // narration detail
        saveTextLog(" clicked on Narration");
        Thread.sleep(500);
        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div/div/div[6]/div/input", "100"); //Amount
        saveTextLog(" Added Amount");
        Thread.sleep(800);

        waitAndClickByXpath(driver, "//body/div[1]/div[1]/form[1]/div[4]/div[1]/div[1]/div[7]/div[1]/div[1]/span[1]/button[1]");
        saveTextLog("Clicked on Calendar Button For select Date From");
        Thread.sleep(200);

        waitAndClickByXpath(driver,"//tbody/tr[1]/td[5]/button[1]/span[1]");
        Thread.sleep(500);
        saveTextLog("Date Selected Successfully");
        Thread.sleep(200);

        scrollToCenterXpath(driver,"//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div/div/div[8]/div/div/input");
        Thread.sleep(200);

        waitAndClickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div/div/div[8]/div/div/span/button");
        saveTextLog("Clicked on Calendar Button For To");
        Thread.sleep(200);

        waitAndClickByXpath(driver,"//tbody/tr[2]/td[6]/button[1]");
        Thread.sleep(500);
        saveTextLog("Date Selected Successfully in To Column");
        Thread.sleep(200);

        scrollToCenterXpath(driver,"//body/div[1]/div[1]/form[1]/div[3]/div[1]");
        Thread.sleep(200);

        clickByXpath(driver, "//*[@id=\"heading-action-wrapper\"]/div/div/div[3]/button");
        saveTextLog("clicked on Submit button");
        Thread.sleep(700);

        /*Assert.assertEquals("AMC", driver.findElement(By.xpath("//tbody/tr[2]/td[6]")).getText());
        System.out.println(driver.findElement(By.xpath("//tbody/tr[2]/td[6]")).getText());
        saveTextLog("AMC Matched");*/

        String tAable_statusAMC = driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div/div[2]/div/table/tbody/tr[2]")).getText();
        System.out.println("The Status  is - " + tAable_statusAMC);
        Thread.sleep(900);


    }


    // For Transaction History
    @Step("Transaction MAnagement - Transaction Status History")
    public void transactionStatusHistory() throws AWTException, InterruptedException {
        Thread.sleep(800);
        saveTextLog("Clicking on Transaction Management");
        waitAndClickByXpath(driver, "//span[contains(text(),'Transaction Management')]");
        Thread.sleep(1000);
        saveTextLog("Clicked on Transaction Management");
        Thread.sleep(700);
        clickByXpath(driver, "//a[contains(text(),'Transaction Status History')]");
        Thread.sleep(2000);
        saveTextLog("Clicked on Transaction status history");
        Thread.sleep(600);


        waitForElementXpath(driver,"//body/div[1]/div[1]/div[1]/div[3]/div[1]/div[2]/div[1]/div[1]/div[1]/input[1]");
        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[1]/div[3]/div[1]/div[2]/div[1]/div[1]/div[1]/input[1]", "1031841633432588732");//AG REfer
        saveTextLog("AG Refer number added");
        Thread.sleep(500);
        scrollToCenterXpath(driver, "//button[contains(text(),'search')]");
        waitAndClickByXpath(driver, "//button[contains(text(),'search')]");
        saveTextLog("Clicked on Search Button");
        Thread.sleep(400);

        scrollToViewXpath(driver, "//body/div[@class='ng-scope']/div[@class='main ng-scope']/div[@class='panel panel-default']/div[1]");
        Thread.sleep(400);

        Assert.assertEquals("paygate 1031841633432588732 202107090001 05 Pending 2021-10-06 15:56:36.0", driver.findElement(By.xpath("//*[@id=\"transactions\"]/tbody/tr[3]")).getText());
        System.out.println(driver.findElement(By.xpath("//*[@id=\"transactions\"]/tbody/tr[3]")).getText());
        saveTextLog("AG REFER is Displayed");
        Thread.sleep(600);

    }

//-------Transaction Management For HOLD-------------------------------------------

    @Test(priority = 6, description = "Transaction Management - Payment Adjustment")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Transaction Management")
    public void TransactionManagemnetHold() throws Exception {
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\Admin_Credentials.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to maker account");
        login(credential[1], credential[3]);
        try {

            paymentHOLDAdjustment();
        } catch (Exception e) {
            Assert.fail("Transaction Management Failed");
        }

    }

    @Step("Transaction Management - Payment Adjustment For AMC ")
    public void paymentHOLDAdjustment() throws AWTException, InterruptedException {
        Thread.sleep(100);
        saveTextLog("Clicking on Transaction Management");
        waitAndClickByXpath(driver, "//span[contains(text(),'Transaction Management')]");
        Thread.sleep(400);

        driver.findElement(By.xpath("//*[@id=\"js-side-menu-6\"]/ul/li[1]/a")).click();
        saveTextLog("Payment Adjustment Selected");
        Thread.sleep(300);

        driver.findElement(By.xpath("//*[@id=\"s2id_autogen1\"]")).click();
        Thread.sleep(300);


        List<WebElement> opt = driver.findElements(By.xpath("//*[@id=\"select2-drop\"]/ul/li[2]/div"));
        int selectedAdjussttmeentType = opt.size();
        for (int j = 0; j < opt.size(); j++) {
            if (opt.get(j).getText().equals("HOLD")) {
                opt.get(j).click();
                break;
            }
        }

        saveTextLog("HOLD Selected");
        Thread.sleep(600);

        sendKeysByXpath(driver, "//*[@id=\"merchantNameLike\"]", "PUMA123");//Merchant Name
        saveTextLog("PUMA123");
        Thread.sleep(1000);
        sendKeysByXpath(driver, "//body/div[1]/div[1]/form[1]/div[4]/div[1]/div[1]/div[5]/div[1]/input[1]", "1031761639552006684");//Merchant AG REf
        saveTextLog("1031761639552006684");

        sendKeysByXpath(driver, "//body/div[1]/div[1]/form[1]/div[4]/div[1]/div[1]/div[6]/div[1]/input[1]", "500"); //Amount
        saveTextLog(" Added Amount");
        Thread.sleep(800);

        waitAndClickByXpath(driver, "//body/div[1]/div[1]/form[1]/div[4]/div[1]/div[1]/div[7]/div[1]/div[1]/span[1]/button[1]");
        saveTextLog("Clicked on Calendar Button For select Date From");
        Thread.sleep(200);

        waitAndClickByXpath(driver,"//tbody/tr[1]/td[5]/button[1]/span[1]");
        Thread.sleep(500);
        saveTextLog("Date Selected Successfully");
        Thread.sleep(200);

        scrollToCenterXpath(driver,"//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div/div/div[8]/div/div/input");
        Thread.sleep(200);

        waitAndClickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/form/div[4]/div/div/div[8]/div/div/span/button");
        saveTextLog("Clicked on Calendar Button For To");
        Thread.sleep(200);

        clickByXpath(driver, "//*[@id=\"heading-action-wrapper\"]/div/div/div[3]/button");
        saveTextLog("clicked on Submit button");
        Thread.sleep(700);

        Assert.assertEquals("Hold", driver.findElement(By.xpath("//td[contains(text(),'Hold')]")).getText());
        System.out.println(driver.findElement(By.xpath("//td[contains(text(),'Hold')]")).getText());
        saveTextLog("Hold Matched");

        String tAable_statusHOLD = driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div/div[2]/div/table/tbody/tr[2]")).getText();
        System.out.println("The Table is-" + tAable_statusHOLD);
        Thread.sleep(900);

    }

    //------------------------Transaction Simulation---------------------
    @Test(priority = 7, description = "Transaction Simulation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test is for simulating transactions")
    public void transactionSimulationNewMerchants() throws Exception {
        boolean testFail = false;
        ReadFromCSV lastRun = new ReadFromCSV(System.getProperty("user.dir") + "/Output_Files/Merchant_Authorization_Status_Last_Session.csv");
        deleteContentsOfCsv("Output_Files/Transactions_Status_Aggregator_Last_Session.csv");
        deleteContentsOfCsv("Output_Files/Transactions_Status_JS_Last_Session.csv");
        for (int i = 1; i < lastRun.SizeOfFile(); i++) {
            String[] lastData = lastRun.ReadLineNumber(i);

            if (lastData[1].equalsIgnoreCase("aggregator hosted")) {
                try {
                    aggregatorHostedSimulator(lastData, "VISA");
                } catch (Exception e) {
                    if (!e.getMessage().equalsIgnoreCase("Unverified Merchant Transaction")) {
                        softAssert.fail("Unauthorized transaction passed");
                        testFail = true;
                        System.out.println(e.getMessage());
                    } else {
                        try {
                            aggregatorHostedSimulator(lastData, "VISA");
                        } catch (Exception e1) {
                            softAssert.fail("Error in transaction");
                            testFail = true;
                            System.out.println(e.getMessage());
                        }
                    }
                }
                try {
                    aggregatorHostedSimulator(lastData, "Mastercard");
                } catch (Exception e) {
                    if (!e.getMessage().equalsIgnoreCase("Unverified Merchant Transaction")) {
                        softAssert.fail("Unauthorized transaction passed");
                        testFail = true;
                        System.out.println(e.getMessage());
                    } else {
                        try {
                            aggregatorHostedSimulator(lastData, "Mastercard");
                        } catch (Exception e1) {
                            softAssert.fail("Error in transaction");
                            testFail = true;
                            System.out.println(e.getMessage());
                        }
                    }
                }
                try {
                    aggregatorHostedSimulator(lastData, "Tabby");
                } catch (Exception e) {
                    try {
                        aggregatorHostedSimulator(lastData, "Tabby");
                    } catch (Exception e1) {
                        softAssert.fail("Error in transaction");
                        testFail = true;
                        System.out.println(e.getMessage());
                    }
                }
            } else if (lastData[1].equalsIgnoreCase("js checkout")) {
                try {
                    jsCheckoutSimulator(lastData, "VISA");
                } catch (Exception e) {
                    if (!e.getMessage().equalsIgnoreCase("Unverified Merchant Transaction")) {
                        softAssert.fail("Unauthorized transaction passed");
                        testFail = true;
                        System.out.println(e.getMessage());
                    } else {
                        try {
                            jsCheckoutSimulator(lastData, "VISA");
                        } catch (Exception e1) {

                            softAssert.fail("Error in transaction");
                            testFail = true;
                            System.out.println(e.getMessage());
                        }
                    }
                }
                try {
                    jsCheckoutSimulator(lastData, "MasterCard");
                } catch (Exception e) {
                    if (!e.getMessage().equalsIgnoreCase("Unverified Merchant Transaction")) {
                        softAssert.fail("Unauthorized transaction passed");
                        testFail = true;
                        System.out.println(e.getMessage());
                    } else {
                        try {
                            jsCheckoutSimulator(lastData, "MasterCard");
                        } catch (Exception e1) {
                            softAssert.fail("Error in transaction");
                            testFail = true;
                            System.out.println(e.getMessage());
                        }
                    }
                }
                try {
                    jsCheckoutSimulator(lastData, "Tabby");
                } catch (Exception e) {
                    if (!e.getMessage().equalsIgnoreCase("Unverified Merchant Transaction")) {
                        softAssert.fail("Unauthorized transaction passed");
                        testFail = true;
                        System.out.println(e.getMessage());
                    } else {
                        try {
                            jsCheckoutSimulator(lastData, "Tabby");
                        } catch (Exception e1) {
                            softAssert.fail("Error in transaction");
                            testFail = true;
                            System.out.println(e.getMessage());
                        }
                    }
                }
            }

            Thread.sleep(5000);
        }
        if (testFail) {
            Assert.fail("Transactions failed for already created merchants");
        }
    }



    //------------------------Transaction Predefined Simulation-----------------------------------
    @Test(priority = 8, description = "Transaction Simulation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test is for simulating transactions")
    public void transactionSimulationPredefinedMerchants() throws Exception {
        boolean testFail = false;
        ReadFromCSV lastRun = new ReadFromCSV(System.getProperty("user.dir") + "/Configuration_Files/Transactions/All Scenarios Created Merchants/Authorized_Merchants_Scenarios.csv");
        deleteContentsOfCsv("Output_Files/Transactions_Status_Aggregator_Last_Session.csv");
        deleteContentsOfCsv("Output_Files/Transactions_Status_JS_Last_Session.csv");
        for (int i = 1; i < lastRun.SizeOfFile(); i++) {
            String[] lastData = lastRun.ReadLineNumber(i);

            if (lastData[1].equalsIgnoreCase("aggregator hosted")) {
                try {
                    aggregatorHostedSimulator(lastData, "VISA");
                } catch (Exception e) {
                    if (!e.getMessage().equalsIgnoreCase("Unverified Merchant Transaction")) {
                        softAssert.fail("Unauthorized transaction passed");
                        testFail = true;
                        System.out.println(e.getMessage());
                    } else {
                        try {
                            aggregatorHostedSimulator(lastData, "VISA");
                        } catch (Exception e1) {
                            softAssert.fail("Error in transaction");
                            testFail = true;
                            System.out.println(e.getMessage());
                        }
                    }
                }
                try {
                    aggregatorHostedSimulator(lastData, "Mastercard");
                } catch (Exception e) {
                    if (!e.getMessage().equalsIgnoreCase("Unverified Merchant Transaction")) {
                        softAssert.fail("Unauthorized transaction passed");
                        testFail = true;
                        System.out.println(e.getMessage());
                    } else {
                        try {
                            aggregatorHostedSimulator(lastData, "Mastercard");
                        } catch (Exception e1) {
                            softAssert.fail("Error in transaction");
                            testFail = true;
                            System.out.println(e.getMessage());
                        }
                    }
                }

            } else if (lastData[1].equalsIgnoreCase("js checkout")) {
                try {
                    jsCheckoutSimulator(lastData, "VISA");
                } catch (Exception e) {
                    if (!e.getMessage().equalsIgnoreCase("Unverified Merchant Transaction")) {
                        softAssert.fail("Unauthorized transaction passed");
                        testFail = true;
                        System.out.println(e.getMessage());
                    } else {
                        try {
                            jsCheckoutSimulator(lastData, "VISA");
                        } catch (Exception e1) {

                            softAssert.fail("Error in transaction");
                            testFail = true;
                            System.out.println(e.getMessage());
                        }
                    }
                }
                try {
                    jsCheckoutSimulator(lastData, "MasterCard");
                } catch (Exception e) {
                    if (!e.getMessage().equalsIgnoreCase("Unverified Merchant Transaction")) {
                        softAssert.fail("Unauthorized transaction passed");
                        testFail = true;
                        System.out.println(e.getMessage());
                    } else {
                        try {
                            jsCheckoutSimulator(lastData, "MasterCard");
                        } catch (Exception e1) {
                            softAssert.fail("Error in transaction");
                            testFail = true;
                            System.out.println(e.getMessage());
                        }
                    }
                }

            }


            Thread.sleep(5000);
        }
        if (testFail) {
            softAssert.fail("Transactions failed for already created merchants");
        }
    }

    @Step("Aggregator Hosted Payment Simulator")
    public void aggregatorHostedSimulator(String[] merchantData, String mode) throws Exception {
        String[] orderDetails = new String[11];
        orderDetails[10] = "No";
        boolean tabbyNegative = false;
        boolean tabbyFail = false;
        ReadFromCSV portalInfo = new ReadFromCSV(System.getProperty("user.dir") + "\\Configuration_Files\\Transactions\\Payment Portals\\Aggregator_Hosted.csv");
        String aggregatorPortalUrl = portalInfo.ReadLineNumber(1)[0];
        driver.get(aggregatorPortalUrl);
        orderDetails[0] = merchantData[0];
        orderDetails[1] = merchantData[7];
        String stepName = "Aggregator Hosted Transaction for: " + merchantData[0] + " | ID: " + merchantData[7];

        ReadFromCSV cardDetails = null;
        saveTextLog("Card Payment in progress");
        if (merchantData[4].equalsIgnoreCase("yes")) {
            stepName += " | Cybersource";
            if (mode.equalsIgnoreCase("visa")) {
                stepName += " | VISA";
                orderDetails[8] = "VISA";
                cardDetails = new ReadFromCSV(System.getProperty("user.dir") + "/Configuration_Files/Transactions/Card Information/Cybersource_Visa.csv");
            } else if (mode.equalsIgnoreCase("mastercard")) {
                stepName += " | MasterCard";
                orderDetails[8] = "MasterCard";
                cardDetails = new ReadFromCSV(System.getProperty("user.dir") + "/Configuration_Files/Transactions/Card Information/Cybersource_MasterCard.csv");
            }
            if (merchantData[2].equalsIgnoreCase("yes")) {
                ReadFromCSV csv = new ReadFromCSV(System.getProperty("user.dir") + "/Configuration_Files/Create_Merchant_Data/Payment_Modes/CybersourcePG_3DS_Key.csv");
                String[] MID = csv.ReadLineNumber(1);
                orderDetails[9] = MID[0];
            } else if (merchantData[3].equalsIgnoreCase("yes")) {
                ReadFromCSV csv = new ReadFromCSV(System.getProperty("user.dir") + "/Configuration_Files/Create_Merchant_Data/Payment_Modes/CybersourcePG_Non-3DS_Key.csv");
                String[] MID = csv.ReadLineNumber(1);
                orderDetails[9] = MID[0];
            }

        } else if (merchantData[5].equalsIgnoreCase("yes")) {
            stepName += " | MPGS";
            if (mode.equalsIgnoreCase("visa")) {
                stepName += " | VISA";
                orderDetails[8] = "VISA";
                cardDetails = new ReadFromCSV(System.getProperty("user.dir") + "/Configuration_Files/Transactions/Card Information/MPGS_Visa.csv");
            } else if (mode.equalsIgnoreCase("mastercard")) {
                stepName += " | MasterCard";
                orderDetails[8] = "MasterCard";
                cardDetails = new ReadFromCSV(System.getProperty("user.dir") + "/Configuration_Files/Transactions/Card Information/MPGS_MasterCard.csv");
            }
            if (merchantData[2].equalsIgnoreCase("yes")) {
                ReadFromCSV csv = new ReadFromCSV(System.getProperty("user.dir") + "/Configuration_Files/Create_Merchant_Data/Payment_Modes/MPGS-Fab-3DS_Key.csv");
                String[] MID = csv.ReadLineNumber(1);
                orderDetails[9] = MID[0];
            } else if (merchantData[3].equalsIgnoreCase("yes")) {
                ReadFromCSV csv = new ReadFromCSV(System.getProperty("user.dir") + "/Configuration_Files/Create_Merchant_Data/Payment_Modes/MPGS-Fab-Non-3DS_Key.csv");
                String[] MID = csv.ReadLineNumber(1);
                orderDetails[9] = MID[0];
            }
        }

        List<String[]> cards = new ArrayList<>();
        if (cardDetails != null) {
            for (int i = 1; i < cardDetails.SizeOfFile(); i++) {
                String[] temp = cardDetails.ReadLineNumber(i);
                if (merchantData[2].equalsIgnoreCase(temp[4]) && merchantData[2].equalsIgnoreCase("yes")) {
                    cards.add(temp);
                } else if (merchantData[3].equalsIgnoreCase(temp[5]) && merchantData[3].equalsIgnoreCase("yes") && merchantData[2].equalsIgnoreCase("no")) {
                    cards.add(temp);
                }
            }
            Thread.sleep(2000);
            String[] selectedCard = cards.get(createRandomNum(0, cards.size() - 1));
            if (merchantData[2].equalsIgnoreCase(selectedCard[4]) && merchantData[2].equalsIgnoreCase("yes")) {
                stepName += " | 3DS";
            } else if (merchantData[3].equalsIgnoreCase(selectedCard[5]) && merchantData[3].equalsIgnoreCase("yes") && merchantData[2].equalsIgnoreCase("no")) {
                stepName += " | Non-3DS";
            }

        }


        try {
            if (merchantData[2].equalsIgnoreCase("yes")) {
                waitForPageToLoad(driver);
                if (driver.getCurrentUrl().contains("https://merchantacsstag.cardinalcommerce.com")) {
                    sendKeysByXpath(driver, "//*[@id=\"password\"]", "1234");
                    saveTextLog("Password entered: " + "1234");
                    Thread.sleep(1000);
                    Screenshot(driver, "");
                    clickByXpath(driver, "//*[@value=\"Submit\"]");
                } else if (driver.getCurrentUrl().contains("https://pguat.safexpay.com/agcore/checkPay#no-back")) {
                    Screenshot(driver, "");
                    waitForElementToBeStale(driver, "//*[@onclick=\"changeAction('YES')\"]");
                } else if (driver.getCurrentUrl().contains("https://ap.gateway.mastercard.com/acs/VisaACS") ||
                        driver.getCurrentUrl().contains("https://ap.gateway.mastercard.com/acs/MastercardACS")) {
                    Screenshot(driver, "");
                    clickByXpath(driver, "//*[@value=\"Submit\"]");
                    Thread.sleep(1000);
                    try {
                        waitForPageToLoad(driver);
                        if (driver.getCurrentUrl().contains("https://pguat.safexpay.com/agcore/payment#no-back")) {
                            Screenshot(driver, "");
                            waitForElementToBeStale(driver, "//*[@onclick=\"changeAction('YES')\"]");
                        }
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            Screenshot(driver, "Error in payment: " + e.getMessage());
        }

        String status = "";
    }

    //-----------------------------------JSCHECKOUT COMMON DATA DECLARATION--------------------------------------------
    @Step("JS Checkout Payment Simulator")
    public void jsCheckoutSimulator(String[] merchantData, String mode) throws Exception {
        String[] orderDetails = new String[8];
        orderDetails[7] = "No";
        String orderId = null, status = null;
        boolean tabbyNegative = false;
        boolean tabbyFail = false;
        ReadFromCSV portalInfo = new ReadFromCSV(System.getProperty("user.dir") + "\\Configuration_Files\\Transactions\\Payment Portals\\JS_Checkout.csv");
        String aggregatorPortalUrl = portalInfo.ReadLineNumber(1)[0];
        driver.get(aggregatorPortalUrl);
    }


    //--------------------------AggregatorHosted without Paymode --CARD TRANSACTION -------------------
    @Test(priority = 9, description = "AggregatorHosted without Paymode")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test is for Agcore Card  transactions")
    public void aggregatorCardPaytransaction() throws Exception {
    /*String[] orderDetails = new String[11];
    orderDetails[10] = "No";*/
        boolean testFail = false;
        ReadFromCSV portalInfo = new ReadFromCSV(System.getProperty("user.dir") + "\\Configuration_Files\\Transactions\\Payment Portals\\Aggregator_Hosted.csv");
        String aggPotalUrl = portalInfo.ReadLineNumber(1)[0];
        driver.get(aggPotalUrl);
        Thread.sleep(1000);
        waitForElementXpath(driver, "//select[@id='domain']");
        clickByXpath(driver, "//*[@id=\"domain\"]/option[3]");
        saveTextLog("AWS UAT selected Successfully");
        Thread.sleep(1200);
        sendKeysByXpath(driver, "//input[@id='me_id']", "202107090001");
        saveTextLog("Merchant Id is printed");
        Thread.sleep(1000);
        sendKeysByXpath(driver, "//input[@id='me_key']", "wR4N7nxdBne3spWjO6MnRtelU3Ryqf+7w6nCcx5fFM8=");
        saveTextLog("Merchant Key is printed");
        Thread.sleep(1000);

        String orderid = getTimestamp("yyMMddhhmmss");
        driver.findElement(By.xpath("//*[@id=\"order_no\"]")).clear();
        Thread.sleep(100);
        sendKeysByXpath(driver, "//*[@id=\"order_no\"]", orderid);
        saveTextLog("Order number: " + orderid);

        int amount = createRandomNum(100, 500);
        driver.findElement(By.xpath("//*[@id=\"amount\"]")).clear();
        sendKeysByXpath(driver, "//*[@id=\"amount\"]", Integer.toString(amount));
        saveTextLog("Amount: " + amount);

        sendKeysByXpath(driver, "//*[@id=\"country\"]", "USA");
        saveTextLog("country");
        sendKeysByXpath(driver, "//*[@id=\"currency\"]", "US Dollar");
        saveTextLog("US Dollar");
        scrollToViewXpath(driver, "//*[@id=\"cs-main-body\"]/div/div/div[3]/div[8]/legend");
        Thread.sleep(500);

        clickByXpath(driver, "//button[normalize-space()='Checkout']");
        saveTextLog("Submit Button Clicked");
        Thread.sleep(1000);

        clickByXpath(driver, "//li[@role='presentation']//a[@id='CD']");
        saveTextLog("Clicked ON Card Payment ");
        Thread.sleep(1000);
        //waitForElementXpath(driver, "//li[@role='presentation']//a[@id='CD']");
        sendKeysByXpath(driver, "//input[@id='cdCardNumber']", "4242 4242 4242 4242");
        saveTextLog("card number added successfully");
        Thread.sleep(1200);
        sendKeysByXpath(driver, "//input[@id='name']", "Kirti");
        saveTextLog("Name On card added successfully");
        Thread.sleep(1200);
        clickByXpath(driver, "//select[@id='cdExpiryMonth']");
        clickByXpath(driver, "//select[@id='cdExpiryMonth']//option[@value='05'][normalize-space()='May']");
        saveTextLog("May Month Selected successfully");
        Thread.sleep(1200);
        clickByXpath(driver, "//*[@id=\"cdExpYear\"]");
        clickByXpath(driver, "//select[@id='cdExpYear']//option[@value='2022'][normalize-space()='2022']");
        saveTextLog("Year Selected successfully");
        Thread.sleep(1200);
        clickByXpath(driver, "//*[@id=\"cdCVV\"]");
        sendKeysByXpath(driver, "//input[@id='cdCVV']", "123");
        saveTextLog("CVV added successfully");
        Thread.sleep(1200);
        clickByXpath(driver, "//*[@id=\"cdCards\"]/div[6]/div/button");
        saveTextLog("Pay Now Clicked successfully");
        Thread.sleep(1200);
        //waitForPageToLoad(driver);
        driver.getCurrentUrl().contains("https://merchantacsstag.cardinalcommerce.com");
        waitForElementXpath(driver, "//*[@id=\"password\"]");
        sendKeysByXpath(driver, "//*[@id=\"password\"]", "1234");
        saveTextLog("Password entered:");
        Thread.sleep(1000);
        driver.findElement(By.xpath("//input[@name='UsernamePasswordEntry']")).click();
        Thread.sleep(1000);

    }


//-------------------------AGNETBANKING-------------------------

    @Test(priority = 10, description = "Agcore without Paymode")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test is for Aggregator Netbanking transactions")
    public void agCoreNetBanktransaction() throws Exception {
        //String[] orderDetails = new String[11];
        //orderDetails[10]="No";
        boolean testFail = false;
        ReadFromCSV portalInfo = new ReadFromCSV(System.getProperty("user.dir") + "\\Configuration_Files\\Transactions\\Payment Portals\\Aggregator_Hosted.csv");
        String aggPotalUrl = portalInfo.ReadLineNumber(1)[0];
        driver.get(aggPotalUrl);
        Thread.sleep(1000);
        waitForElementXpath(driver, "//select[@id='domain']");
        clickByXpath(driver, "//*[@id=\"domain\"]/option[3]");
        saveTextLog("AWS UAT selected Successfully");
        Thread.sleep(1200);
        sendKeysByXpath(driver, "//input[@id='me_id']", "202107090001");
        saveTextLog("Merchant Id is printed");
        Thread.sleep(1000);
        sendKeysByXpath(driver, "//input[@id='me_key']", "wR4N7nxdBne3spWjO6MnRtelU3Ryqf+7w6nCcx5fFM8=");
        saveTextLog("Merchant Key is printed");
        Thread.sleep(1000);

        String orderid = getTimestamp("yyMMddhhmmss");
        driver.findElement(By.xpath("//*[@id=\"order_no\"]")).clear();
        Thread.sleep(100);
        sendKeysByXpath(driver, "//*[@id=\"order_no\"]", orderid);
        saveTextLog("Order number: " + orderid);


        clickByXpath(driver, "//button[normalize-space()='Checkout']");
        saveTextLog("Submit Button Clicked");
        Thread.sleep(1000);
        driver.findElement(By.xpath("//li[@role='presentation']//a[@id='NB']")).click();
        saveTextLog("Netbanking selected");
        Thread.sleep(700);
        clickByXpath(driver, "//*[@id=\"netB\"]/div[3]/div/div/button/span[1]");
        clickByXpath(driver, "//*[@id=\"netB\"]/div[3]/div/div/div/div/input");
        driver.findElement(By.xpath("//*[@id=\"netB\"]/div[3]/div/div/div/ul/li[3]/a/span[1]")).click();
        saveTextLog("Test Bank Selected");
        Thread.sleep(2000);
        driver.findElement(By.xpath("//*[@id=\"netB\"]/div[5]/div/button")).click();
        saveTextLog("PayNow button clicked");
        Thread.sleep(1000);
        driver.findElement(By.xpath("//body/form[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/button[1]")).click();
        saveTextLog("clicked on Generate Success Transaction");
        Thread.sleep(1000);

        String statuus, ordderId;
        //statuus = driver.findElement(By.xpath("//*[@id=\"cs-main-body\"]/div/div/h2")).getAttribute("innerText");
        saveTextLog("Successful status is printed");
        Thread.sleep(1000);


    }
        /*String expected_url = "https://pguat.safexpay.com/simulator/initiateTransaction";
        String current_url = driver.getCurrentUrl();
        Assert.assertTrue(expected_url.equals(current_url), "URL does not match\n");
        Thread.sleep(2000);
        System.out.println(" Successful Transaction Page is open\n");*/


//---------------------------------AGHostedPaymodeNetBank------------------------------

    @Test(priority = 11, description = "Agcore with Paymode")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test is for Aggregator Hosted with Paymode Netbanking transactions")
    public void agHostedPayNetBAnkiransaction() throws Exception {
        boolean testFail = false;
        ReadFromCSV portalInfo = new ReadFromCSV(System.getProperty("user.dir") + "\\Configuration_Files\\Transactions\\Payment Portals\\Aggregator_Hosted.csv");
        String aggPotalUrl = portalInfo.ReadLineNumber(1)[0];
        driver.get(aggPotalUrl);
        Thread.sleep(1000);
        waitForElementXpath(driver, "//select[@id='domain']");
        clickByXpath(driver, "//*[@id=\"domain\"]/option[3]");
        saveTextLog("AWS UAT selected Successfully");
        Thread.sleep(1200);
        sendKeysByXpath(driver, "//input[@id='me_id']", "202201280002");
        saveTextLog("Merchant Id is printed");
        Thread.sleep(1000);
        sendKeysByXpath(driver, "//input[@id='me_key']", "jdnrBy4LPyHUEQzuH+BE3lRCkXWjTLiGBd0s2sx58To=");
        saveTextLog("Merchant Key is printed");
        Thread.sleep(1000);

        String orderid = getTimestamp("yyMMddhhmmss");
        driver.findElement(By.xpath("//*[@id=\"order_no\"]")).clear();
        Thread.sleep(100);
        sendKeysByXpath(driver, "//*[@id=\"order_no\"]", orderid);
        saveTextLog("Order number: " + orderid);

        clickByXpath(driver, "//select[@id='paymode']");
        saveTextLog("Paymode Selected as a Netbanking");
        Thread.sleep(1000);
        clickByXpath(driver, "//option[contains(text(),'Net Banking')]");
        saveTextLog("Selected Paymode Netbanking");
        Thread.sleep(500);

        scrollToViewXpath(driver, "//*[@id=\"cs-main-body\"]/div/div/div[3]/div[6]/legend");
        Thread.sleep(500);

        clickByXpath(driver, "//*[@id=\"cs-main-body\"]/div/div/div[4]/div/div/div/button");
        saveTextLog("Submit Button Clicked");
        Thread.sleep(1000);

        clickByXpath(driver, "//*[@id=\"netBankname\"]");
        saveTextLog("Test Bank selected");
        Thread.sleep(1000);

        driver.findElement(By.xpath("//*[@id=\"netB\"]/div[5]/div/button")).click();
        //clickByXpath(driver, "//button[@class='btn sp-btn netPay secure netbankingPay']");
        saveTextLog("Pay now button selected");
        Thread.sleep(1000);
        driver.findElement(By.xpath("//button[normalize-space()='Generate Success Transaction']")).click();
        saveTextLog("clicked on Generate Success Transaction");
        Thread.sleep(1000);

        String statuus, ordderId;
        statuus = driver.findElement(By.xpath("//*[@id=\"cs-main-body\"]/div/div/h2")).getAttribute("innerText");
        saveTextLog("Successful status is printed");
        Thread.sleep(1000);



    }


//-----------------------------------agHostedCardPaymode-------------------------

    @Test(priority = 12, description = "Agcore with Paymode")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test is for Aggregator Hosted with Paymode Netbanking transactions")
    public void agHostedPayCardtransaction() throws Exception {
        boolean testFail = false;
        ReadFromCSV portalInfo = new ReadFromCSV(System.getProperty("user.dir") + "\\Configuration_Files\\Transactions\\Payment Portals\\Aggregator_Hosted.csv");
        String aggPotalUrl = portalInfo.ReadLineNumber(1)[0];
        driver.get(aggPotalUrl);
        Thread.sleep(1000);
        waitForElementXpath(driver, "//select[@id='domain']");
        clickByXpath(driver, "//*[@id=\"domain\"]/option[3]");
        saveTextLog("AWS UAT selected Successfully");
        Thread.sleep(1200);
        sendKeysByXpath(driver, "//input[@id='me_id']", "202201280002");
        saveTextLog("Merchant Id is printed");
        Thread.sleep(1000);
        sendKeysByXpath(driver, "//input[@id='me_key']", "jdnrBy4LPyHUEQzuH+BE3lRCkXWjTLiGBd0s2sx58To=");
        saveTextLog("Merchant Key is printed");
        Thread.sleep(1000);

        String orderid = getTimestamp("yyMMddhhmmss");
        driver.findElement(By.xpath("//*[@id=\"order_no\"]")).clear();
        Thread.sleep(100);
        sendKeysByXpath(driver, "//*[@id=\"order_no\"]", orderid);
        saveTextLog("Order number: " + orderid);
        sendKeysByXpath(driver, "//*[@id=\"country\"]", "USA");
        saveTextLog("country");
        sendKeysByXpath(driver, "//*[@id=\"currency\"]", "US Dollar");
        saveTextLog("US Dollar");
        scrollToViewXpath(driver, "//*[@id=\"cs-main-body\"]/div/div/div[3]/div[8]/legend");
        Thread.sleep(500);

        // sendKeysByXpath(driver, "//input[@id='order_no']", "34");
        //saveTextLog("order number entered Successfully");
        //Thread.sleep(1200);
        clickByXpath(driver, "//select[@id='paymode']");
        clickByXpath(driver, "//*[@id=\"paymode\"]/option[3]");
        saveTextLog("Selected card as a Paymode");
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@id=\"cs-main-body\"]/div/div/div[4]/div/div/div/button");
        saveTextLog("Submit Button Clicked");
        Thread.sleep(500);
        driver.findElement(By.xpath("//*[@id=\"CD\"]")).click();
        saveTextLog("Card option selected");
        Thread.sleep(1000);
        //clickByXpath(driver,"//*[@id=\"cdCardNumber\"]");
        sendKeysByXpath(driver, "//input[@id='cdCardNumber']", "4242424242424242");
        saveTextLog("Card Number entered");
        Thread.sleep(1000);
        sendKeysByXpath(driver, "//input[@id='name']", "Kirti");
        saveTextLog("Name On card added successfully");
        Thread.sleep(1200);
        clickByXpath(driver, "//select[@id='cdExpiryMonth']");
        clickByXpath(driver, "//select[@id='cdExpiryMonth']//option[@value='05'][normalize-space()='May']");
        saveTextLog("May Month Selected successfully");
        Thread.sleep(1200);
        clickByXpath(driver, "//*[@id=\"cdExpYear\"]");
        clickByXpath(driver, "//select[@id='cdExpYear']//option[@value='2022'][normalize-space()='2022']");
        saveTextLog("Year Selected successfully");
        Thread.sleep(1200);
        clickByXpath(driver, "//*[@id=\"cdCVV\"]");
        sendKeysByXpath(driver, "//input[@id='cdCVV']", "123");
        saveTextLog("CVV added successfully");
        Thread.sleep(1200);
        clickByXpath(driver, "//*[@id=\"cdCards\"]/div[7]/div/button");
        saveTextLog("Pay Now Clicked successfully");
        Thread.sleep(1200);
        waitForPageToLoad(driver);
        driver.getCurrentUrl().contains("https://merchantacsstag.cardinalcommerce.com");
        sendKeysByXpath(driver, "//*[@id=\"password\"]", "1234");
        saveTextLog("OTP entered: " + "1234");
        Thread.sleep(1000);
        //waitForElementXpath(driver, "//input[@id='password']");
        //sendKeysByXpath(driver, "//input[@id='password']", "1234");
        //Thread.sleep(1000);
        //clickByXpath(driver, "//");
        //saveTextLog("OTP submitted successfully");
        //Thread.sleep(1000);
        driver.findElement(By.xpath("//input[@name='UsernamePasswordEntry']")).click();
        Thread.sleep(900);
        //driver.findElement(By.xpath("//button[normalize-space()='Generate Success Transaction']")).click();
        //saveTextLog("clicked on Generate Success Transaction");
        //Thread.sleep(1000);

        String statuus, ordderId;
        statuus = driver.findElement(By.xpath("//legend[contains(text(),'Your payment is completed. Here is the details for')]")).getAttribute("innerText");
        saveTextLog("Successful status is printed");
        Thread.sleep(1000);

        /*if (driver.findElement(By.xpath("//legend[contains(text(),'Your payment is completed. Here is the details for')]")).isDisplayed()) {
            statuus = driver.findElement(By.xpath("//*[@id=\"cs-main-body\"]/div/div/div/div[2]/legend")).getAttribute("innerText");
            ordderId = driver.findElement(By.xpath("//*[@id=\"cs-main-body\"]/div/div/div/div[4]/div[1]/div/label")).getText();
            Thread.sleep(700);
        } else {
            saveTextLog("Failed Transaction error");
        }*/



    }


//--------------------------------Merchant NetBanking------------------------------

    @Test(priority = 13, description = "Merchant with  Paymode and scheme")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test is for Merchnat Netabnking  transactions")
    public void merChantNetbnkTransaction() throws Exception {
        boolean testFail = false;
        ReadFromCSV portalInfo = new ReadFromCSV(System.getProperty("user.dir") + "\\Configuration_Files\\Transactions\\Payment Portals\\Aggregator_Hosted.csv");
        String aggPotalUrl = portalInfo.ReadLineNumber(1)[0];
        driver.get(aggPotalUrl);
        Thread.sleep(1000);
        waitForElementXpath(driver, "//select[@id='domain']");
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@id=\"domain\"]/option[3]");
        saveTextLog("AWS UAT selected Successfully");
        Thread.sleep(1200);
        sendKeysByXpath(driver, "//input[@id='me_id']", "202107090004");
        saveTextLog("Merchant Id is printed");
        Thread.sleep(1000);
        sendKeysByXpath(driver, "//input[@id='me_key']", "Rfv3F8y2PM26w7mutwkvuE6bO+Qr8Pz0dv3zGpWp+7A=");
        saveTextLog("Merchant Key is printed");
        Thread.sleep(1000);

        String orderid = getTimestamp("yyMMddhhmmss");
        driver.findElement(By.xpath("//*[@id=\"order_no\"]")).clear();
        Thread.sleep(100);
        sendKeysByXpath(driver, "//*[@id=\"order_no\"]", orderid);
        saveTextLog("Order number: " + orderid);

        clickByXpath(driver, "//*[@id=\"pg_id\"]");
        sendKeysByXpath(driver, "//input[@id='pg_id']", "63");
        saveTextLog("Payment Gateway ID as a 63");
        Thread.sleep(1000);
        clickByXpath(driver, "//select[@id='paymode']");
        //saveTextLog("Paymode Selected as a Netbanking");
        //Thread.sleep(1000);
        clickByXpath(driver, "//*[@id=\"paymode\"]/option[2]");
        saveTextLog("Selected Paymode is Netbanking");
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@id=\"scheme\"]");
        sendKeysByXpath(driver, "//*[@id=\"scheme\"]", "7");
        saveTextLog("Scheme added");
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@id=\"emi_months\"]");
        sendKeysByXpath(driver, "//input[@id='emi_months']", "7");
        saveTextLog("EMI added");
        Thread.sleep(1000);
        scrollToCenterXpath(driver, "/html/body/form/div/div[2]/div/div/div/div[3]/div[9]/legend");
        clickByXpath(driver, "//*[@id=\"cs-main-body\"]/div/div/div[4]/div/div/div/button");
        saveTextLog("Submit Button Clicked");
        Thread.sleep(1000);
        waitAndClickByXpath(driver, "//*[@id=\"avantgarde\"]/form/div/div/div/div/div/div/div/div/div/button[1]");
        saveTextLog("Genearte Success Transaction");
        Thread.sleep(600);

        String statuus, ordderId;
        //statuus = driver.findElement(By.xpath("//*[@id=\"cs-main-body\"]/div/div/h2")).getAttribute("innerText");
        saveTextLog("Successful status is printed");
        Thread.sleep(1000);



        /*if (driver.findElement(By.xpath("//*[@id=\\\"cs-main-body\\\"]/div/div/h2")).isDisplayed()) {
            statuus = driver.findElement(By.xpath("//div[@class=\"sp-response-message\"]/h6")).getAttribute("innerText");
            ordderId = driver.findElement(By.xpath("//*[@id=\"cs-main-body\"]/div/div/div/div[4]/div[1]/div/div")).getText();
            Thread.sleep(700);
        } else {
            saveTextLog("Failed Transaction error");
        }*/
            /*String expected_url = "https://pguat.safexpay.com/simulator/initiateTransaction";
            String current_url = driver.getCurrentUrl();
            Assert.assertTrue(expected_url.equals(current_url), "URL does not match\n");
            Thread.sleep(2000);
            System.out.println(" Successful Transaction Page is open\n");*/


    }


    //------------------------------MerchantCreditCard------------------------------
    @Test(priority = 14, description = "Merchant with  Paymode and scheme")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test is for Merchnat Netabnking  transactions")
    public void merChaantCaarrdtransaction() throws Exception {
        boolean testFail = false;
        ReadFromCSV portalInfo = new ReadFromCSV(System.getProperty("user.dir") + "\\Configuration_Files\\Transactions\\Payment Portals\\Aggregator_Hosted.csv");
        String aggPotalUrl = portalInfo.ReadLineNumber(1)[0];
        driver.get(aggPotalUrl);
        Thread.sleep(1000);
        waitForElementXpath(driver, "//select[@id='domain']");
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@id=\"domain\"]/option[3]");
        saveTextLog("AWS UAT selected Successfully");
        Thread.sleep(1200);
        sendKeysByXpath(driver, "//input[@id='me_id']", "202107090004");
        saveTextLog("Merchant Id is printed");
        Thread.sleep(1000);
        sendKeysByXpath(driver, "//input[@id='me_key']", "Rfv3F8y2PM26w7mutwkvuE6bO+Qr8Pz0dv3zGpWp+7A=");
        saveTextLog("Merchant Key is printed");
        Thread.sleep(1000);

        String orderid = getTimestamp("yyMMddhhmmss");
        driver.findElement(By.xpath("//*[@id=\"order_no\"]")).clear();
        Thread.sleep(100);
        sendKeysByXpath(driver, "//*[@id=\"order_no\"]", orderid);
        saveTextLog("Order number: " + orderid);

        clickByXpath(driver, "//*[@id=\"pg_id\"]");
        sendKeysByXpath(driver, "//input[@id='pg_id']", "626");
        saveTextLog("Payment Gateway ID as a 626");
        Thread.sleep(1000);
        clickByXpath(driver, "//select[@id='paymode']");
        clickByXpath(driver, "//*[@id=\"paymode\"]/option[3]");
        saveTextLog("Selected Paymode is Credit Card");
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@id=\"scheme\"]");
        sendKeysByXpath(driver, "//*[@id=\"scheme\"]", "1");
        saveTextLog("Scheme added");
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@id=\"emi_months\"]");
        sendKeysByXpath(driver, "//input[@id='emi_months']", "1");
        saveTextLog("EMI added");
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@id=\"card_no\"]");
        sendKeysByXpath(driver, "//input[@id='card_no']", "4242424242424242");
        saveTextLog("number added");
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@id=\"exp_month\"]");
        sendKeysByXpath(driver, "//select[@id='exp_month']", "5");
        saveTextLog("Month added");
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@id=\"exp_year\"]");
        sendKeysByXpath(driver, "//select[@id='exp_year']", "2022");
        saveTextLog("Year added");
        Thread.sleep(1000);
        sendKeysByXpath(driver, "//input[@id='cvv2']", "123");
        saveTextLog("CVV added");
        Thread.sleep(1000);
        sendKeysByXpath(driver, "//input[@id='card_name']", "Kirti");
        saveTextLog("Name on Card added");
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@id=\"cust_name\"]");
        sendKeysByXpath(driver, "//*[@id=\"cust_name\"]", "Kirti");
        saveTextLog("Name on Card added");
        Thread.sleep(1000);
        sendKeysByXpath(driver, "//input[@id='email_id']", "swaraa1792@gmail.com");
        saveTextLog("Email ID added");
        Thread.sleep(1000);
        sendKeysByXpath(driver, "//input[@id='mobile_no']", "8698017135");
        saveTextLog("Mobile Number added");
        Thread.sleep(1000);
        scrollToCenterXpath(driver, "/html/body/form/div/div[2]/div/div/div/div[3]/div[9]/legend");
        clickByXpath(driver, "//*[@id=\"cs-main-body\"]/div/div/div[4]/div/div/div/button");
        saveTextLog("Submit Button Clicked");
        Thread.sleep(1000);
        sendKeysByXpath(driver, "//input[@id='password']", "1234");
        //clickByXpath(driver, "//");
        saveTextLog("password submitted successfully");
        Thread.sleep(1000);
        driver.findElement(By.xpath("//input[@name='UsernamePasswordEntry']")).click();
        Thread.sleep(900);
        clickByXpath(driver, "//*[@id=\"avantgarde\"]/form/div/div/div/div/div/div/div/div/div/button[1]");
        saveTextLog("Genearte Success Transaction");
        Thread.sleep(600);
        String statuus, ordderId;
        statuus = driver.findElement(By.xpath("//*[@id=\"cs-main-body\"]/div/div/h2")).getAttribute("innerText");
        saveTextLog("Successful status is printed");
        Thread.sleep(1000);
        if (driver.findElement(By.xpath("//*[@id=\\\"cs-main-body\\\"]/div/div/h2")).isDisplayed()) {
            statuus = driver.findElement(By.xpath("//div[@class=\"sp-response-message\"]/h6")).getAttribute("innerText");
            ordderId = driver.findElement(By.xpath("//*[@id=\"cs-main-body\"]/div/div/div/div[4]/div[1]/div/div")).getText();
            Thread.sleep(700);
        } else {
            saveTextLog("Failed Transaction error");
        }
        String expected_url = "https://pguat.safexpay.com/simulator/initiateTransaction";
        String current_url = driver.getCurrentUrl();
        Assert.assertTrue(expected_url.equals(current_url), "URL does not match\n");
        Thread.sleep(2000);
        System.out.println(" Successful Transaction Page is open\n");


    }


    //-------------------------------JS CHECKOUT NET BANKING----------------------------
    @Test(priority = 15, description = "JS Checkout")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test is for JS NET  transactions")
    public void jSNetbanktransacion() throws Exception {
        boolean testFail = false;
        ReadFromCSV portalInfo = new ReadFromCSV(System.getProperty("user.dir") + "\\Configuration_Files\\Transactions\\Payment Portals\\JS_Checkout.csv");
        String jscheckPortalUrl = portalInfo.ReadLineNumber(1)[0];
        driver.get(jscheckPortalUrl);
        waitForElementXpath(driver, "//*[@id=\"meid\"]");
        Thread.sleep(2000);
        sendKeysByXpath(driver, "//*[@id=\"meid\"]", "202107080012");
        saveTextLog("Merchant Id is printed");
        Thread.sleep(2000);
        sendKeysByXpath(driver, "//*[@id=\"key\"]", "i7ob6QGEM32VK4as4D1T0zjpnNAaZGE3p5P4nLU7OSk=");
        saveTextLog("Merchant Key is printed");
        Thread.sleep(2000);

        /*String orderid = getTimestamp("yyMMddhhmmss");
        driver.findElement(By.xpath("//*[@id=\"order_no\"]")).clear();
        Thread.sleep(100);
        sendKeysByXpath(driver, "//*[@id=\"order_no\"]", orderid);
        saveTextLog("Order number: " + orderid);*/

        int amount = createRandomNum(10, 50);
        driver.findElement(By.xpath("//*[@id=\"amount\"]")).clear();
        sendKeysByXpath(driver, "//*[@id=\"amount\"]", Integer.toString(amount));
        saveTextLog("Amount: " + amount);
        String randomName = getRandomString();
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@type=\"submit\"]");
        saveTextLog("Submit Button Clicked");
        Thread.sleep(1000);
        driver.findElement(By.xpath("//button[normalize-space()='Buy']")).click();
        saveTextLog("Clicked on Buy Button");
        Thread.sleep(1000);

        waitForElementXpath(driver, "//*[@id=\"main-div\"]/div/div/div/div");
        Thread.sleep(1000);
        driver.findElement(By.xpath("//*[@id=\"main-div\"]/div/div/div/div/div/div[2]/div/div[2]/div[1]/div[1]/ul/li[1]/div")).click();
        //clickByXpath(driver, "");
        saveTextLog("Netbanking Selected");
        Thread.sleep(500);
        clickByXpath(driver, "/html/body/div[2]/div[1]/div/div/div/div/div/div[2]/div/div[2]/div[1]/div[2]/div[2]/div/div[2]/div/select/option[10]");
        saveTextLog("Test Bank Selected successfully");
        Thread.sleep(100);
        clickByXpath(driver, "/html/body/div[2]/div[1]/div/div/div/div/div/div[2]/div/div[2]/div[1]/div[2]/div[2]/div/div[3]/div/button");
        saveTextLog("Clicked on Payment successfully");
        Thread.sleep(700);



        /*driver.get("https://pguat.safexpay.com/agcore/jscheckoutPayments#no-back");
        String parent=driver.getWindowHandle();
        Set<String>s=driver.getWindowHandles();
        Iterator<String> I1= s.iterator();

        while(I1.hasNext()) {

            String child_window = I1.next();


            if (!parent.equals(child_window)) {
                driver.switchTo().window(child_window);

                System.out.println(driver.switchTo().window(child_window).getTitle());
                driver.close();
            }
            driver.switchTo().window("Payment Gateway");
        }*/


        saveTextLog("Generate Success Transaction");
        Thread.sleep(600);
        saveTextLog("Transferred to the next windw");



    }





/*//waitForElementXpath(driver, "//*[@id=\"avantgarde\"]/form/div/div/div/div/div/div/div/div/div/button[1]/i");
        driver.switchTo().
        clickByXpath(driver,"//*[@id=\"avantgarde\"]/form/div/div/div/div/div/div/div/div/div/button[1]/i");
        saveTextLog("Transaction Done successfully");
        Thread.sleep(1000);
        saveTextLog("Transaction Genearted Window Opened");
        Thread.sleep(500);
    }*/



       /* String statuus, ordderId;
        statuus = driver.findElement(By.xpath("//*[@id=\"cs-main-body\"]/div/div/h2")).getAttribute("innerText");
        saveTextLog("Successful status is printed");
        Thread.sleep(1000);
        if (driver.findElement(By.xpath("//*[@id=\\\"cs-main-body\\\"]/div/div/h2")).isDisplayed()) {
            statuus = driver.findElement(By.xpath("//div[@class=\"sp-response-message\"]/h6")).getAttribute("innerText");
            ordderId = driver.findElement(By.xpath("//*[@id=\"cs-main-body\"]/div/div/div/div[4]/div[1]/div/div")).getText();
            Thread.sleep(700);
        } else {
            saveTextLog("Failed Transaction error");
        }*/

        /*String expected_url = "https://pguat.safexpay.com/DOM_jsSim/";
        String current_url = driver.getCurrentUrl();
        Assert.assertTrue(expected_url.equals(current_url), "URL does not match\n");
        Thread.sleep(2000);
        System.out.println(" Successful Transaction Page is open\n");*/


    //----------------------JSCheckoutCArdPAymentJS---------------
    @Test(priority = 16, description = "JS Checkout CardPayment")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test is for JS Checkout CardPayment transactions")
    public void jSCardPaymenttransaction() throws Exception {
        boolean testFail = false;
        ReadFromCSV portalInfo = new ReadFromCSV(System.getProperty("user.dir") + "\\Configuration_Files\\Transactions\\Payment Portals\\JS_Checkout.csv");
        String jscheckPortalUrl = portalInfo.ReadLineNumber(1)[0];
        driver.get(jscheckPortalUrl);
        driver.get(jscheckPortalUrl);
        waitForElementXpath(driver, "//*[@id=\"meid\"]");
        Thread.sleep(2000);
        sendKeysByXpath(driver, "//*[@id=\"meid\"]", "202107080012");
        saveTextLog("Merchant Id is printed");
        Thread.sleep(2000);
        sendKeysByXpath(driver, "//*[@id=\"key\"]", "i7ob6QGEM32VK4as4D1T0zjpnNAaZGE3p5P4nLU7OSk=");
        saveTextLog("Merchant Key is printed");
        Thread.sleep(2000);
        int amount = createRandomNum(10, 50);
        driver.findElement(By.xpath("//*[@id=\"amount\"]")).clear();
        sendKeysByXpath(driver, "//*[@id=\"amount\"]", Integer.toString(amount));
        saveTextLog("Amount: " + amount);
        String randomName = getRandomString();
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@type=\"submit\"]");
        saveTextLog("Submit Button Clicked");
        Thread.sleep(1000);
        driver.findElement(By.xpath("//button[normalize-space()='Buy']")).click();
        saveTextLog("Clicked on Buy Button");
        Thread.sleep(2000);
        waitForElementXpath(driver, "//*[@id=\"main-div\"]/div/div");
        Thread.sleep(2000);
        clickByXpath(driver, "//li[@data-id='card']");
        saveTextLog("Clicked on Card");
        Thread.sleep(700);
        clickByXpath(driver, "//*[@id=\"cr_no\"]");
        saveTextLog("clicked on Card number");
        sendKeysByXpath(driver, "/html/body/div[2]/div[1]/div/div/div/div/div/div[2]/div/div[2]/div[1]/div[2]/div[1]/div/div[1]/div/input", "4242424242424242");
        saveTextLog(" Card number entered successfully");
        Thread.sleep(760);
        clickByXpath(driver, "//input[@id='exp']");
        sendKeysByXpath(driver, "/html[1]/body[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/input[1]", "05/22");
        Thread.sleep(1000);
        saveTextLog("Entered expiry Date");
        clickByXpath(driver, "//*[@id=\"cvcpwd\"]");
        sendKeysByXpath(driver, "/html[1]/body[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[2]/div[2]/input[1]", "123");
        Thread.sleep(2000);
        saveTextLog("Entered CVV Number");
        clickByXpath(driver, "//*[@id=\"sp-footer-btn\"]");
        saveTextLog("clicked on Payment button");
        Thread.sleep(700);
        waitForElementXpath(driver, "//button[normalize-space()='Generate Success Transaction']");
        clickByXpath(driver, "//button[normalize-space()='Generate Success Transaction']");
        Thread.sleep(1000);
        String statuus, ordderId;
        statuus = driver.findElement(By.xpath("//*[@id=\"cs-main-body\"]/div/div/h2")).getAttribute("innerText");
        saveTextLog("Successful status is printed");
        Thread.sleep(1000);

    }

    //-------------------------transactionJUPIPaymode--------------
    @Test(priority = 17, description = "JS transactionJUPIPaymode")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test is for JS transaction UPI Paymode")
    public void jsUPIPaymodetransaction() throws Exception {
        boolean testFail = false;
        ReadFromCSV portalInfo = new ReadFromCSV(System.getProperty("user.dir") + "\\Configuration_Files\\Transactions\\Payment Portals\\JS_Checkout.csv");
        String jscheckPortalUrl = portalInfo.ReadLineNumber(1)[0];
        driver.get(jscheckPortalUrl);
        driver.get(jscheckPortalUrl);
        waitForElementXpath(driver, "//*[@id=\"meid\"]");
        Thread.sleep(2000);
        sendKeysByXpath(driver, "//*[@id=\"meid\"]", "202107080012");
        saveTextLog("Merchant Id is printed");
        Thread.sleep(2000);
        sendKeysByXpath(driver, "//*[@id=\"key\"]", "i7ob6QGEM32VK4as4D1T0zjpnNAaZGE3p5P4nLU7OSk=");
        saveTextLog("Merchant Key is printed");
        Thread.sleep(2000);
        int amount = createRandomNum(10, 50);
        driver.findElement(By.xpath("//*[@id=\"amount\"]")).clear();
        sendKeysByXpath(driver, "//*[@id=\"amount\"]", Integer.toString(amount));
        saveTextLog("Amount: " + amount);
        String randomName = getRandomString();
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@type=\"submit\"]");
        saveTextLog("Submit Button Clicked");
        Thread.sleep(1000);
        driver.findElement(By.xpath("//button[normalize-space()='Buy']")).click();
        saveTextLog("Clicked on Buy Button");
        Thread.sleep(2000);
        //waitForElementXpath(driver,"//*[@id=\"main-div\"]/div/div");
        //Thread.sleep(2000);
        scrollToCenterXpath(driver, "//li[@data-id='upi']");
        clickByXpath(driver, "//li[@data-id='upi']");
        saveTextLog("UPI selected");
        Thread.sleep(800);
        clickByXpath(driver, "//body/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[2]/div[4]/div[1]/div[1]/div[1]/div[2]/div[1]");
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@id=\"upiMobileNo\"]");
        sendKeysByXpath(driver, "/html[1]/body[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[2]/div[4]/div[1]/div[2]/div[1]/div[1]/input[1]", "8698017135");
        saveTextLog("Mobile Number Added");
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@id=\"sp-footer-btn\"]");
        saveTextLog("clicked on Payment button");
        Thread.sleep(700);

        String statuus, ordderId;
        statuus = driver.findElement(By.xpath("//*[@id=\"cs-main-body\"]/div/div/h2")).getAttribute("innerText");
        saveTextLog("Successful status is printed");
        Thread.sleep(1000);
        if (driver.findElement(By.xpath("//*[@id=\\\"cs-main-body\\\"]/div/div/h2")).isDisplayed()) {
            statuus = driver.findElement(By.xpath("//div[@class=\"sp-response-message\"]/h6")).getAttribute("innerText");
            ordderId = driver.findElement(By.xpath("//*[@id=\"cs-main-body\"]/div/div/div/div[4]/div[1]/div/div")).getText();
            Thread.sleep(700);
        } else {
            saveTextLog("Failed Transaction error");
        }
        String expected_url = "https://pguat.safexpay.com/DOM_jsSim/";
        String current_url = driver.getCurrentUrl();
        Assert.assertTrue(expected_url.equals(current_url), "URL does not match\n");
        Thread.sleep(2000);
        System.out.println(" Successful Transaction Page is open\n");


    }


//----------------------------JSOtherPaymode----------------------

    @Test(priority = 18, description = "JS Checkout transactionJSOtherPaymode")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test is for simulating transactions")
    public void jSOtherPaymodetransaction() throws Exception {
        boolean testFail = false;
        ReadFromCSV portalInfo = new ReadFromCSV(System.getProperty("user.dir") + "\\Configuration_Files\\Transactions\\Payment Portals\\JS_Checkout.csv");
        String jscheckPortalUrl = portalInfo.ReadLineNumber(1)[0];
        driver.get(jscheckPortalUrl);
        driver.get(jscheckPortalUrl);
        waitForElementXpath(driver, "//*[@id=\"meid\"]");
        Thread.sleep(2000);
        sendKeysByXpath(driver, "//*[@id=\"meid\"]", "202107080012");
        saveTextLog("Merchant Id is printed");
        Thread.sleep(2000);
        sendKeysByXpath(driver, "//*[@id=\"key\"]", "i7ob6QGEM32VK4as4D1T0zjpnNAaZGE3p5P4nLU7OSk=");
        saveTextLog("Merchant Key is printed");
        Thread.sleep(2000);
        int amount = createRandomNum(10, 50);
        driver.findElement(By.xpath("//*[@id=\"amount\"]")).clear();
        sendKeysByXpath(driver, "//*[@id=\"amount\"]", Integer.toString(amount));
        saveTextLog("Amount: " + amount);
        String randomName = getRandomString();
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@type=\"submit\"]");
        saveTextLog("Submit Button Clicked");
        Thread.sleep(1000);
        driver.findElement(By.xpath("//button[normalize-space()='Buy']")).click();
        saveTextLog("Clicked on Buy Button");
        Thread.sleep(2000);
        scrollToCenterXpath(driver, "//*[@id=\"main-div\"]/div/div/div/div/div/div[2]/div/div[2]/div[1]/div[1]/ul/li[3]");
        //driver.findElement(By.xpath("//button[normalize-space()='Buy']")).click();
        clickByXpath(driver, "//*[@id=\"main-div\"]/div/div/div/div/div/div[2]/div/div[2]/div[1]/div[1]/ul/li[5]");
        saveTextLog("Other Payment Method Selected");
        Thread.sleep(2000);
        clickByXpath(driver, "//body/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[2]/div[5]/div[1]/ul[1]/li[1]");
        saveTextLog("Cash Option Selected Successfully");
        Thread.sleep(1500);
        clickByXpath(driver, "//*[@id=\"x_customer_email\"]");
        sendKeysByXpath(driver, "//input[@id='x_customer_email']", "swaraa1792@gmail.com");
        saveTextLog("Email added");
        Thread.sleep(700);
        clickByXpath(driver, "//*[@id=\"googlePayMobileNo\"]");
        sendKeysByXpath(driver, "//input[@id='googlePayMobileNo']", "8698017135");
        saveTextLog("Phone number added to Payment");
        Thread.sleep(900);
        clickByXpath(driver, "//*[@id=\"x_customer_shipping_zip\"]");
        sendKeysByXpath(driver, "//input[@id='x_customer_shipping_zip']", "411017");
        saveTextLog("ZipCode added Successfully");
        Thread.sleep(900);
        clickByXpath(driver, "//*[@id=\"sp-footer-btn\"]");
        saveTextLog("clicked on Payment button");
        Thread.sleep(1000);

        String statuus, ordderId;
        statuus = driver.findElement(By.xpath("//*[@id=\"cs-main-body\"]/div/div/h2")).getAttribute("innerText");
        saveTextLog("Successful status is printed");
        Thread.sleep(1000);
        if (driver.findElement(By.xpath("//*[@id=\\\"cs-main-body\\\"]/div/div/h2")).isDisplayed()) {
            statuus = driver.findElement(By.xpath("//div[@class=\"sp-response-message\"]/h6")).getAttribute("innerText");
            ordderId = driver.findElement(By.xpath("//*[@id=\"cs-main-body\"]/div/div/div/div[4]/div[1]/div/div")).getText();
            Thread.sleep(700);
        } else {
            saveTextLog("Failed Transaction error");
        }
        String expected_url = "https://pguat.safexpay.com/DOM_jsSim/";
        String current_url = driver.getCurrentUrl();
        Assert.assertTrue(expected_url.equals(current_url), "URL does not match\n");
        Thread.sleep(2000);
        System.out.println(" Successful Transaction Page is open\n");


    }

    //-------------SETTLEMENT----------------------------------
    @Test(priority = 19, description = "Settlement")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Settlement")
    public void settleMent() throws Exception {
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\Admin_Credentials.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to Maker Account");
        login(credential[1], credential[3]);
        try {
            //excepMIS();
            //payoutMIS();
            setMent();
        } catch (Exception e) {
            Assert.fail("Settlement Failed");
        }
    }

    // region settle checking method
    @Step("Settlement Checking")
    public void setMent() throws Exception {
        boolean testFail = false;
        scrollToViewXpath(driver, "//li[@id='js-side-menu-9']");
        saveTextLog("scrolling Page");
        waitAndClickByXpath(driver, "//span[contains(text(),'Settlement')]");
        saveTextLog("Settlement Selected");
        Thread.sleep(400);
        clickByXpath(driver, "//a[contains(text(),'Settlement MIS')]"); // Navigate to Settlement Option
        saveTextLog("Settlement MIS Selected");
        Thread.sleep(2000);

        waitAndClickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[2]/form/div[3]/div[1]/div[1]/div/div/span/button");
        saveTextLog("Clicked on Calendar Button For select Date From");
        Thread.sleep(200);

        waitAndClickByXpath(driver,"//body[1]/div[1]/div[1]/div[2]/form[1]/div[3]/div[1]/div[1]/div[1]/div[1]/div[1]/ul[1]/li[1]/div[1]/table[1]/tbody[1]/tr[1]/td[4]/button[1]/span[1]");
        Thread.sleep(500);
        saveTextLog("Date Selected Successfully");
        Thread.sleep(200);

        waitAndClickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[2]/form/div[3]/div[1]/div[2]/div/div/span/button");
        saveTextLog("Clicked on Calendar Button For To");
        Thread.sleep(200);

        waitAndClickByXpath(driver,"//body[1]/div[1]/div[1]/div[2]/form[1]/div[3]/div[1]/div[2]/div[1]/div[1]/div[1]/ul[1]/li[1]/div[1]/table[1]/tbody[1]/tr[2]/td[6]/button[1]");
        Thread.sleep(500);
        saveTextLog("Date Selected Successfully in To Column");
        Thread.sleep(200);

        waitAndClickByXpath(driver, "//button[normalize-space()='Submit']");
        saveTextLog("Clicked on Submit Button");
        Thread.sleep(900);



        /*saveTextLog(" clicking on Date");
        Thread.sleep(700);
        clickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[2]/form/div[3]/div[1]/div[1]/div/div/input");
        WebElement dteCkurntAG = driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div[2]/form/div[3]/div[1]/div[1]/div/div/span/button"));
        dteCkurntAG.click();
        //identify all td elements in list
        List<WebElement> dtheElement = driver.findElements(By.xpath("//*[@id=\"datepicker-265-4752-8\"]/button/span"));
        //list traversal
        for (int k = 0; k < dtheElement.size(); k++) {
            //check date
            String dt = dtheElement.get(k).getText();
            if (dt.equals("2")) {
                dtheElement.get(k).click();
                break;
            }
        }
        saveTextLog("Date seletcted from Column");
        Thread.sleep(1000);


        //driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div[2]/form/div[3]/div[1]/div[2]/div/div/input"));

        clickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[2]/form/div[3]/div[1]/div[2]/div/div/input");
        WebElement dteNextDK = driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div[2]/form/div[3]/div[1]/div[2]/div/div/span/button/i"));
        dteNextDK.click();
        //identify all td elements in list
        List<WebElement> dteEleemment = driver.findElements(By.xpath("//*[@id=\"datepicker-509-7251-18\"]/button/span"));
        //list traversal
        for (int k = 0; k < dteEleemment.size(); k++) {
            //check date
            String dt = dteEleemment.get(k).getText();
            if (dt.equals("2")) {
                dteEleemment.get(k).click();
                break;
            }
        }
        saveTextLog("Date seletcted from Column");
        Thread.sleep(500);*/




    }



        /*String messageLM = driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div[1]/div/div[2]/button")).getText();
        Screenshot(driver, "SnackBar Message: " + messageLM);
        Thread.sleep(1200);*/

//endregion

    // region payout MIS checking method
/*@Step("payout MIS")
public void payoutMIS() throws AWTException, InterruptedException {
    boolean testFail = false;
    scrollToViewXpath(driver, "//*[@id=\"js-side-menu-10\"]/a");
    saveTextLog("scrolling Page");
    Thread.sleep(500);
    waitAndClickByXpath(driver,"//*[@id=\"js-side-menu-11\"]/a/span[2]/span");
    clickByXpath(driver, "//*[@id=\"js-side-menu-11\"]/ul/li[3]/a");
    saveTextLog("Payout MIS Settlement Selected");
    Thread.sleep(900);
    sendKeysByXpath(driver, "//input[@id='merchantNameLike']", "PUMA");
    saveTextLog("Name is Added");
    Thread.sleep(2000);
    clickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[4]/div/div/div[2]/div/div/div");
    saveTextLog("Clicking On Date");
    WebElement dtePaMIsPP = driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div[4]/div/div/div[2]/div/div/span/button"));
    dtePaMIsPP.click();
    //identify all td elements in list
    List<WebElement> dtePayout = driver.findElements(By.xpath("//*[@id=\"datepicker-186-1114-16\"]/button"));
    //list traversal
    for (int k = 0; k <= dtePayout.size(); k++) {
        //check date
        String dt = dtePayout.get(k).getText();
        if (dt.equals("2")) {
            dtePayout.get(k).click();
            break;
        }
    }
    saveTextLog("Date seletcted from Column");
    Thread.sleep(1000);

    clickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[4]/div/div/div[3]/div/div/input");
    saveTextLog("clicked on input");

    WebElement dtePayouMIS = driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div[4]/div/div/div[3]/div/div/span/button/i"));
    dtePayouMIS.click();
    //identify all td elements in list
    List<WebElement> dteelementPayouMIS = driver.findElements(By.xpath("//*[@id=\"datepicker-308-1902-24\"]/button/span"));
    //list traversal
    for (int k = 0; k < dteelementPayouMIS.size(); k++) {
        //check date
        String dt = dteelementPayouMIS.get(k).getText();
        if (dt.equals("2")) {
            dteelementPayouMIS.get(k).click();
            break;
        }
    }
    saveTextLog("Date seletcted To  Column");
    Thread.sleep(500);

    driver.findElement(By.xpath("//*[@id=\"heading-action-wrapper\"]/div/div/div[2]/button")).click();
    saveTextLog("Clicked on Submit Button Successfully");
    Thread.sleep(1000);

    scrollToViewXpath(driver,"//*[@id=\"avantgarde\"]/div[1]/div/div[5]/div");
    saveTextLog("Scroll Down Page");
    driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div[5]/div/div[1]/table/tbody/tr[1]/td[11]/a")).click();
    Thread.sleep(700);*/
//endregion


/*@Step("Settlement Checking")
    public void excepMIS() throws Exception {
        boolean testFail = false;
        scrollToViewXpath(driver, "//*[@id=\"js-side-menu-10\"]/a");
        saveTextLog("scrolling Page");
        clickByXpath(driver, "//*[@id=\"js-side-menu-11\"]/ul/li[4]/a");
        saveTextLog("Exception Mis selected");
        Thread.sleep(750);*/


    /*driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div[5]/div/div[1]/table/tbody/tr[1]/td[8]/input")).click();
    saveTextLog("Amount Paid is activated");
    Thread.sleep(5000);*/







    /*saveTextLog("selected Date displayed");
    WebElement dateBbox = driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div[4]/div/div/div[2]/div/div/span/button"));
    dateBbox.sendKeys("12122021");
    saveTextLog("12122021 Date dispalyed");*/

    /*driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div[5]/div/div[1]/table/tbody/tr[1]/td[8]/input")).click();
    saveTextLog("Amount Paid is activated");
    Thread.sleep(5000);*/




















    /*clickByXpath(driver,"//*[@id=\"avantgarde\"]/div[1]/div/div[4]/div/div/div[2]/div/div/input");
    waitAndClickByXpath(driver, "//*[@id=\"datepicker-302-1985-17\"]/button");
    driver.findElement(By.tagName("td")).click();
    Thread.sleep(1000);
    saveTextLog("Date Selected");
    Thread.sleep(1000);*/






    /*WebElement dateWift = driver.findElement(By.xpath("8-16\"]/button"));
    List<WebElement> columns=dateWift.findElements(By.tagName(""));

    for (WebElement cell: columns) {
        //Select 13th Date
        if (cell.getText().equals("13")) {
            cell.findElement(By.linkText("13")).click();
            break;
        }*/




    /*saveTextLog(" clicking on Date");
    Thread.sleep(700);
    clickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[4]/div/div/div[2]/div/div/input");
    WebElement dtePaMIsPP = driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div[4]/div/div/div[2]/div/div/span/button"));
    dtePaMIsPP.click();
    //identify all td elements in list
    List<WebElement> dtePayout = driver.findElements(By.xpath("//*[@id=\"datepicker-186-1114-16\"]/button"));
    //list traversal
    for (int k = 0; k <= dtePayout.size(); k++) {
        //check date
        String dt = dtePayout.get(k-12).getText();
        if (dt.equals("2")) {
            dtePayout.get(k).click();
            break;
        }
    }
    saveTextLog("Date seletcted from Column");
    Thread.sleep(1000);*/


        /*clickByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[4]/div/div/div[3]/div/div/input");
        WebElement dtePayouMIS = driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div[4]/div/div/div[3]/div/div/span/button/i"));
        dtePayouMIS.click();
        //identify all td elements in list
        List<WebElement> dteelementPayouMIS = driver.findElements(By.xpath("//*[@id=\"datepicker-308-1902-24\"]/button/span"));
        //list traversal
        for (int k = 0; k < dteelementPayouMIS.size(); k++) {
            //check date
            String dt = dteelementPayouMIS.get(k).getText();
            if (dt.equals("2")) {
                dteelementPayouMIS.get(k).click();
                break;
            }
        }
        saveTextLog("Date seletcted To  Column");
        Thread.sleep(500);

        driver.findElement(By.xpath("//*[@id=\"heading-action-wrapper\"]/div/div/div[2]/button")).click();
        Thread.sleep(1000);
        saveTextLog("Clicked on Submit Button");
        Thread.sleep(1000);
        scrollToViewXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[5]/div");
        Thread.sleep(700);





    wait.until(ExpectedConditions.visibilityOfElementLocated((By.cssSelector("body.body_bgclr.ng-scope:nth-child(2) div.ng-scope:nth-child(5) div.main.ng-scope:nth-child(5) div.panel.panel-default:nth-child(4) div.panel-body div.row div.col-lg-3.col-md-3.col-sm-3.col-xs-12:nth-child(2) div.form-group.no-margin-hr div.input-group div.ng-valid.ng-valid-date-disabled.ng-dirty ul.uib-datepicker-popup.dropdown-menu.ng-scope div.uib-datepicker.ng-isolate-scope table.uib-daypicker tbody:nth-child(2) tr.uib-weeks.ng-scope:nth-child(2) td.uib-day.text-center.ng-scope:nth-child(5) button.btn.btn-default.btn-sm > span.ng-binding"))));
    List<WebElement> cellsOfDepartureDate = wait.until(
            ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("section:nth-of-type(1) > .lightpick__days > div")));*/


    //--------------------------------------------MIS-----------------------------------------------------------
    @Test(priority = 20, description = "MIS")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Transaction MIS")
    public void transactionMIS() throws Exception {
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\Admin_Credentials.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to Maker Account");
        login(credential[1], credential[3]);
        try {
            transactionnMISOrderID();
            transactionnMISforAgREF();

        } catch (Exception e) {
            Assert.fail("Transaction MIS Failed");
        }
    }

    @Step("Transaction MIS Download- Search with Order ID")
    public void transactionnMISOrderID() throws Exception {
        boolean testFail = false;
        waitAndClickByXpath(driver, "//*[@id=\"js-side-menu-5\"]/a");
        clickWithJavaScriptByXpath(driver, "//a[normalize-space()='Transaction MIS']"); // Navigate to Transaction MIS Download
        saveTextLog("Transaction MIS Selected");
        Thread.sleep(2000);
        clickByXpath(driver, "/html/body/div[1]/div/div[1]/div[2]/div/div/div[1]/div/div");
        saveTextLog("selected specific type");
        Thread.sleep(800);
        clickByXpath(driver, "/html/body/div[4]/div/input");  //Select order type
        clickByXpath(driver, "/html/body/div[4]/ul/li[2]");
        Thread.sleep(800);
        saveTextLog("Order Number successfully");
        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[1]/div[2]/div/div/div[2]/div/div/input", "14765");
        clickByXpath(driver, "/html/body/div[1]/div/div[1]/div[2]/div/div/div[2]/div/div/span/button");
        saveTextLog("order number searched successfully");
        Thread.sleep(800);

        String status_data = driver.findElement(By.xpath("//*[@id=\"transactions\"]/tbody/tr[1]")).getText();
        System.out.println("The Date is - " + status_data);
        Thread.sleep(900);

        Assert.assertEquals("14765", driver.findElement(By.xpath("//*[@id=\"transactions\"]/tbody/tr[1]/td[7]")).getText());
        System.out.println(driver.findElement(By.xpath("//*[@id=\"transactions\"]/tbody/tr[1]/td[7]")).getText());
        saveTextLog("ORDER-ID Number Matched");

        driver.findElement(By.xpath("//button[normalize-space()='Download Reports']"));
        clickByXpath(driver, "/html/body/div[1]/div/div[1]/div[1]/div/div/div/button");// Download Excl
        saveTextLog("Clicked on Download Report");
        Thread.sleep(800);



    }

    @Step("Transaction MIS Download- Search with AGREF")
    public void transactionnMISforAgREF() throws Exception {
        boolean testFail = false;
        waitAndClickByXpath(driver, "//*[@id=\"js-side-menu-5\"]/a");
        clickWithJavaScriptByXpath(driver, "//a[normalize-space()='Transaction MIS']"); // Navigate to Transaction MIS Download
        saveTextLog("Transaction MIS Selected");
        Thread.sleep(2000);
        clickByXpath(driver, "/html/body/div[1]/div/div[1]/div[2]/div/div/div[1]/div/div");
        saveTextLog("selected specific type");
        Thread.sleep(800);
        clickByXpath(driver, "/html/body/div[4]/div/input");  //Select order type
        clickByXpath(driver, "//*[@id=\"select2-drop\"]/ul/li[1]");
        Thread.sleep(800);
        saveTextLog("AG Number Enter successfully");
        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[1]/div[2]/div/div/div[2]/div/div/input", "1031341644414430125");
        clickByXpath(driver, "/html/body/div[1]/div/div[1]/div[2]/div/div/div[2]/div/div/span/button");
        saveTextLog("AGREF number searched successfully");
        Thread.sleep(800);

        String status_daata = driver.findElement(By.xpath("//*[@id=\"transactions\"]/tbody/tr[1]")).getText();
        System.out.println("The Data  is - " + status_daata);
        Thread.sleep(900);

        Assert.assertEquals("1031341644414430125", driver.findElement(By.xpath("//*[@id=\"transactions\"]/tbody/tr[1]/td[4]")).getText());
        System.out.println(driver.findElement(By.xpath("//*[@id=\"transactions\"]/tbody/tr[1]/td[4]")).getText());
        saveTextLog("Ag-Ref Number Matched");

        driver.findElement(By.xpath("//button[normalize-space()='Download Reports']"));
        clickByXpath(driver, "/html/body/div[1]/div/div[1]/div[1]/div/div/div/button");// Download Excl
        saveTextLog("Clicked on Download Report");
        Thread.sleep(800);

    }


//---------------------------MERCHANT PORTAL- Merchant DashBoard-------------------------------

    @Test(priority = 21, description = "Merchant Portal- Merchant Dashboard")
    @Severity(SeverityLevel.CRITICAL)
    @Description(" Merchant Portal- Merchant Dashboard ")
    public void merchantDashboard() throws Exception {
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\MerchantPortalUrl.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to Merchant  Account");
        login(credential[1], credential[2]);
        try {
            merchantSimpleDashboard();
        } catch (Exception e) {
            Assert.fail("Merchant Dashboard Failed");
        }
    }

    @Step("Merchant Dashboard Selection")
    public void merchantSimpleDashboard() throws Exception {
        waitAndClickByXpath(driver, "//body/div[1]/div[1]/div[1]/ul[1]/li[1]/a[1]");
        saveTextLog("Clicked on MIS");
        Thread.sleep(500);
        driver.findElement(By.xpath("//a[contains(text(),'Dashboard')]")).click();
        saveTextLog("Clicked on Dashboard");
        Thread.sleep(600);
        String hrHeading = driver.findElement(By.xpath("//body/div[1]/div[1]/div[2]/div[2]/div[3]/div[1]/div[1]/div[1]/h1[1]")).getText();
        System.out.println("Page Heading as - " + hrHeading);
        Thread.sleep(700);


        driver.findElement(By.xpath("//*[@id=\"countActive\"]/a")).click();
        //saveTextLog("Transaction COunt is :52");
        String trCount = driver.findElement(By.xpath("//*[@id=\"countActive\"]/a")).getText();
        System.out.println("The Transaction Count  is - " + trCount);
        Thread.sleep(800);


        clickByXpath(driver, "//*[@id=\"amountActive\"]/a");
        String trAmt = driver.findElement(By.xpath("//li[@id='amountActive']")).getText();
        System.out.println("The Transaction Amount- " + trAmt);
        Thread.sleep(500);

        driver.findElement(By.xpath("//*[@id=\"chargesActive\"]/a")).click();
        //saveTextLog("Displayed Transaction Charges: 29.73");
        String trChrges = driver.findElement(By.xpath("//body/div[1]/div[1]/div[2]/div[2]/div[4]/div[1]/div[1]/div[1]/ul[1]/li[3]/a[1]")).getText();
        System.out.println("The Transaction Charges is - " + trChrges);
        Thread.sleep(700);


    }

    //--------------Merchant Portal- Merchant Transaction MIS-----------------------
    @Test(priority = 22, description = "Merchant Portal- Merchant Transaction MIS")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Merchant Portal- Merchant Transaction MIS ")
    public void merchantTransactionMISSearch() throws Exception {
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\MerchantPortalUrl.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to Merchant  Account");
        login(credential[1], credential[2]);
        try {
            merchantTransactionMISSearchWithPgID();
            merchantTransactionMISSearchWithOrderNo();
            merchantTransactionMISSearchWithTransactionAGRef();

        } catch (Exception e) {
            Assert.fail("Merchant Transaction MIS Failed");
        }
    }


    @Step("PG_ID Searching")
    public void merchantTransactionMISSearchWithPgID() throws Exception {
        driver.findElement(By.xpath("//body/div[1]/div[1]/div[1]/ul[1]/li[1]/a[1]")).click();
        saveTextLog("Clicked on MIS");
        Thread.sleep(500);
        clickByXpath(driver, "//a[contains(text(),'Transaction MIS')]");
        saveTextLog("Clicked on Transaction MIS Button");
        Thread.sleep(700);


        driver.findElement(By.xpath("//body[1]/div[1]/div[1]/div[2]/div[2]/div[3]/div[1]/div[1]/div[1]/div[2]/div[1]/div[2]/button[1]")).click();
        Thread.sleep(700);


        driver.findElement(By.xpath("//div[@id='s2id_autogen1']")).click();
        saveTextLog("Search button Clicked");
        Thread.sleep(800);


        clickByXpath(driver, "//body/div[@id='select2-drop']/ul[1]/li[3]/div[1]");
        saveTextLog("PG_ID Selected");
        Thread.sleep(700);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/div[3]/div[1]/div[1]/div[1]/div[2]/div[1]/div[4]/div[1]/div[1]/input[1]", "63");
        saveTextLog("Data Entered");
        Thread.sleep(700);


        driver.findElement(By.xpath("//button[contains(text(),'Search')]")).click();
        saveTextLog("PG_ID Addded");
        Thread.sleep(700);

        driver.findElement(By.xpath("//body/div[1]/div[1]/div[2]/div[2]/div[3]/div[1]/div[1]/div[1]/div[2]/div[1]/div[2]/button[1]")).click();
        Thread.sleep(700);

    }

    @Step("Order_number Searching")
    public void merchantTransactionMISSearchWithOrderNo() throws Exception {
        driver.findElement(By.xpath("//body/div[1]/div[1]/div[1]/ul[1]/li[1]/a[1]")).click();
        saveTextLog("Clicked on MIS");
        Thread.sleep(500);
        waitAndClickByXpath(driver, "//a[contains(text(),'Transaction MIS')]");
        saveTextLog("Clicked on Transaction MIS Button");
        Thread.sleep(700);

        driver.findElement(By.xpath("//div[@id='s2id_autogen1']")).click();
        saveTextLog("Search button Clicked");
        Thread.sleep(800);

        clickByXpath(driver, "//body/div[@id='select2-drop']/ul[1]/li[2]");
        saveTextLog("Order_ID Selected");
        Thread.sleep(700);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/div[3]/div[1]/div[1]/div[1]/div[2]/div[1]/div[4]/div[1]/div[1]/input[1]", "18050");
        Thread.sleep(700);

        driver.findElement(By.xpath("//button[contains(text(),'Search')]")).click();
        saveTextLog("OrderID Addded");
        Thread.sleep(700);

        driver.findElement(By.xpath("//body[1]/div[1]/div[1]/div[2]/div[2]/div[3]/div[1]/div[1]/div[1]/div[2]/div[1]/div[2]/button[1]")).click();
        Thread.sleep(700);


    }

    @Step("Merchant Transaction MIS- AGREF Searching")
    public void merchantTransactionMISSearchWithTransactionAGRef() throws Exception {
        driver.findElement(By.xpath("//body/div[1]/div[1]/div[1]/ul[1]/li[1]/a[1]")).click();
        saveTextLog("Clicked on MIS");
        Thread.sleep(500);
        clickByXpath(driver, "//a[contains(text(),'Transaction MIS')]");
        saveTextLog("Clicked on Transaction MIS Button");
        Thread.sleep(700);
        driver.findElement(By.xpath("//div[@id='s2id_autogen1']")).click();
        saveTextLog("Serch button Clicked");
        Thread.sleep(800);
        clickByXpath(driver, "//body/div[@id='select2-drop']/ul[1]/li[1]/div[1]");
        saveTextLog("Ag Ref Selected");
        Thread.sleep(700);
        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/div[3]/div[1]/div[1]/div[1]/div[2]/div[1]/div[4]/div[1]/div[1]/input[1]", "1031291641977513390");
        Thread.sleep(700);
        driver.findElement(By.xpath("//button[contains(text(),'Search')]")).click();
        saveTextLog("Ag Ref number Addded");
        Thread.sleep(700);


        driver.findElement(By.xpath("//button[contains(text(),'Download Reports')]")).click();
        saveTextLog("Clicked on Download Reports");
        Thread.sleep(1000);

    }


//--------------- MerchantTransactionMISDownloads------------------


    @Test(priority = 23, description = " Merchant Transaction MIS Download")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Merchant Portal")
    public void merchantTransactionMISDownloads() throws Exception {
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\MerchantPortalUrl.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to Merchant  Account");
        login(credential[1], credential[2]);
        try {
            merchantTransactionMISCSVDownload();
        } catch (Exception e) {
            Assert.fail("Merchant Download Failed");
        }
    }

    @Step("Merchant Download Report For CSV Download")
    public void merchantTransactionMISCSVDownload() throws Exception {
        driver.findElement(By.xpath("//body/div[1]/div[1]/div[1]/ul[1]/li[1]/a[1]")).click();
        saveTextLog("Clicked on MIS");
        Thread.sleep(500);
        clickByXpath(driver, "//a[contains(text(),'Transaction MIS')]");
        saveTextLog("Clicked on Transaction MIS Button");
        Thread.sleep(700);
        driver.findElement(By.xpath("//div[@id='s2id_autogen1']")).click();
        saveTextLog("Serch button Clicked");
        Thread.sleep(800);
        clickByXpath(driver, "//body/div[@id='select2-drop']/ul[1]/li[1]/div[1]");
        saveTextLog("Ag Ref Selected");
        Thread.sleep(700);
        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/div[3]/div[1]/div[1]/div[1]/div[2]/div[1]/div[4]/div[1]/div[1]/input[1]", "1031291641977513390");
        Thread.sleep(700);
        driver.findElement(By.xpath("//button[contains(text(),'Search')]")).click();
        saveTextLog("Ag Ref number Addded");
        Thread.sleep(700);
        driver.findElement(By.xpath("//button[contains(text(),'Download Reports')]")).click();
        saveTextLog("Clicked on Download Reports");
        Thread.sleep(1000);


        clickByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/div[8]/div[3]/div[1]/div[1]/div[1]/div[1]/div[1]/input[1]");
        saveTextLog(" clicked on Date");
        Thread.sleep(700);


        waitAndClickByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/div[8]/div[3]/div[1]/div[1]/div[5]/div[1]/div[1]/a[1]");//Adjustment type
        saveTextLog("Download File Type Clicked");
        Thread.sleep(500);
        List<WebElement> oppt = driver.findElements(By.xpath("//body/div[@id='select2-drop']/ul[1]/li[1]/div[1]/span[1]"));
        int selectedDownloadType = oppt.size();
        for (int j = 0; j < oppt.size(); j++) {
            if (oppt.get(j).getText().equals("CSV")) {
                oppt.get(j).click();
                break;
            }
        }
        saveTextLog("CSV selected");
        Thread.sleep(500);


    }




    //------------------Merchant Portal -  Merchant Success Ratio--------------
    @Test(priority = 24, description = "Merchant Portal- Merchant Success Ratio")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Merchant Portal- Merchant Success Ratio")
    public void merchantsuccessR() throws Exception {
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\MerchantPortalUrl.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to Merchant  Account");
        login(credential[1], credential[2]);

        try {
            merchantSuccessRatio();

        } catch (Exception e) {
            Assert.fail("Merchant Success Ratio Failed");
        }
    }

    @Step("Merchant Download Report For Download")
    public void   merchantSuccessRatio() throws Exception {
        driver.findElement(By.xpath("//body/div[1]/div[1]/div[1]/ul[1]/li[1]/a[1]")).click();
        saveTextLog("Clicked on MIS");
        Thread.sleep(500);

        clickByXpath(driver,"//a[contains(text(),'Merchant Success Ratio')]");
        String ms_Ratio = driver.findElement(By.xpath("//a[contains(text(),'Merchant Success Ratio')]")).getText();
        System.out.println("The Page Heading is - " + ms_Ratio);
        Thread.sleep(900);


        driver.findElement(By.xpath("//*[@id=\"reportrange\"]/b")).click();
        saveTextLog("Clicked on DropDown Button");
        Thread.sleep(700);

        waitForElementXpath(driver, "//body/div[3]");
        saveTextLog("Range Selected");
        Thread.sleep(600);

        driver.findElement(By.xpath("//body/div[3]/div[3]/ul[1]/li[2]")).click();
        saveTextLog("Last 7 Days Selected");
        Thread.sleep(700);

        driver.findElement(By.xpath("//button[contains(text(),'Download Report')]")).click();
        saveTextLog("Clicked on Dowload Reports Successfully");
        Thread.sleep(700);


    }



//--------Merchant Portal- Transaction Management-----

    @Test(priority = 25, description = "Merchant Portal- Media Based Payment")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Merchant Portal -Media Based Payment ")
    public void mediaBasedPaymentAll() throws Exception {
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\MerchantPortalUrl.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to Merchant  Account");
        login(credential[1], credential[2]);
        try {
            mediaBasedPaymentsEmail();
            mediaBasedPaymentsSMS();
            mediaBasedPaymentSMSandEmail();
            mediaBasedPaymentQRCode();
        } catch (Exception e) {
            Assert.fail("Merchant Portal Failed");
        }
    }

    @Step("Merchant Portal - Media Based Payments For Email")
    public void mediaBasedPaymentsEmail() throws Exception {
        driver.findElement(By.xpath("//span[contains(text(),'Transaction Management')]")).click();
        saveTextLog("Clicked on Transaction Management");
        Thread.sleep(500);

        clickByXpath(driver, "//*[@id=\"js-side-menu-1\"]/ul/li[2]/a");
        String tm_MBP = driver.findElement(By.xpath("//*[@id=\"js-side-menu-1\"]/ul/li[2]/a")).getText();
        System.out.println("The page is - " + tm_MBP);
        Thread.sleep(900);

        driver.findElement(By.xpath("//*[@id=\"heading-action-wrapper\"]/div/div/div[2]/div/div/button")).click();
        saveTextLog("Clicked on Add New Button");
        Thread.sleep(500);

        waitForElementXpath(driver, "//h4[contains(text(),'Manage Media Based Payment Details')]");
        String tm_MMBPD = driver.findElement(By.xpath("//h4[contains(text(),'Manage Media Based Payment Details')]")).getText();
        System.out.println("The page is - " + tm_MMBPD);
        Thread.sleep(900);


        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[1]/div[1]/div[1]/input[1]", "Kirti");
        Thread.sleep(500);
        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[1]/div[2]/div[1]/input[1]", "Dhotre");//Enter Last Name
        saveTextLog("First and Last Name added");
        Thread.sleep(500);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[1]/div[4]/div[1]/input[1]", "kirtid@safexpay.com");  //Enter Email
        saveTextLog("Entered Email Address");
        Thread.sleep(700);

        clickByXpath(driver, "//*[@id=\"s2id_autogen3\"]/a/span[2]/b");  //Clicks Country Dropdown
        saveTextLog("Clicked Country Dropdown Button");
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@id=\"select2-drop\"]/ul/li[6]/div");  //Selects IND as Country
        saveTextLog("Selected INDIA");
        Thread.sleep(5000);

        clickByXpath(driver, "//*[@id=\"s2id_autogen5\"]/a/span[2]/b");  //Select Currency
        saveTextLog("Clicked Currency Dropdown Button");
        clickByXpath(driver, "//body/div[@id='select2-drop']/ul[1]/li[2]/div[1]");  //Select INR
        saveTextLog("Selected INR as currency");
        JavascriptExecutor jst = (JavascriptExecutor) driver;
        jst.executeScript("window.scrollBy(0,350)", "");
        saveTextLog("Scroll down to Select currency");
        Thread.sleep(700);

        sendKeysByXpath(driver, "//*[@id=\"frmAmt\"]", "4090");  //Enter Amount
        saveTextLog("Amount Entered Successfully");
        Thread.sleep(700);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[1]/div[10]/div[1]/input[1]", "Axis");  //Enter Amount
        saveTextLog("Product name Entered Successfully");
        Thread.sleep(700);

        scrollToCenterXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[1]/div[1]/div[1]/div[1]/h1[1]");
        Thread.sleep(700);

        clickByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[1]/div[1]/div[1]/div[1]/h1[1]");
        Thread.sleep(700);

        driver.findElement(By.cssSelector("#btn1")).click();
        saveTextLog("Submit Button Clicked");
        Thread.sleep(700);


        Assert.assertEquals("kirtid@safexpay.com", driver.findElement(By.cssSelector("body.body_bgclr.ng-scope:nth-child(2) div.container-fluid:nth-child(5) div.row div.main:nth-child(2) div.ng-scope div.ng-scope:nth-child(6) div.card.card-big div.card-content div.table-responsive table.table.table-hover.table-bordered.table-striped.bs-events-table.ng-pristine.ng-untouched.ng-valid tbody:nth-child(2) tr.ng-scope:nth-child(1) > td.word-break.ng-binding:nth-child(4)")).getText());
        System.out.println(driver.findElement(By.cssSelector("body.body_bgclr.ng-scope:nth-child(2) div.container-fluid:nth-child(5) div.row div.main:nth-child(2) div.ng-scope div.ng-scope:nth-child(6) div.card.card-big div.card-content div.table-responsive table.table.table-hover.table-bordered.table-striped.bs-events-table.ng-pristine.ng-untouched.ng-valid tbody:nth-child(2) tr.ng-scope:nth-child(1) > td.word-break.ng-binding:nth-child(4)")).getText());
        saveTextLog("email Matched");
        Thread.sleep(700);

        String messageq3 = driver.findElement(By.xpath("//*[@id=\"btn1\"]")).getText();
        Screenshot(driver, "Email has been Sent Successfully to the customer: " + messageq3);
        Thread.sleep(700);


    }

//---------------------FOR SMS------------------

    @Step("Merchant Portal - Media Based Payments For SMS")
    public void mediaBasedPaymentsSMS() throws Exception {
        driver.findElement(By.linkText("Media Based Payment")).click();
        saveTextLog("Clicked on Transaction Management");
        Thread.sleep(500);

        clickByXpath(driver, "//*[@id=\"js-side-menu-1\"]/ul/li[2]/a");
        String tm_MBP = driver.findElement(By.xpath("//*[@id=\"js-side-menu-1\"]/ul/li[2]/a")).getText();
        System.out.println("The page is - " + tm_MBP);
        Thread.sleep(900);

        driver.findElement(By.xpath("//*[@id=\"heading-action-wrapper\"]/div/div/div[2]/div/div/button")).click();
        saveTextLog("Clicked on Add New Button");
        Thread.sleep(500);

        waitForElementXpath(driver, "//h4[contains(text(),'Manage Media Based Payment Details')]");
        String tm_MMBPD = driver.findElement(By.xpath("//h4[contains(text(),'Manage Media Based Payment Details')]")).getText();
        System.out.println("The page is - " + tm_MMBPD);
        Thread.sleep(900);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[1]/div[1]/div[1]/input[1]", "Swara");
        Thread.sleep(500);
        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[1]/div[2]/div[1]/input[1]", "DK");//Enter Last Name
        saveTextLog("First and Last Name added");
        Thread.sleep(500);

        clickByXpath(driver, "//*[@id=\"s2id_autogen1\"]/a/span[2]/b");
        Thread.sleep(700);
        driver.findElement(By.xpath("//*[@id=\"select2-drop\"]/ul/li[2]")).click();
        saveTextLog("SMS Selected");
        JavascriptExecutor jsk = (JavascriptExecutor) driver;
        jsk.executeScript("window.scrollBy(0,350)", "");
        saveTextLog("Scroll down");
        Thread.sleep(500);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[1]/div[5]/div[1]/input[1]", "8698017135");  //Enter GST NUMBER
        saveTextLog("Entered Mobile Address");
        Thread.sleep(700);

        clickByXpath(driver, "//*[@id=\"s2id_autogen3\"]/a/span[2]/b");  //Clicks Country Dropdown
        saveTextLog("Clicked Country Dropdown Button");
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@id=\"select2-drop\"]/ul/li[6]/div");  //Selects IND as Country
        saveTextLog("Selected INDIA");
        Thread.sleep(5000);

        clickByXpath(driver, "//*[@id=\"s2id_autogen5\"]/a/span[2]/b");  //Select Currency
        saveTextLog("Clicked Currency Dropdown Button");
        //scrollToViewXpath(driver, "/html/body/div[6]/ul/li[2]");
        clickByXpath(driver, "//body/div[@id='select2-drop']/ul[1]/li[2]/div[1]");  //Select INR
        saveTextLog("Selected INR as currency");
        JavascriptExecutor jst = (JavascriptExecutor) driver;
        jst.executeScript("window.scrollBy(0,350)", "");
        saveTextLog("Scroll down after currency");
        Thread.sleep(700);

        sendKeysByXpath(driver, "//*[@id=\"frmAmt\"]", "250");  //Enter Amount
        saveTextLog("Amount Entered Successfully");
        Thread.sleep(700);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[1]/div[10]/div[1]/input[1]", "Axis");  //Enter Amount
        saveTextLog("Product name Entered Successfully");
        Thread.sleep(700);


        scrollToCenterXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[1]/div[1]/div[1]/div[1]/h1[1]");
        Thread.sleep(700);

        clickByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[1]/div[1]/div[1]/div[1]/h1[1]");
        Thread.sleep(700);

        driver.findElement(By.cssSelector("#btn1")).click();
        saveTextLog("Submit Button Clicked");
        Thread.sleep(700);


        String messageSMS = driver.findElement(By.xpath("//*[@id=\"btn1\"]")).getText();
        Screenshot(driver, "SMS has been Sent Successfully to the customer: " +messageSMS);
        Thread.sleep(700);
    }



    //---------------------Media based Payment SMS and Email---------------

    @Step("Merchant Portal - Media Based Payments Email and SMS")
    public void mediaBasedPaymentSMSandEmail() throws Exception {
        driver.findElement(By.xpath("//span[contains(text(),'Transaction Management')]")).click();
        saveTextLog("Clicked on Transaction Management");
        Thread.sleep(500);

        clickByXpath(driver, "//*[@id=\"js-side-menu-1\"]/ul/li[2]/a");
        String tm_MBP = driver.findElement(By.xpath("//*[@id=\"js-side-menu-1\"]/ul/li[2]/a")).getText();
        System.out.println("The page is - " + tm_MBP);
        Thread.sleep(900);

        driver.findElement(By.xpath("//*[@id=\"heading-action-wrapper\"]/div/div/div[2]/div/div/button")).click();
        saveTextLog("Clicked on Add New Button");
        Thread.sleep(500);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[1]/div[1]/div[1]/input[1]", "Hindavi");
        Thread.sleep(500);
        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[2]/div[2]/form/div[2]/div[2]/div/div/div[2]/div/input", "Dhotre");//Enter Last Name
        saveTextLog("First and Last Name added");
        Thread.sleep(500);

        clickByXpath(driver, "//*[@id=\"s2id_autogen1\"]/a/span[2]/b");
        Thread.sleep(700);
        driver.findElement(By.xpath("//*[@id=\"select2-drop\"]/ul/li[3]")).click();
        saveTextLog("Email and SMS Selected");
        JavascriptExecutor jsk = (JavascriptExecutor) driver;
        jsk.executeScript("window.scrollBy(0,350)", "");
        saveTextLog("Scroll down");
        Thread.sleep(500);



        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[1]/div[4]/div[1]/input[1]", "dhotre.kitu17@gmail.com");  //Enter EMAIL-ID
        saveTextLog("Entered Email Address");
        Thread.sleep(700);


        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[1]/div[5]/div[1]/input[1]", "8698017135");  //Enter Mobile NUMBER
        saveTextLog("Entered Mobile Address");
        Thread.sleep(700);

        clickByXpath(driver, "//*[@id=\"s2id_autogen3\"]/a/span[2]/b");  //Clicks Country Dropdown
        saveTextLog("Clicked Country Dropdown Button");
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@id=\"select2-drop\"]/ul/li[6]/div");  //Selects IND as Country
        saveTextLog("Selected INDIA");
        Thread.sleep(5000);

        clickByXpath(driver, "//*[@id=\"s2id_autogen5\"]/a/span[2]/b");  //Select Currency
        saveTextLog("Clicked Currency Dropdown Button");
        //scrollToViewXpath(driver, "/html/body/div[6]/ul/li[2]");
        clickByXpath(driver, "//body/div[@id='select2-drop']/ul[1]/li[2]/div[1]");  //Select INR
        saveTextLog("Selected INR as currency");
        JavascriptExecutor jst = (JavascriptExecutor) driver;
        jst.executeScript("window.scrollBy(0,350)", "");
        saveTextLog("Scroll down after currency");
        Thread.sleep(700);

        sendKeysByXpath(driver, "//*[@id=\"frmAmt\"]", "215");  //Enter Amount
        saveTextLog("Amount Entered Successfully");
        Thread.sleep(700);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[1]/div[10]/div[1]/input[1]", "Dress");  //Enter Amount
        saveTextLog("Product name Entered Successfully");
        Thread.sleep(700);


        scrollToCenterXpath(driver,"//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[1]/div[1]/div[1]/div[1]/h1[1]");
        Thread.sleep(700);

        clickByXpath(driver,"//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[1]/div[1]/div[1]/div[1]/h1[1]");
        Thread.sleep(700);

        driver.findElement(By.cssSelector("#btn1")).click();
        saveTextLog("Submit Button Clicked");
        Thread.sleep(700);

        String messageSMSk = driver.findElement(By.xpath("//*[@id=\"btn1\"]")).getText();
        Screenshot(driver, "Email and Sms has been sent successfully to the customer: " +messageSMSk);
        Thread.sleep(700);

    }

//-------------Media Based Payment QR Code----------

    @Step("Merchant Portal - Media Based Payments QR CODE")
    public void mediaBasedPaymentQRCode() throws Exception {
        driver.findElement(By.xpath("//span[contains(text(),'Transaction Management')]")).click();
        saveTextLog("Clicked on Transaction Management");
        Thread.sleep(500);

        clickByXpath(driver, "//body/div[1]/div[1]/div[1]/ul[1]/li[2]/ul[1]/li[2]/a[1]");
        String tm_MBPqr = driver.findElement(By.xpath("//*[@id=\"js-side-menu-1\"]/ul/li[2]/a")).getText();
        System.out.println("The page is - " + tm_MBPqr);
        Thread.sleep(900);

        driver.findElement(By.xpath("//body[1]/div[1]/div[1]/div[2]/div[2]/form[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/button[1]")).click();
        saveTextLog("Clicked on Add New Button");
        Thread.sleep(500);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[1]/div[1]/div[1]/input[1]", "Rewa");
        Thread.sleep(500);
        sendKeysByXpath(driver, "//*[@id=\"avantgarde\"]/div[1]/div/div[2]/div[2]/form/div[2]/div[2]/div/div/div[2]/div/input", "DK");//Enter Last Name
        saveTextLog("First and Last Name added");
        Thread.sleep(500);

        clickByXpath(driver, "//*[@id=\"s2id_autogen1\"]/a/span[2]/b");
        Thread.sleep(700);
        driver.findElement(By.xpath("//*[@id=\"select2-drop\"]/ul/li[4]/div")).click();
        saveTextLog("QR Selected");
        JavascriptExecutor jsk = (JavascriptExecutor) driver;
        jsk.executeScript("window.scrollBy(0,350)", "");
        saveTextLog("Scroll down");
        Thread.sleep(500);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[1]/div[4]/div[1]/input[1]", "kirtid@safexpay.com");  //Enter Email ID
        saveTextLog("Entered Email Address");
        Thread.sleep(700);

        clickByXpath(driver, "//*[@id=\"s2id_autogen3\"]/a/span[2]/b");  //Clicks Country Dropdown
        saveTextLog("Clicked Country Dropdown Button");
        Thread.sleep(1000);
        clickByXpath(driver, "//*[@id=\"select2-drop\"]/ul/li[6]/div");  //Selects IND as Country
        saveTextLog("Selected INDIA");
        Thread.sleep(5000);

        clickByXpath(driver, "//*[@id=\"s2id_autogen5\"]/a/span[2]/b");  //Select Currency
        saveTextLog("Clicked Currency Dropdown Button");

        clickByXpath(driver, "//body/div[@id='select2-drop']/ul[1]/li[2]/div[1]");  //Select INR
        saveTextLog("Selected INR as currency");
        JavascriptExecutor jst = (JavascriptExecutor) driver;
        jst.executeScript("window.scrollBy(0,350)", "");
        saveTextLog("Scroll down after currency");
        Thread.sleep(700);


        sendKeysByXpath(driver, "//*[@id=\"frmAmt\"]", "259");  //Enter Amount
        saveTextLog("Amount Entered Successfully");
        Thread.sleep(700);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[1]/div[10]/div[1]/input[1]", "Dress");  //Enter Amount
        saveTextLog("Product name Entered Successfully");
        Thread.sleep(700);

        scrollToCenterXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[1]/div[1]/div[1]/div[1]/h1[1]");
        Thread.sleep(700);

        clickByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[1]/div[1]/div[1]/div[1]/h1[1]");
        Thread.sleep(700);

        driver.findElement(By.cssSelector("#btn1")).click();
        saveTextLog("Submit Button Clicked");
        Thread.sleep(700);

        Assert.assertEquals("kirtid@safexpay.com", driver.findElement(By.cssSelector("body.body_bgclr.ng-scope:nth-child(2) div.container-fluid:nth-child(5) div.row div.main:nth-child(2) div.ng-scope div.ng-scope:nth-child(6) div.card.card-big div.card-content div.table-responsive table.table.table-hover.table-bordered.table-striped.bs-events-table.ng-pristine.ng-untouched.ng-valid tbody:nth-child(2) tr.ng-scope:nth-child(1) > td.word-break.ng-binding:nth-child(4)")).getText());
        System.out.println(driver.findElement(By.cssSelector("body.body_bgclr.ng-scope:nth-child(2) div.container-fluid:nth-child(5) div.row div.main:nth-child(2) div.ng-scope div.ng-scope:nth-child(6) div.card.card-big div.card-content div.table-responsive table.table.table-hover.table-bordered.table-striped.bs-events-table.ng-pristine.ng-untouched.ng-valid tbody:nth-child(2) tr.ng-scope:nth-child(1) > td.word-break.ng-binding:nth-child(4)")).getText());
        saveTextLog("email Matched");
        Thread.sleep(700);

        String messageSMSp = driver.findElement(By.xpath("//*[@id=\"btn1\"]")).getText();
        Screenshot(driver, "Email has been Sent Successfully to the customer: " +messageSMSp);
        Thread.sleep(700);

    }



    //------------- Merchant Instant Payment-----------------------
    @Test(priority = 26, description = "Merchant Portal- Transaction Management(MIP)")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Merchant Portal - Transaction Management(MIP)")
    public void merchantInstaPayment() throws Exception {
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\MerchantPortalUrl.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to Merchant  Account");
        login(credential[1], credential[2]);
        try {
            minstaPayment();
        } catch (Exception e) {
            Assert.fail("Merchant Transaction Management(MIP)  Failed");
        }
    }

    @Step("Merchant Portal - Merchant Instant Payment")
    public void minstaPayment() throws Exception {
        driver.findElement(By.xpath("//span[contains(text(),'Transaction Management')]")).click();
        saveTextLog("Clicked on Transaction Management");
        Thread.sleep(500);

        clickByXpath(driver, "//a[contains(text(),'Merchant Instant Payments')]");
        String tm_MiP = driver.findElement(By.xpath("//a[contains(text(),'Merchant Instant Payments')]")).getText();
        System.out.println("The page is - " + tm_MiP);
        Thread.sleep(900);

        driver.findElement(By.xpath("//*[@id=\"heading-action-wrapper\"]/div/div/div[2]/div/div/button")).click();
        saveTextLog("Clicked on Add New Button");
        Thread.sleep(900);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/div[1]/form[1]/div[4]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/input[1]", "IDFCBK");  //Enter Amount
        saveTextLog("Product name Entered Successfully");
        Thread.sleep(700);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/div[1]/form[1]/div[4]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/input[1]", "56");  //Enter Quantity
        saveTextLog("Quantity Details Entered Successfully");
        Thread.sleep(700);


        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/div[1]/form[1]/div[4]/div[2]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/input[1]", "ACTIVE");  //Enter Button Name
        saveTextLog("Button Details Added Successfully");
        Thread.sleep(700);

        driver.findElement(By.cssSelector("#btn1")).click();
        String Status_Sub = driver.findElement(By.xpath("//button[@id='btn1']")).getText();
        System.out.println("The status is - " + Status_Sub);
        Thread.sleep(900);

        String dataMIP = driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div[2]/div[2]/div[3]/div[2]/div/div[1]/table/tbody")).getText();
        System.out.println("The Data is - " +dataMIP);
        Thread.sleep(900);

    }



//-----------------------------------Merchant Portal - Setting--------------------------

    @Test(priority = 27, description = "Merchant Portal- Settings - Manage User - create User ")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Setting - Manage User")
    public void settingsManageusr() throws Exception {
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\MerchantPortalUrl.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to Merchant  Account");
        login(credential[1], credential[2]);
        try {
            manageUsr();

        } catch (Exception e) {
            Assert.fail("Merchant Manage User Failed");
        }
    }


    @Step("Manage User-Setting")
    public void manageUsr() throws Exception {
        driver.findElement(By.xpath("//*[@id=\"js-side-menu-3\"]/a")).click();
        saveTextLog("Clicked on Setting");
        Thread.sleep(500);

        clickByXpath(driver, "//a[contains(text(),'Manage User')]");
        String tm_BMBP = driver.findElement(By.xpath("//a[contains(text(),'Manage User')]")).getText();
        System.out.println("The page is - " + tm_BMBP);
        Thread.sleep(900);

        driver.findElement(By.xpath("//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/button[1]")).click();
        saveTextLog("Clicked on Add New Button");
        Thread.sleep(900);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[1]/div[1]/div[1]/input[1]", "ABCD112534");  //Enter ID for User
        saveTextLog("User ID Added Successfully");
        Thread.sleep(700);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[2]/div[1]/div[1]/input[1]", "Swaarra");  //Enter First name of User
        saveTextLog("First Name Added Successfully");
        Thread.sleep(700);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[2]/div[2]/div[1]/input[1]", "Dhhotree");  //Enter Last name of User
        saveTextLog("Last Name Added Successfully");
        Thread.sleep(700);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[3]/div[1]/div[1]/input[1]", "dsk.kitu177@gmail.com");  //Enter Email-ID for User
        saveTextLog("Email Address Entered Successfully");
        Thread.sleep(700);

        sendKeysByXpath(driver, "//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[2]/div[2]/div[1]/div[3]/div[2]/div[1]/input[1]", "8698017335");  //Enter Mobile Number
        saveTextLog("Mobile Number Added Successfully");
        Thread.sleep(700);

        driver.findElement(By.xpath("//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[1]/div[1]/div[1]/div[3]/div[1]/div[3]/button[1]")).click();
        saveTextLog("submit button clicked Successfully");
        Thread.sleep(700);

        String table_frstRow = driver.findElement(By.xpath("//*[@id=\"no-more-tables\"]/table/tbody/tr[1]")).getText();
        System.out.println("The Data is - " + table_frstRow);
        Thread.sleep(900);


    }


//-------------------------------Merchnat Portal- Settings- Manage Paymodes ----------------------------------------

    @Test(priority = 28, description = "Merchant Portal -Setting - Manage Paymodes ")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Setting - Manage Paymodes - Downloads File Format")
    public void settingManagePmode() throws Exception {
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\MerchantPortalUrl.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to Merchant  Account");
        login(credential[1], credential[2]);
        try {
            setmanagePaymodes();

        } catch (Exception e) {
            Assert.fail("Merchant Portal Failed");
        }
    }


    @Step("Manage User-Setting")
    public void setmanagePaymodes() throws Exception {
        driver.findElement(By.xpath("//*[@id=\"js-side-menu-3\"]/a")).click();
        saveTextLog("Clicked on Setting");
        Thread.sleep(500);

        clickByXpath(driver, "//a[contains(text(),'Manage Paymodes')]");
        String tm_BMBP = driver.findElement(By.xpath("//a[contains(text(),'Manage Paymodes')]")).getText();
        System.out.println("The page is - " + tm_BMBP);
        Thread.sleep(900);
        driver.findElement(By.xpath("//*[@id=\"formatdrdw\"]")).click();
        Thread.sleep(900);
        driver.findElement(By.xpath("//*[@id=\"heading-action-wrapper\"]/div[1]/div/div[2]/div/div/div/div/ul/li[1]/a")).click();
        saveTextLog("Excel File Downloaded");
        Thread.sleep(1000);
        scrollToCenterXpath(driver, "//*[@id=\"formatdrdw\"]");
        Thread.sleep(700);
        driver.findElement(By.xpath("//*[@id=\"formatdrdw\"]")).click();
        Thread.sleep(900);
        driver.findElement(By.xpath("//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/ul[1]/li[2]/a[1]")).click();
        saveTextLog("CSV File Downloaded");
        Thread.sleep(1000);

    }


//-----------------FlexiQR - Dynamic QR------------------

    @Test(priority = 29, description = "FlexiQR - Dynamic QR")
    @Severity(SeverityLevel.CRITICAL)
    @Description("FlexiQR - Dynamic QR")
    public void flexiQR() throws Exception {
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\MerchantPortalUrl.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to Merchant  Account");
        login(credential[1], credential[2]);
        try {
            flexiDynamicQR();
            flexiStaticQR();

        } catch (Exception e) {
            Assert.fail("Merchant Portal Failed");
        }
    }


    @Step("Manage User-Setting")
    public void flexiDynamicQR() throws Exception {
        driver.findElement(By.xpath("//span[contains(text(),'FlexiQR')]")).click();
        saveTextLog("Clicked on FlexiQR");
        Thread.sleep(500);

        clickByXpath(driver, "//*[@id=\"js-side-menu-7\"]/ul/li[1]/a");
        String flexiiQR = driver.findElement(By.xpath("//*[@id=\"js-side-menu-7\"]/ul/li[1]/a")).getText();
        System.out.println("The page is - " +flexiiQR);
        Thread.sleep(900);

        String flexxiiQR = driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div[2]/div[2]/div[3]/div/div[2]/h4")).getText();
        System.out.println("The Data is - " +flexxiiQR);
        Thread.sleep(900);

        WebElement table = driver.findElement(By.tagName("table"));
        List<WebElement> listOfRows = table.findElements(By.tagName("tr"));
        System.out.println("Rows:"+listOfRows.size());
        //List<WebElement> listOfCols = listOfRows.get(0).findElements(By.tagName("td")); //If first row is normal row

        List<WebElement> listOfCols = listOfRows.get(0).findElements(By.tagName("th")); //If first row is header row

        System.out.println("Columns: "+listOfCols.size());
        Thread.sleep(600);

        String dataflxqr = driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div[2]/div[2]/div[3]/div/div[2]/div[1]/table/tbody/tr[1]")).getText();
        System.out.println("The Data is - " +dataflxqr);
        Thread.sleep(900);


    }


    @Step("Manage User-Setting- Static QR")
    public void flexiStaticQR() throws Exception {
        driver.findElement(By.xpath("//span[contains(text(),'FlexiQR')]")).click();
        saveTextLog("Clicked on FlexiQR");
        Thread.sleep(500);

        clickByXpath(driver, "//body[1]/div[1]/div[1]/div[1]/ul[1]/li[8]/ul[1]/li[3]/a[1]");
        String flexiiQqR = driver.findElement(By.xpath("//body[1]/div[1]/div[1]/div[1]/ul[1]/li[8]/ul[1]/li[3]/a[1]")).getText();
        System.out.println("The page is - " + flexiiQqR);
        Thread.sleep(900);

        WebElement table = driver.findElement(By.tagName("table"));
        List<WebElement> listOfRows = table.findElements(By.tagName("tr"));
        System.out.println("Rows:" + listOfRows.size());
        List<WebElement> listOfCols = listOfRows.get(0).findElements(By.tagName("th")); //If first row is header row

        System.out.println("Columns: " + listOfCols.size());
        Thread.sleep(600);

        String datafllxqr = driver.findElement(By.xpath("//*[@id=\"no-more-tables\"]/table \n")).getText();
        System.out.println("The Data is - " + datafllxqr);
        Thread.sleep(900);


    }




    //-------------------------------Merchant Portal- Subscription Management -----------------------

    @Test(priority = 30, description = "Merchant Portal- Subscription Management")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Merchant Portal- Subscription Management")
    public void subscriptManagementPlan() throws Exception {
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\MerchantPortalUrl.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to Merchant  Account");
        login(credential[1], credential[2]);
        try {
            manageSubscript();
            activeSubPlan();

        } catch (Exception e) {
            Assert.fail("Subscription Management Portal Failed");
        }
    }

    @Step("Merchant Portal- Manage Subscription Plan")
    public void manageSubscript() throws Exception {

        scrollToViewXpath(driver, "//span[contains(text(),'Subscription Management')]");
        Thread.sleep(200);

        driver.findElement(By.xpath("//span[contains(text(),'Subscription Management')]")).click();
        saveTextLog("Clicked on Subscription Management");
        Thread.sleep(500);

        driver.findElement(By.xpath("//*[@id=\"js-side-menu-9\"]/ul/li[1]/a")).click();
        String manageSub = driver.findElement(By.xpath("//*[@id=\"js-side-menu-9\"]/ul/li[1]/a")).getText();
        System.out.println("The page is - " + manageSub);
        Thread.sleep(900);

        driver.findElement(By.xpath("//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[1]/button[1]")).click();
        saveTextLog("Clicked on Add New Button");
        Thread.sleep(900);

        sendKeysByXpath(driver, "//*[@id=\"PlanName\"]", "Average");  //Enter Plan Name
        saveTextLog("Plan Name  Added Successfully");
        Thread.sleep(700);

        sendKeysByXpath(driver, "//*[@id=\"Amount\"]", "20");  //Enter Amount
        saveTextLog("Amount Entred Successfully");
        Thread.sleep(700);

        driver.findElement(By.xpath("//*[@id=\"siFrequency\"]")).click();
        clickByXpath(driver,"//*[@id=\"siFrequency\"]/option[3]");
        String freqncy  = driver.findElement(By.xpath("//*[@id=\"siFrequency\"]/option[3]")).getText();
        System.out.println("The Frequency Selected is - " + freqncy);
        Thread.sleep(900);

        driver.findElement(By.xpath("//*[@id=\"siNoOfTimes\"]")).click();
        clickByXpath(driver,"//*[@id=\"siNoOfTimes\"]/option[4]");
        String minDuration  = driver.findElement(By.xpath("//*[@id=\"siNoOfTimes\"]/option[4]")).getText();
        System.out.println("The Minimum Duration Selected is - " + minDuration);
        Thread.sleep(900);

        driver.findElement(By.xpath("//*[@id=\"daterange\"]")).click();
        clickByXpath(driver, "//*[@id=\"avantgarde\"]/div[3]/div[1]/div[2]/table/tbody/tr[3]/td[2]");
        Thread.sleep(700);
        String dateFrom  = driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[3]/div[1]/div[2]/table/tbody/tr[2]/td[2]")).getText();
        Thread.sleep(900);

        driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[3]/div[2]/div[2]/table/tbody/tr[2]/td[4]")).click();
        String dateTo  = driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[3]/div[2]/div[2]/table/tbody/tr[1]/td[4]")).getText();
        Thread.sleep(700);

        driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[3]/div[3]/div/button[1]")).click();
        saveTextLog("Clicked on Apply");
        Thread.sleep(900);

        clickByXpath(driver,"//*[@id=\"payMode\"]");
        saveTextLog("Clicked on Paymode");
        Thread.sleep(400);

        driver.findElement(By.xpath("//*[@id=\"payMode\"]/option[2]")).click();
        saveTextLog("UPI Selected");
        Thread.sleep(700);

        String subScript = driver.findElement(By.xpath("//*[@id=\"no-more-tables\"]/table/tbody/tr[1]")).getText();
        System.out.println(driver.findElement(By.xpath("//*[@id=\"no-more-tables\"]/table/tbody/tr[1]")).getText());
        saveTextLog("First Row Data Displayed Successfully");


    }


//--------------Active -- SubscriptionPlan ----------

    @Step("Merchant Portal- Manage Subscription Plan - Active Subscription")
    public void activeSubPlan() throws Exception {
        scrollToViewXpath(driver, "//span[contains(text(),'Subscription Management')]");
        Thread.sleep(200);

        driver.findElement(By.xpath("//span[contains(text(),'Subscription Management')]")).click();
        saveTextLog("Clicked on Subscription Management");
        Thread.sleep(500);

        driver.findElement(By.xpath("//*[@id=\"js-side-menu-9\"]/ul/li[2]/a")).click();
        String activeSub = driver.findElement(By.xpath("//*[@id=\"js-side-menu-9\"]/ul/li[2]/a")).getText();
        System.out.println("The page is - " + activeSub);
        Thread.sleep(500);

        driver.findElement(By.xpath("//*[@id=\"activePlanForm\"]/div/div/div/div[1]/div[3]/div/div/label")).click();
        Thread.sleep(500);

        clickByXpath(driver,"//*[@id=\"activePlanForm\"]/div/div/div/div[2]/button");
        saveTextLog("Submit Clicked");
        Thread.sleep(700);

        waitForElementXpath(driver,"//*[@id=\"deleteuser\"]/div/div/div[2]");
        saveTextLog("Are you sure you want to active the plan for : SI or Cards");
        Thread.sleep(700);


        waitForElementXpath(driver,"//body/div[1]/div[1]/div[2]/div[2]/div[4]/div[1]/div[1]/div[3]/button[1]");
        driver.findElement(By.xpath("//*[@id=\"deleteuser\"]/div/div/div[3]/button[1]")).click();
        String messageIP=driver.findElement(By.xpath("//*[@id=\"avantgarde\"]/div[1]/div/div[2]/div[2]/div[2]")).getText();
        Screenshot(driver,"Your Plan will Active Shortly"+messageIP);
        Thread.sleep(700);

    }


    //---------------------------Merchant Portal -ChargeBack-------------------------------

    @Test(priority = 31, description = "Merchant Portal- ChargeBack")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Merchant Portal- ChargeBack")
    public void chargeBack() throws Exception {
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\MerchantPortalUrl.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to Merchant Account");
        login(credential[1], credential[2]);
        try {
            chargeBackTracking();

        } catch (Exception e) {
            Assert.fail("ChargeBAck Failed");
        }
    }

    @Step("Merchant Portal- ChargeBack")
    public void chargeBackTracking() throws Exception {

        waitAndClickByXpath(driver,"//span[contains(text(),'Chargeback')]");
        saveTextLog("Clicked on ChargeBack");
        Thread.sleep(200);

        driver.findElement(By.xpath("//a[contains(text(),'Chargeback Tracking')]")).click();
        saveTextLog("Clicked on ChargeBAck Tracking");
        Thread.sleep(250);

        clickByXpath(driver,"");
        String chrgpBack = driver.findElement(By.xpath("//*[@id=\"heading-action-wrapper\"]/div/div/div[1]")).getText();
        System.out.println("The Page is -" +chrgpBack);
        Thread.sleep(350);

        clickByXpath(driver,"//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[3]/div[2]/div[1]/div[1]");
        String totlopenCount = driver.findElement(By.xpath("//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[3]/div[2]/div[1]/div[1]")).getText();
        System.out.println(" - " +totlopenCount);

        clickByXpath(driver,"//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[3]/div[2]/div[2]/div[1]");
        String totAmount = driver.findElement(By.xpath("//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[3]/div[2]/div[2]/div[1]")).getText();
        System.out.println(" - " +totAmount);

        clickByXpath(driver,"//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[3]/div[2]/div[3]/div[1]");
        String totlcloseCount = driver.findElement(By.xpath("//body/div[1]/div[1]/div[2]/div[2]/form[1]/div[3]/div[2]/div[3]/div[1]")).getText();
        System.out.println(" - " +totlcloseCount);


        String CBlist = driver.findElement(By.xpath("//h4[contains(text(),'ChargeBack List')]")).getText();
        System.out.println("The Header is Now - " +CBlist);
        Thread.sleep(200);

        String tb_Row_dataFirst = driver.findElement(By.xpath("//*[@id=\"no-more-tables\"]/table/tbody/tr[1]")).getText();
        System.out.println("The Row is - " +tb_Row_dataFirst);
        Thread.sleep(500);


    }


//---------------------------Merchant Portal - ---------------------

    @Test(priority = 32, description = "Merchant Portal- ")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Merchant Portal- ChargeBack")
    public void newFunction() throws Exception {
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\MerchantPortalUrl.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0], "Logging in to Merchant Account");
        login(credential[1], credential[2]);
        try {
            chargeBackTracking();

        } catch (Exception e) {
            Assert.fail("ChargeBAck Failed");
        }
    }

    @Step("Merchant Portal- ChargeBack")
    public void different() throws Exception {


    }




//----------------------PARAM UPDATES----------------

    @Test(priority=33, description = "ParamUpdates")
    @Severity(SeverityLevel.CRITICAL)
    @Description("ParamUpdates")
    public void ParamUpdates() throws Exception {
        Thread.sleep(2000);
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\Admin_Credentials.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0],"Logging in to Maker Account");
        login(credential[1],credential[3]);

        paramUPdatesAdmin();
    }
    @Step("Refund MIS")
    public void paramUPdatesAdmin() throws Exception {
        boolean testFail = false;
        waitForPageToLoad(driver);

        clickByXpath(driver,"//*[@id=\"js-side-menu-2\"]/a");
        saveTextLog("clicked on Merchant Management");
        Thread.sleep(500);
        waitAndClickByXpath(driver, "//a[contains(text(),'Param Update')]");
        String paramup = driver.findElement(By.xpath("//a[contains(text(),'Param Update')]")).getText();
        System.out.println(" - " +paramup);
        Thread.sleep(700);

        //clickByXpath(driver,"//body/div[1]/div[1]/div[1]/div[1]/form[1]/div[4]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/button[1]");
        //Thread.sleep(800);

        WebElement upload_file = driver.findElement(By.xpath("//body/div[1]/div[1]/div[1]/div[1]/form[1]/div[4]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/button[1]"));
        Thread.sleep(300);
        upload_file.sendKeys("C:/Users/Kirtid/Downloads/UpdateParamYesPgIsg.xls");
        Thread.sleep(400);
        saveTextLog("File uploaded successfully");
        Thread.sleep(400);


    }
    //--------------Risk or velocity-------------------
    @Test(priority=34, description = "Risk")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Risk")
    public void Risk() throws Exception {
        Thread.sleep(2000);
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\Admin_Credentials.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0],"Logging in to Maker Account");
        login(credential[1],credential[3]);

        riskRules();
    }
    @Step("Risk Rules")
    public void riskRules() throws Exception {
        boolean testFail = false;
        waitForPageToLoad(driver);
        clickByXpath(driver,"//*[@id=\"js-side-menu-2\"]/a");
        saveTextLog("clicked on Merchant Management");
        Thread.sleep(500);
        waitAndClickByXpath(driver,"//*[@id=\"js-side-menu-2\"]/ul/li[7]/a");
        String risk = driver.findElement(By.xpath("//*[@id=\"js-side-menu-2\"]/ul/li[7]/a")).getText();
        System.out.println(" - " +risk);
        Thread.sleep(400);


        sendKeysByXpath(driver,"//*[@id=\"merchantNameLike\"]","Kirti25422");
        Thread.sleep(500);
        //clickByXpath(driver,"//*[@id=\"loader\"]");
        saveTextLog("Name added : Kirti25422");
        Thread.sleep(700);



        //waitForElementXpath(driver,"//*[@id=\"minTxnAmount\"]");
        //Thread.sleep(500);

        //clickByXpath(driver,"//*[@id=\"minTxnAmount\"]");
        //sendKeysByXpath(driver,"//*[@id=\"minTxnAmount\"]","05");
        //saveTextLog("Minimum transaction amount added");
        //Thread.sleep(200);








        /*sendKeysByXpath(driver,"//input[@id='merchantNameLike']","Kirti25422");
        //Merchant name
        clickByXpath(driver,"//img[@id='loader']");
        saveTextLog("Name added : Kirti25422");
        Thread.sleep(100);

        */




        //waitAndClickElementByText(driver, "SUBMIT");
        //Thread.sleep(500);















    }
//-------------------------------Integration Details--------------------------

    @Test(priority=35, description = "Refund MIS")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Refund")
    public void integrationnDetails() throws Exception {
        Thread.sleep(2000);
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\Admin_Credentials.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0],"Logging in to Maker Account");
        login(credential[1],credential[3]);

        integrationDetails();
    }
    @Step("Refund MIS")
    public void integrationDetails() throws Exception {
        boolean testFail = false;
        waitForPageToLoad(driver);

        sendKeysByXpath(driver,"//*[@id=\"merchantNameLike\"]","Kirti25422");
        Thread.sleep(500);
        clickByXpath(driver,"//button[contains(text(),'Submit')]");
        saveTextLog("Name added : Kirti25422");
        Thread.sleep(700);


    }

//----------------Merchant Pricing-----------------------

    @Test(priority=36, description = "Merchant Pricing")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Merchant Pricing")
    public void merchantPricing() throws Exception {
        Thread.sleep(2000);
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\Admin_Credentials.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0],"Logging in to Maker Account");
        login(credential[1],credential[3]);

        mPricing();
    }
    @Step("Refund MIS")
    public void mPricing() throws Exception {
        boolean testFail = false;
        waitForPageToLoad(driver);
        clickByXpath(driver,"//*[@id=\"js-side-menu-2\"]/a");
        saveTextLog("clicked on Merchant Management");
        Thread.sleep(500);
        waitAndClickByXpath(driver,"//a[contains(text(),'Merchant Pricing')]");
        String merchantPricing = driver.findElement(By.xpath("//a[contains(text(),'Merchant Pricing')]")).getText();
        System.out.println(" - " +merchantPricing);
        Thread.sleep(400);


        sendKeysByXpath(driver,"//*[@id=\"merchantNameLike\"]","Kirti25422");
        Thread.sleep(500);
        clickByXpath(driver,"//button[contains(text(),'Submit')]");
        saveTextLog("Name added : Kirti25422");
        Thread.sleep(700);

        waitAndClickByXpath(driver,"//*[@id=\"Pricing\"]/form/div[2]/div[1]/div/nav/ul/li[1]/a");
        saveTextLog("Clicked on Amazon Pay");
        Thread.sleep(300);

        waitAndClickByXpath(driver,"//*[@id=\"s2id_autogen134\"]");
        clickByXpath(driver, "//*[@id=\"select2-drop\"]/ul");
        saveTextLog("Selected INR");
        Thread.sleep(400);



    }









    //----------------------Refund-----------------------
    @Test(priority=37, description = "Refund MIS")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Refund")
    public void refundMIS() throws Exception {
        Thread.sleep(2000);
        String path = System.getProperty("user.dir") + "\\Configuration_Files\\Admin_Credentials.csv";  //path to get login details file or credentials file
        ReadFromCSV csv = new ReadFromCSV(path);  //Reading credentials file
        String[] credential = csv.ReadLineNumber(1); //Reads first line containing login id and password
        System.out.println(Arrays.toString(credential));
        openUrl(credential[0],"Logging in to Maker Account");
        login(credential[1],credential[3]);

        refundMISDownload();
    }
    @Step("Refund MIS")
    public void refundMISDownload() throws Exception {
        boolean testFail=false;
        waitForPageToLoad(driver);
        Thread.sleep(1000);
        clickByXpath(driver,"//*[@id=\"js-side-menu-3\"]");
        clickByXpath(driver,"//*[@id=\"js-side-menu-3\"]/ul/li/a"); // Refund MIS navigation
        List<WebElement> merchant;
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"mid\"]")));
        Thread.sleep(1000);
        clickByXpath(driver,"//*[@id=\"mid\"]");
        Thread.sleep(1000);
        merchant=wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//*[@id=\"mid\"]/option"))); // Merchant MID Processor
        Thread.sleep(5000);
        for(WebElement w:merchant)
        {
            if(w.getText().equalsIgnoreCase("fab_safexpay1"))
            {
                w.click();
                break;
            }
        }
        //------------------------Check for file downloaded or not--------------------------------
        String downloadPath = System.getProperty("user.dir") + "\\downloadFiles";
        File directory=new File(downloadPath);
        int initial_size=directory.list().length;
        Thread.sleep(4000);
        waitAndClickByXpath(driver,"//*[@id=\"downloadID2\"]/a");
        Thread.sleep(5000);
        Boolean flag=false;
        if(initial_size==directory.list().length)
        {
            saveTextLog("Refund MIS not downloaded"); // fail
            testFail=true;
        }
        else{
            saveTextLog("Refund MIS Downloaded");
            Screenshot(driver,"");
            flag=true;
        }
        if(flag) {
            File download = new File("downloadFiles");
            File[] files = download.listFiles();
            String path = null;
            String filename = null;
            for (File f : files) {
                if (f.getName().contains("Refund")) {
                    path = f.getPath();
                    // filename=f.getName();
                }
            }
            ZipFile zip = new ZipFile(path);
            zip.extractAll("downloadFiles/");
            // String [] RefundCSVname=filename.split(".");
            // ReadFromCSV csvreader= new ReadFromCSV("downloadFiles/"+RefundCSVname[0]+".csv");
        }
        if(testFail){
            throw new Exception("Refund MIS not downloaded");
        }
    }

    @AfterTest
    public void afterTest(){
        driver.close();
    }

}


