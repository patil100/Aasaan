package Functions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Iterator;
import java.util.Set;

public class PopupWindow {
    private String mainWindow;
    private Set<String> allWindows;
    private Iterator<String> iterator;

    public void switchToPopupWindow(WebDriver driver) {
        mainWindow=driver.getWindowHandle();
        allWindows=driver.getWindowHandles();
        iterator=allWindows.iterator();
        while (iterator.hasNext()) {
            String ChildWindow = iterator.next();

            if (!mainWindow.equalsIgnoreCase(ChildWindow)) {
                driver.switchTo().window(ChildWindow);
            }
        }

    }
    public void closePopupWindow(WebDriver driver){
        if(allWindows.size()==0)
            return;
        if(!driver.getWindowHandle().equalsIgnoreCase(mainWindow)){
            driver.close();
        }
        driver.switchTo().window(mainWindow);
        allWindows.clear();
    }
    public void switchToMainWindow(WebDriver driver){
        if(mainWindow!=null){
            driver.switchTo().window(mainWindow);
        }
    }
}
