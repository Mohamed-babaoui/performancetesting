package pages;
// src/main/java/com/example/pages/BasePage.java


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        // Adjust the wait time as necessary
        System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
        this.wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(100));
        System.out.println("fffffffffffffffffffffffffffffffff");
    }

 /*   public  void retryingFindClick(By by) {
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
    }*/
    public void retryingFindClick(By by) {
    int attempts = 0;
    while (attempts < 5) {
        try {
            WebElement element = this.wait.until(ExpectedConditions.elementToBeClickable(by));
            element.click();
            break;
        } catch (Exception e) {
            System.out.println("Error during click: " + e.getMessage());
        }
        attempts++;
    }
    if (attempts == 5) {
        try {
            WebElement element = this.driver.findElement(by);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


   
}
