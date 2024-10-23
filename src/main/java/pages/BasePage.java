package pages;
// src/main/java/com/example/pages/BasePage.java


import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ElementClickInterceptedException;
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
            // Wait until the element is clickable
            WebElement element = this.wait.until(ExpectedConditions.elementToBeClickable(by));
            
            // Scroll the element into view
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            
            // Try clicking the element
            element.click();
            break; // Exit loop if click is successful

        } catch (StaleElementReferenceException e) {
            System.out.println("Element went stale, retrying...");

        } catch (ElementClickInterceptedException e) {
            System.out.println("Element click intercepted, retrying...");
            // Optionally add some waiting to allow for overlays/pop-ups to disappear
            try {
                Thread.sleep(500); // Small delay before retry
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

        } catch (Exception e) {
            System.out.println("Error during click: " + e.getMessage());
            // As a last resort, use JavaScript to click the element
            WebElement element = this.driver.findElement(by);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            break; // Exit loop after using JS click
        }
        attempts++;
    }
}


   
}
