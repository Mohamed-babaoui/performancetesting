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
    public void goToApp(String url) {
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
    }

    public void loginCred(String username, String password) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        wait.until(ExpectedConditions.elementToBeClickable(usernameField)).sendKeys(username);
        wait.until(ExpectedConditions.elementToBeClickable(passwordField)).sendKeys(password);
    }


    public void login() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
        wait.until(ExpectedConditions.elementToBeClickable(lastNameField));
    }

}
