package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ConfirmationPage extends BasePage {

    // Corrected Locators
    private final  By cancelWithoutCharges = By.xpath("//button[@id='btCancelWithoutCharge']");
    private final  By dropdownBookingCancelReason_label = By.xpath("//*[@id='dropdownBookingCancelReason_label']");
    private final  By dropdownBookingCancelReason_label_choice = By.xpath("//*[@id='dropdownBookingCancelReason_panel']/div/ul/li[4]");
    private final  By btnCancelYes = By.xpath("//*[@id='btnCancelYes']");
    private final  By lastBtnCancelYes = By.xpath("//*[@id='btOKDlgBkgCancelled']");

    public ConfirmationPage(WebDriver driver) {
        super(driver);
    }



    public Boolean cancelBooking() {
        try {
            retryingFindClick(cancelWithoutCharges);
            retryingFindClick(dropdownBookingCancelReason_label);
            retryingFindClick(dropdownBookingCancelReason_label_choice);
            retryingFindClick(btnCancelYes);
            retryingFindClick(lastBtnCancelYes);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}