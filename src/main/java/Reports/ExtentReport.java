package Reports;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.util.Random;


/*
void StartExtentReport(WebDriver driver)  //Sets up file to write extent report

ExtentTest CreateNewTest(WebDriver driver, String message)  //Creates new test

void saveTextLogPass(WebDriver driver, ExtentTest test, String message)  //Sets test as passed

void saveTextLogFail(WebDriver driver, ExtentTest test, String message)  //Sets test as failed

void saveTextLogError(WebDriver driver, ExtentTest test, String message)  //Saves test error message

void saveTextLogInfo(WebDriver driver, ExtentTest test, String message)  //Adds info and screenshot to test

String screenshot(WebDriver driver)  //takes screenshot

void EndExtentReport()  //Ends report
 */


public class ExtentReport {
    static Random rand = new Random();
    static ExtentReports extent;
    static ExtentHtmlReporter htmlReporter;

    public static void StartExtentReport(WebDriver driver) {
        String folder = System.getProperty("user.dir") + "\\Extent_Reports";
        File file = new File(folder);
//        String path = System.getProperty("user.dir") + "\\Extent_Reports\\index.html";

        htmlReporter = new ExtentHtmlReporter(".\\Extent_Reports\\index.html");
//        // Create an object of Extent Reports
        extent = new ExtentReports();
//        extent = new ExtentReports(path,true);
        extent.attachReporter(htmlReporter);
        htmlReporter.config().setAutoCreateRelativePathMedia(true);

        extent.setSystemInfo("Host Name", "Selenium");
        extent.setSystemInfo("Environment", "Window");
        extent.setSystemInfo("User Name", "AdminNew");

        // Name of the report
        htmlReporter.config().setReportName("Automation Testing");
        // Dark Theme
        htmlReporter.config().setTheme(Theme.STANDARD);
    }

    public static ExtentTest CreateNewTest(WebDriver driver, String message) {
        ExtentTest test = extent.createTest(message);
        return test;
    }

    public static void saveTextLogPass(WebDriver driver, ExtentTest test, String message) {
        System.out.println(message);
        test.pass(message);
//        test.log(Status.PASS, message);
    }



    public static void saveTextLogFail(WebDriver driver, ExtentTest test, String message) {
        System.out.println(message);
        test.fail(message);
//        test.log(Status.FAIL, message);
    }

    public static void saveTextLogError(WebDriver driver, ExtentTest test, String message) throws Exception {
        System.out.println(message);
        test.error(message,MediaEntityBuilder.createScreenCaptureFromPath(screenshot(driver)).build());

    }

    public static void saveTextLogInfo(WebDriver driver, ExtentTest test, String message) throws Exception {
        System.out.println(message);

        test.info(message,MediaEntityBuilder.createScreenCaptureFromPath(screenshot(driver)).build());
//       test.info("Snapshot below: " + test.addScreenCapture(screenshot(driver, message)));

    }

    public static String screenshot(WebDriver driver) {

//        String dateName = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
//        File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//        //after execution, you could see a folder "Screenshots" under Extent_Reports folder
//        String destination = System.getProperty("user.dir") + "\\Extent_Reports\\Screenshots\\" + rand.nextInt(100000000) + ".png";
//        File finalDestination = new File(destination);
//        FileUtils.copyFile(source, finalDestination);
//        //Returns the captured file path
//        return destination;

        File src=((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String path =System.getProperty("user.dir")+"\\Extent_Reports\\Screenshots\\"+rand.nextInt(1000000)+".png";
        File destination=new File(path);
        try
        {
            FileUtils.copyFile(src, destination);
        } catch (Exception e)
        {
            System.out.println("Capture Failed "+e.getMessage());
        }
        return destination.getAbsolutePath();

    }

    public static void EndExtentReport() throws Exception {
        extent.flush();
    }
}
