package pages;
// src/main/java/com/example/pages/BasePage.java


import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        // Adjust the wait time as necessary
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(100));
    }

    public  void retryingFindClick(By by) {
        int attempts = 0;
        while(attempts < 5) {
            try {
                this.wait.until(ExpectedConditions.elementToBeClickable(by)).click();
                break;
            } catch(StaleElementReferenceException e) {
                System.out.println("Element went stale, retrying...");
            }
            attempts++;
        }
    }
}
