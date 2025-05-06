package pages;
// src/main/java/com/example/pages/AvailabilityPage.java

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class AvailabilityPage extends BasePage {

    // Corrected Locators
    private By startDateField = By.xpath("//*[@id=\"calendarStartDate_input\"]");
    private By endDateField = By.xpath("//*[@id=\"calendarEndDate_input\"]");
    private By panelSearchCriteriaHeader = By.id("panelSearchCriteria_header");
    private By siteDropdown = By.id("dropDownSite_input");
  //  private By siteOption = By.xpath("//span[contains(text(),'La Corniche de la Plage')]");
    private By siteOption = By.xpath("//div[@id='dropDownSite_panel']/ul[1]/li[1]");
    private By searchButton = By.id("tabItemClientButtonSearch");
    private By selectDestinationButton = By.xpath("//*[@id='panelResultOfferTabControl:tableDestinations:0:destinationToBook']");
    private By panelReservation_header = By.xpath("//div[@id='panelReservation_header']");
    public AvailabilityPage(WebDriver driver) {
        super(driver);
    }

    // For Scenario1: Without end date
    public Boolean enterStartDate(String startDate) {
        try {
            Thread.sleep(2000);
            wait.until(ExpectedConditions.elementToBeClickable(startDateField)).clear();
            driver.findElement(startDateField).sendKeys(startDate);
            driver.findElement(By.tagName("body")).click();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // For Scenario2: With end date
    /*  public void enterStartAndEndDate(String startDate, String endDate) throws InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(startDateField)).clear();
        driver.findElement(startDateField).sendKeys(startDate);
        wait.until(ExpectedConditions.elementToBeClickable(endDateField)).click();
        Thread.sleep(1000);
        wait.until(ExpectedConditions.elementToBeClickable(endDateField)).clear();
        driver.findElement(endDateField).sendKeys(endDate);
        Thread.sleep(1000);
    }*/
    public Boolean enterStartAndEndDate(String startDate, String endDate) {
        try {
            WebElement startDateField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"calendarStartDate_input\"]")));
            startDateField.sendKeys(startDate);

            // Pause for 2 seconds
            Thread.sleep(2000);

            // Wait for the end date field to be clickable, clear it, and enter the end date
            WebElement endDateField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"calendarEndDate_input\"]")));
            endDateField.clear();
            endDateField.sendKeys(endDate);

            // Pause for 2 seconds
            Thread.sleep(2000);

            // Clear and re-enter the end date (if required by your logic)
            endDateField = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"calendarEndDate_input\"]")));
            endDateField.clear();
            endDateField.sendKeys(endDate);
        /*    Thread.sleep(2000);
            WebElement startDateElement = wait.until(ExpectedConditions.elementToBeClickable(startDateField));
            String originalEndDate = driver.findElement(endDateField).getAttribute("value");

            startDateElement.clear();
            startDateElement.sendKeys(startDate);
            driver.findElement(By.tagName("body")).click();



            // Wait for end date to change from original value
            wait.until(ExpectedConditions.not(
                    ExpectedConditions.attributeToBe(endDateField, "value", originalEndDate)
            ));

            WebElement endDateElement = driver.findElement(endDateField);
            endDateElement.clear();
            endDateElement.sendKeys(endDate);*/
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean selectSite(String site) {
        try {
           retryingFindClick(panelSearchCriteriaHeader);
            Thread.sleep(3000);
            wait.until(ExpectedConditions.elementToBeClickable(siteDropdown)).sendKeys(site);
            Thread.sleep(1000);
            retryingFindClick(siteOption);
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean searchAvailability() {
        try {
           // retryingFindClick(searchButton);
            driver.findElement(searchButton).click();
            wait.until(ExpectedConditions.presenceOfElementLocated(selectDestinationButton));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean selectDestination() {

        try {
            retryingFindClick(selectDestinationButton);
            wait.until(ExpectedConditions.elementToBeClickable(panelReservation_header));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
