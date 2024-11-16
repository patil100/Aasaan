package Functions;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/*
WebDriver driverAllocation(String browserName)  //Creates a browser by matching string name(firefox, chrome, internet explorer)

WebDriver headlessDriverAllocation(String browserName)  //Creates a headless browser .
                                                        //The program will behave just like a browser but will not show any GUI.

 */

public class Driver {
    public static WebDriver driverAllocation(String browserName) {

        if (browserName.trim().toLowerCase().contains("firefox")) {

            // Defining System Property for the firefox driver
            System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "\\Webdriver\\geckodriver.exe");
            WebDriver driver = new FirefoxDriver();
            driver.manage().window().maximize();
            System.out.println("Executing firefox Driver in UI mode..\n");
            return driver;

        } else if(browserName.trim().toLowerCase().contains("ie")){

                // Defining System Property for the IEDriver
                System.setProperty("webdriver.ie.driver", System.getProperty("user.dir") + "\\Webdriver\\IEDriverServer.exe");
                // Instantiate a IEDriver class.
                WebDriver driver = new InternetExplorerDriver();
                driver.manage().window().maximize();
                System.out.println("Executing IE Driver in UI mode..\n");
                return driver;
            }
        else {
        

        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\Webdriver\\chromedriver.exe");
        // Defining System Property for the chrome driver


//        Map<String, Object> prefs = new HashMap<String, Object>();
//
//        // Use File.separator as it will work on any OS
//        prefs.put("download.default_directory",new File(System.getProperty("user.dir") + "\\downloadFiles").getAbsolutePath());
//
//        // Adding capabilities to ChromeOptions
//        WebDriverManager.chromedriver().setup();
//        ChromeOptions options = new ChromeOptions();
//        options.setExperimentalOption("prefs", prefs);

        // Launching browser with desired capabilities
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        System.out.println("Executing Chrome Driver in UI mode..\n");
        return driver;

    }}

    public static WebDriver headlessDriverAllocation(String browserName) {

        if (browserName.trim().toLowerCase().contains("firefox")) {

            System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "\\Webdriver\\geckodriver.exe");
            FirefoxOptions options = new FirefoxOptions();
            options.setHeadless(true);

            WebDriver driver = new FirefoxDriver(options);
            driver.manage().window().maximize();
            System.out.println("Executing firefox Driver in Headless mode..\n");
            return driver;
        } else {

            if (browserName.trim().toLowerCase().contains("html")) {

                WebDriver driver = new HtmlUnitDriver();
                driver.manage().window().maximize();
                System.out.println("Executing HtmlUnitDriver Driver in Headless mode..\n");
                return driver;
            }
        }


        System.setProperty("webdriver.chrome.driver", ".//Webdriver//chromedriver.exe");
        //initialize the driver
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); //options.setHeadless(true);
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        System.out.println("Executing Chrome Driver in Headless mode..\n");
        return driver;

    }
}
