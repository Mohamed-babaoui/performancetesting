package pages;
// src/main/java/com/example/pages/CustomerSearchPage.java

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CustomerSearchPage extends BasePage {

    // Corrected Locators
    private By lastNameField = By.id("tabViewIdentification:inputNom");
    private By firstNameField = By.id("tabViewIdentification:inputPrenom");
    private By postalCodeField = By.id("tabViewIdentification:inputCodePostal");
    private By emailField = By.id("tabViewIdentification:inputDuChoixDropDown");
    private By searchButton = By.id("tabViewIdentification:tabItemClientButtonSearch");
    private By selectCustomerButton = By.xpath("//*[@id='tabViewIdentification:resultatClientCentralBufferTableau:0:btnCreateReservation']");
    private By startDateField = By.id("calendarStartDate_input");

    public CustomerSearchPage(WebDriver driver) {
        super(driver);
    }

    // For Scenario1: Search by name and postal code
    public void searchCustomerByNameData(String lastName, String firstName, String postalCode) {
        wait.until(ExpectedConditions.elementToBeClickable(lastNameField)).sendKeys("SANTIER");
        wait.until(ExpectedConditions.elementToBeClickable(firstNameField)).sendKeys("JEAN-MARC");
        wait.until(ExpectedConditions.elementToBeClickable(postalCodeField)).sendKeys("50350");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    // For Scenario2: Search by email
    public void searchCustomerByEmail(String email) {
        wait.until(ExpectedConditions.elementToBeClickable(emailField)).sendKeys("cjmsantier@orange.fr");
        try {
            Thread.sleep(2000);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void selectCustomer() {
        retryingFindClick(searchButton);
        wait.until(ExpectedConditions.elementToBeClickable(selectCustomerButton));
    }
    public void goToAvailability(){
        retryingFindClick(selectCustomerButton);
        wait.until(ExpectedConditions.elementToBeClickable(startDateField));
    }
}
