package Reports;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.Attachment;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

/*
void DelPreviousReport()  //Deletes report present in allure-results folder to create space for new results

byte[] Screenshot(WebDriver driver, String message)  //Screenshots the webpage and stores with a message

String saveTextLog(String message)  //Logs the message to console
 */

public class AllureReport {
    public static void DelPreviousReport() throws IOException {
        try {
            String filePath = System.getProperty("user.dir") + "\\allure-results";
            //Creating the File object
            File file = new File(filePath);
//            if(file.delete()){
//                System.out.println(filePath+"deleted");
//            }else System.out.println(filePath+ " not deleted");
            FileUtils.deleteDirectory(file);
            System.out.println(filePath);
            System.out.println("Files deleted........");
        } catch (Exception e) {
            saveTextLog("ERR: " +e.getMessage());
            throw e;
        }
    }
    public static void changeStepName(String name)
    {
        AllureLifecycle lifecycle = Allure.getLifecycle();
        lifecycle.updateStep(testStep -> testStep.setName(name));
    }

    @Attachment(value = "Page Screenshot",type = "image/png")
    public static byte[] Screenshot(WebDriver driver, String message) {
        saveTextLog(message);
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    @Attachment(value = "{0}", type = "text/plain")
    public static String saveTextLog(String message) {
        System.out.println(message);
        return message;
    }
}
