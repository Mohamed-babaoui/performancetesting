package pages;
// src/main/java/com/example/pages/CustomerSearchPage.java

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.awt.print.Book;

public class CustomerSearchPage extends BasePage {

    // Corrected Locators
    private By lastNameField = By.id("tabViewIdentification:inputNom");
    private By firstNameField = By.id("tabViewIdentification:inputPrenom");
    private By postalCodeField = By.id("tabViewIdentification:inputCodePostal");
    private By emailField = By.id("tabViewIdentification:inputDuChoixDropDown");
    private By searchButton = By.xpath("//*[@id=\"tabViewIdentification:tabItemClientButtonSearch\"]");
    private By selectCustomerButton = By.xpath("//*[@id='tabViewIdentification:resultatClientCentralBufferTableau:0:btnCreateReservation']");
    private By startDateField = By.xpath("//*[@id=\"calendarStartDate_input\"]");

    public CustomerSearchPage(WebDriver driver) {
        super(driver);
    }

    // For Scenario1: Search by name and postal code
    public Boolean searchCustomerByNameData(String lastName, String firstName, String postalCode) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(lastNameField)).sendKeys(lastName);
            wait.until(ExpectedConditions.elementToBeClickable(firstNameField)).sendKeys(firstName);
            wait.until(ExpectedConditions.elementToBeClickable(postalCodeField)).sendKeys(postalCode);
            driver.findElement(By.tagName("body")).click();
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    // For Scenario2: Search by email
    public Boolean searchCustomerByEmail(String email) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(emailField)).sendKeys(email);
            driver.findElement(By.tagName("body")).click();
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public Boolean selectCustomer() {
        try {
            retryingFindClick(searchButton);
            wait.until(ExpectedConditions.elementToBeClickable(selectCustomerButton));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public Boolean goToAvailability(){
        try {
            retryingFindClick(selectCustomerButton);
            wait.until(ExpectedConditions.elementToBeClickable(startDateField));
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
