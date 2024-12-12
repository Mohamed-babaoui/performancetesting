package pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ReservationPage extends BasePage {

    // Corrected Locators
    private final By commentField = By.id("txtExternalReference");
    private final By mandatoryGroupAmount = By.xpath("//*[@id='ReservationTabViewRepeater:0:panelReservationRepeaterMandatoryGroups:0:mandatoryGroupDatatable:0:mandatoryGroupAmount']");
    private final By amountOption = By.xpath("//*[@id='ReservationTabViewRepeater:0:panelReservationRepeaterMandatoryGroups:0:mandatoryGroupDatatable:0:mandatoryGroupAmount_panel']/div/ul/li[2]");
    //private final By addBeneficiaryButton = By.xpath("//*[contains(@id, 'mandatoryGroupAddBf')]");
    private final By addBeneficiaryButton = By.xpath("//*[@id=\"ReservationTabViewRepeater:0:panelReservationRepeaterMandatoryGroups:0:mandatoryGroupDatatable:0:j_id_m1:0:mandatoryGroupAddBf\"]");

    private final By selectListValueFormatReqAttrLabel = By.xpath("//*[contains(@id, 'selectListValueFormatReqAttr_label')]");
    private final By selectListValueFormatReqAttrOption = By.xpath("//*[contains(@id, 'selectListValueFormatReqAttr_panel')]/div/ul/li[2]");
    private final By addBeneficiaryConfirmButton = By.xpath("//button[@id='serviceDetailDatatable:0:addBeneficiaryButtonServiceDetailEdition']");//serviceDetailDatatable:0:addBeneficiaryButtonServiceDetailEdition
    private final By serviceDetailsConfirmButton = By.xpath("//button[@id='dialogContentBeneficiaryEditionBtnValidate']");
    private final By validateOptionButton = By.xpath("//button[@id='dialogServiceDetailsEditionBtnValidate']");
    //private final By assuranceOptionButton = By.xpath("//button[@id='ReservationTabViewRepeater:0:panelReservationRepeaterMandatoryGroups:1:mandatoryGroupDatatable:2:mandatoryGroupAdd']");
    private final By assuranceOptionButton=By.xpath("//*[@id=\"ReservationTabViewRepeater:0:panelReservationRepeaterMandatoryGroups:1:mandatoryGroupDatatable:2:mandatoryGroupAdd\"]");

    private final By bookingConfirmationOkButton = By.xpath("//button[@id='btValidateOption']");
    private final By bookingNumber=By.xpath("//label[@id='bookingConfirmationBookingNbLabel']");
    private final  By goToConfirmation = By.xpath("//button[@id='buttonGoToConfirmation']");
    private final By bookingConfirmationOkButtonLabel = By.xpath("//button[@id='bookingConfirmationOkBt']");
    private final  By cancelWithoutCharges = By.xpath("//button[@id='btCancelWithoutCharge']");

    public ReservationPage(WebDriver driver) {
        super(driver);
    }

    public Boolean fillReservationPredata(String comment )
    {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(commentField)).sendKeys(comment);
            retryingFindClick(mandatoryGroupAmount);
            retryingFindClick(amountOption);
            retryingFindClick(addBeneficiaryButton);
            retryingFindClick(selectListValueFormatReqAttrLabel);
            retryingFindClick(selectListValueFormatReqAttrOption);
            retryingFindClick(addBeneficiaryConfirmButton);
            Thread.sleep(1000);
            retryingFindClick(serviceDetailsConfirmButton);
            Thread.sleep(1000);
            retryingFindClick(validateOptionButton);
            Thread.sleep(5000);
            retryingFindClick(assuranceOptionButton);
            Thread.sleep(5000);
            wait.until(ExpectedConditions.visibilityOfElementLocated(bookingConfirmationOkButton));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }
    public Boolean fillReservationDetails() {
        try {
            retryingFindClick(bookingConfirmationOkButton);
            wait.until(ExpectedConditions.visibilityOfElementLocated(bookingNumber));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public String getBookingNumber() {
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(bookingNumber)).getText().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Boolean clickOkOnBookingConfirmation() {
        try {
            retryingFindClick(bookingConfirmationOkButtonLabel);
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public Boolean confirmBooking() {
        try {
            retryingFindClick(goToConfirmation);
            wait.until(ExpectedConditions.visibilityOfElementLocated(cancelWithoutCharges));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}