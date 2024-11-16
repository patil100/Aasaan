package Functions;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

import static Functions.ClickElement.waitAndClickById;
import static Functions.ClickElement.waitAndClickByXpath;

/*

void uploadByXpath(WebDriver driver, String xpath, String path)  //Sends file path into a writable text box located by Xpath

void uploadById(WebDriver driver, String id, String path)  //Sends path into a writable test box located by id

uploadByXpathRobo(WebDriver driver, String xpath, String Path)  //Uses Robo class to generate native system input events.
                                                                //Writes path onto the window created and presses enter to submit

void uploadByIdRobo(WebDriver driver, String id, String Path)  //Uses Robo class to generate native system input events.
                                                               //Writes path onto the window created and presses enter to submit

 */

public class UploadFile {
    public static void uploadByXpath(WebDriver driver, String xpath, String path) {
        WebDriverWait wait = new WebDriverWait(driver, 100);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        element.sendKeys(path);
        element.sendKeys(Keys.RETURN);
    }

    public static void uploadById(WebDriver driver, String id, String path) {
        WebDriverWait wait = new WebDriverWait(driver, 100);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
        element.sendKeys(path);
        element.sendKeys(Keys.RETURN);
    }

    public static void uploadByXpathRobo(WebDriver driver, String xpath, String Path) throws InterruptedException, AWTException {
        StringSelection path = new StringSelection(Path);
        waitAndClickByXpath(driver,xpath);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(path,null);
        Robot robot = new Robot();
        Thread.sleep(1000);
        // Press Enter
        robot.keyPress(KeyEvent.VK_ENTER);
// Release Enter
        robot.keyRelease(KeyEvent.VK_ENTER);
        // Press CTRL+V
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
// Release CTRL+V
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_V);
        Thread.sleep(1000);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }
    public static void uploadByIdRobo(WebDriver driver, String id, String Path) throws InterruptedException, AWTException {
        StringSelection path = new StringSelection(Path);
        waitAndClickById(driver,id);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(path,null);
        Robot robot = new Robot();
        Thread.sleep(1000);
        // Press Enter
        robot.keyPress(KeyEvent.VK_ENTER);
// Release Enter
        robot.keyRelease(KeyEvent.VK_ENTER);
        // Press CTRL+V
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
// Release CTRL+V
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyRelease(KeyEvent.VK_V);
        Thread.sleep(1000);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }
}
