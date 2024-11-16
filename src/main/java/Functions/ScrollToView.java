package Functions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


/*
void scrollToViewElement(WebDriver driver, String xpath)  //Waits for the element to load and scrolls to view it

void scrollToViewElementId(WebDriver driver, String id)  //Waits for the element to load and scrolls to view it

void scrollToViewWebElement(WebDriver driver, WebElement element)  //Waits for the element to load and scrolls to view it
 */
public class ScrollToView {
    public static void scrollToViewElementXpath(WebDriver driver, String xpath) {

        try {
            WebDriverWait wait = new WebDriverWait(driver, 100);
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            Actions action = new Actions(driver);
            action.moveToElement(element).build().perform();

        } catch (Exception e) {
            System.out.println("Unable to scroll to the element\n\n"+e.getMessage());
        }

    }
    public static void scrollToViewElementId(WebDriver driver, String id) {

        try {
            WebDriverWait wait = new WebDriverWait(driver, 100);
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
            Actions action = new Actions(driver);
            action.moveToElement(element).build().perform();

        } catch (Exception e) {
            System.out.println("Unable to scroll to the element\n\n"+e.getMessage());
        }

    }

    public static void scrollToViewWebElement(WebDriver driver, WebElement element) {

        try {
            Actions action = new Actions(driver);
            action.moveToElement(element).build().perform();

        } catch (Exception e) {
            System.out.println("Unable to scroll to the element\n\n"+e.getMessage());
        }

    }
    public static void scrollToViewXpath(WebDriver driver, String xpath) {

        try {
            Actions action = new Actions(driver);
            WebElement element= driver.findElement(By.xpath(xpath));
            action.moveToElement(element).build().perform();

        } catch (Exception e) {
            System.out.println("Unable to scroll to the element\n\n"+e.getMessage());
        }

    }
    public static void scrollToViewXpathByJavascript(WebDriver driver, String xpath) {

        try {
            JavascriptExecutor je = (JavascriptExecutor) driver;
            WebElement element= driver.findElement(By.xpath(xpath));
            je.executeScript("arguments[0].scrollIntoView(true);",element);

        } catch (Exception e) {
            System.out.println("Unable to scroll to the element\n\n"+e.getMessage());
        }

    }
    public static void scrollToCenterXpath(WebDriver driver, String xpath) {

        try {
            WebElement element = driver.findElement(By.xpath(xpath));

            String scrollElementIntoMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
                    + "var elementTop = arguments[0].getBoundingClientRect().top;"
                    + "window.scrollBy(0, elementTop-(viewPortHeight/2));";

            ((JavascriptExecutor) driver).executeScript(scrollElementIntoMiddle, element);

        } catch (Exception e) {
            System.out.println("Unable to scroll to the element\n\n"+e.getMessage());
        }

    }

    public static void scrollToCenterElement(WebDriver driver, WebElement element) {

        try {
            String scrollElementIntoMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
                    + "var elementTop = arguments[0].getBoundingClientRect().top;"
                    + "window.scrollBy(0, elementTop-(viewPortHeight/2));";

            ((JavascriptExecutor) driver).executeScript(scrollElementIntoMiddle, element);

        } catch (Exception e) {
            System.out.println("Unable to scroll to the element\n\n"+e.getMessage());
        }

    }

}
