package Functions;
import com.google.common.base.Function;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/*
 void clickWithJavaScriptByWebElement(WebDriver driver, WebElement element)    //Zooms out the page and scrolls to view the element, then clicks on it

 void clickWithJavaScriptByXpath(WebDriver driver, String xpath)   //Takes the xpath and clicks on it(Zooms out the page and scrolls to view the element)

 void waitAndClickByXpath(WebDriver driver, String xpath)  //Waits for element to load and then clicks on it (Takes Xpath)

 void waitAndClickById(WebDriver driver, String id)  //Waits for element to load and then clicks on it (Takes id)

 void sendKeysByXpath(WebDriver driver, String xpath,String sendKey)  //Waits for element to load and Sends or writes a string after locating the Xpath

 void sendKeysById(WebDriver driver, String id, String sendKey)  //Waits for element to load and Sends or writes a string after locating the id

 void waitForElementXpath(WebDriver driver, String xpath)  //Delays the test for the element to load

 boolean isPresentElement(WebDriver driver, String class)  //checks if an element is present by class

 void clickByXpath(WebDriver driver, String xpath)  //clicks on the mentioned xpath

*/

public class ClickElement {

    public static void clickWithJavaScriptByWebElement(WebDriver driver, WebElement element) {

        try {

            driver.switchTo().defaultContent();
            WebElement html = driver.findElement(By.tagName("html"));
            html.sendKeys(Keys.chord(Keys.CONTROL, Keys.SUBTRACT));
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].scrollIntoView()", element);
            executor.executeScript("arguments[0].click();", element);

        } catch (Exception e) {
            System.out.println("Unable to click element\n\n"+e.getMessage());
        }
        try {

            Actions actions = new Actions(driver);
            actions.moveToElement(element).click().build().perform();

        } catch (Exception e) {}
    }

    public static void clickWithJavaScriptByXpath(WebDriver driver, String xpath) {
        WebDriverWait wait = new WebDriverWait(driver, 100);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));

        try {

            driver.switchTo().defaultContent();
            WebElement html = driver.findElement(By.tagName("html"));
            html.sendKeys(Keys.chord(Keys.CONTROL, Keys.SUBTRACT));
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].scrollIntoView()", element);
            executor.executeScript("arguments[0].click();", element);

        } catch (Exception e) {
            System.out.println("Unable to click element\n\n"+e.getMessage());
        }
        try {

            Actions actions = new Actions(driver);
            actions.moveToElement(element).click().build().perform();

        } catch (Exception e) {}
    }

    public static void waitAndClickByXpath(WebDriver driver, String xpath) {
        WebDriverWait wait = new WebDriverWait(driver, 100);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        element.click();
    }

    public static void waitAndClickById(WebDriver driver, String id) {
        WebDriverWait wait = new WebDriverWait(driver, 100);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
        element.click();
    }
    public static void sendKeysByXpath(WebDriver driver, String xpath,String sendKey) {
        WebDriverWait wait = new WebDriverWait(driver, 100);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        element.sendKeys(sendKey);
    }
    public static void waitForElementByText(WebDriver driver, String text)
    {
        WebDriverWait wait= new WebDriverWait(driver, 100);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(text)));
    }
    public static void waitAndClickElementByText(WebDriver driver, String text)
    {
        WebDriverWait wait= new WebDriverWait(driver, 100);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(text))).click();

    }

    public static void sendKeysById(WebDriver driver, String id, String sendKey) {
        WebDriverWait wait = new WebDriverWait(driver, 100);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
        element.sendKeys(sendKey);
    }

    public static void waitForElementXpath(WebDriver driver, String xpath) {
        WebDriverWait wait = new WebDriverWait(driver, 100);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
    }

    public static void waitForElementXpathByTime(WebDriver driver, String xpath, long time) {
        WebDriverWait wait = new WebDriverWait(driver, time);
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
    }
    public static boolean waitForElementXpathToLoad(WebDriver driver, String xpath) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        try{WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        return true;}
        catch (Exception e){
            return false;
        }
    }

    public static void hoverByXpath(WebDriver driver, String xpath) {
        Actions actions = new Actions(driver);
        WebElement target=driver.findElement(By.xpath(xpath));
        actions.moveToElement(target).perform();
    }

    public static void hoverByElement(WebDriver driver, WebElement target) {
        Actions actions = new Actions(driver);
        actions.moveToElement(target).perform();
    }

    public static boolean isPresentElementByClass(WebDriver driver, String classname) {
        try {
            return driver.findElement(By.className(classname)).isDisplayed();
        }catch (Exception e){
            return false;
        }
    }

    public static void waitForPageToLoad(WebDriver driver){
        WebDriverWait wait=new WebDriverWait(driver, 60);
        wait.until((Function<? super WebDriver, Boolean>) webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    public static void waitForElementToBeStale(WebDriver driver, String xpath) {
        new WebDriverWait(driver, 30).until(ExpectedConditions.stalenessOf(driver.findElement(By.xpath(xpath))));
    }

    public static void clickByXpath(WebDriver driver, String xpath){
        driver.findElement(By.xpath(xpath)).click();
    }
    public static void clickByText(WebDriver driver, String text){
        driver.findElement(By.linkText(text)).click();
    }
    public static void clickById(WebDriver driver, String id){
        driver.findElement(By.id(id)).click();
    }


    public static WebElement waitForTwoElementsByXpath(WebDriver driver, String element1, String element2, int time) {
        try {
            for (int i = 0; i < time * 2; i++) {
                try {
                    if (driver.findElement(By.xpath(element1)).isDisplayed()) {
                        return driver.findElement(By.xpath(element1));
                    }
                }catch (Exception e){}
                try {
                    if (driver.findElement(By.xpath(element2)).isDisplayed()) {
                        return driver.findElement(By.xpath(element2));
                    }
                }catch (Exception e){}
                Thread.sleep(500);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }
}
