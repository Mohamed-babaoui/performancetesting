package pages;
// src/main/java/com/example/pages/LoginPage.java

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

    // Corrected Locators
    private final By usernameField = By.id("inputLoginUserName");
    private final By passwordField = By.id("inputLoginPassword");
    private final By loginButton = By.id("btnLoginGetConnected");
    private By lastNameField = By.id("tabViewIdentification:inputNom");


    public LoginPage(WebDriver driver) {
        super(driver);
    }
    public Boolean goToApp(String url) {
        try {
            driver.get(url);
            wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean loginCred(String username, String password) {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        try {
            wait.until(ExpectedConditions.elementToBeClickable(usernameField)).sendKeys(username);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            wait.until(ExpectedConditions.elementToBeClickable(passwordField)).sendKeys(password);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public Boolean login() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            wait.until(ExpectedConditions.elementToBeClickable(lastNameField));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
