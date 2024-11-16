package Functions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import static Reports.AllureReport.saveTextLog;

public class SelectRandomFile {
    public static int selectRandomFileFromList(WebDriver driver, String xpath){
        WebElement selectedFile=null;
        List<WebElement> filesList = driver.findElements(By.xpath(xpath));
        if (filesList.size() == 1) {
            try {
                if (filesList.get(0).getText().toLowerCase().contains("select")) {
                    saveTextLog("No data available in table");
                    Thread.sleep(500);
                    return -1;
                }
            } catch (Exception e) {
            }
            return 0;

        } else if (filesList.size() > 1) {
            int min = 2, max = filesList.size();
            int randomNum = new Random().nextInt((max - min) + 1) + min;
            return randomNum-1;
        }
        return -1;
    }

    public static int createRandomNum(int min, int max){
        int randomNum = new Random().nextInt((max - min) + 1) + min;
        return randomNum;
    }
}
