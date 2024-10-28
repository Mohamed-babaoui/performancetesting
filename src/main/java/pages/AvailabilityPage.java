package pages;
// src/main/java/com/example/pages/AvailabilityPage.java

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class AvailabilityPage extends BasePage {

    // Corrected Locators
    private By startDateField = By.id("calendarStartDate_input");
    private By endDateField = By.id("calendarEndDate_input");
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
    public void enterStartDate(String startDate) {
        wait.until(ExpectedConditions.elementToBeClickable(startDateField)).clear();
        driver.findElement(startDateField).sendKeys(startDate);
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
    public void enterStartAndEndDate(String startDate, String endDate) {
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
        endDateElement.sendKeys(endDate);
    }

    public void selectSite(String site) {
        retryingFindClick(panelSearchCriteriaHeader);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        wait.until(ExpectedConditions.elementToBeClickable(siteDropdown)).sendKeys(site);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        retryingFindClick(siteOption);
    }

    public void searchAvailability() {
        retryingFindClick(searchButton);
        wait.until(ExpectedConditions.elementToBeClickable(selectDestinationButton));
    }

    public void selectDestination() {
        retryingFindClick(selectDestinationButton);
        wait.until(ExpectedConditions.elementToBeClickable(panelReservation_header));
    }
}
