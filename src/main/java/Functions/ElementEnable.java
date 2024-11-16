package Functions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/*
boolean elementIsEnable(WebDriver driver, String xpath)  //Checks if an element is clickable
 */

public class ElementEnable {
    public static boolean elementIsEnable(WebDriver driver, String xpath){
      try {
          WebDriverWait wait = new WebDriverWait(driver, 5);
          WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
          Actions action = new Actions(driver);
          action.moveToElement(element).build().perform();
          return element.isEnabled();
      }catch (Exception e){
          return false;
      }
    }
}
