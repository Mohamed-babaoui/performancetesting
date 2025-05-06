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
    private final By AddBeneficiaryButtonCP=By.xpath("//*[@id=\"ReservationTabViewRepeater:0:panelReservationRepeaterMandatoryGroups:0:mandatoryGroupDatatable_data\"]/tr[1]/td[8]/div/button");
    private final By selectListValueFormatReqAttrLabel = By.xpath("//*[contains(@id, 'selectListValueFormatReqAttr_label')]");
    private final By selectListValueFormatReqAttrLabelCP = By.xpath("//*[contains(@id, '1:selectListValueFormatReqAttrPack') and contains(@id, 'dataTableOptionalServices2:0:serviceDetailTreeTable')]");

    private final By selectListValueFormatReqAttrOption = By.xpath("//*[contains(@id, 'selectListValueFormatReqAttr_panel')]/div/ul/li[2]");
    private final By selectListValueFormatReqAttrOptionCP = By.xpath("//*[contains(@id, '1:selectListValueFormatReqAttrPack_panel') and contains(@id, 'dataTableOptionalServices2:0:serviceDetailTreeTable')][1]/div/ul/li[2]");

    private final By addBeneficiaryConfirmButton = By.xpath("//button[@id='serviceDetailDatatable:0:addBeneficiaryButtonServiceDetailEdition']");//serviceDetailDatatable:0:addBeneficiaryButtonServiceDetailEdition
    private final By addBeneficiaryConfirmButtonCP = By.xpath("//button[@id='dataTableOptionalServices2:0:serviceDetailTreeTable:0_0_0:addBeneficiaryButtonPackageDetailEdition']");//serviceDetailDatatable:0:addBeneficiaryButtonServiceDetailEdition

    private final By serviceDetailsConfirmButton = By.xpath("//button[@id='dialogContentBeneficiaryEditionBtnValidate']");
    private final By validateOptionButton = By.xpath("//button[@id='dialogServiceDetailsEditionBtnValidate']");
    private final By validateOptionButtonCP = By.xpath("//button[@id='dialogPackageDetailsEditionBtnValidate']");

    //private final By assuranceOptionButton = By.xpath("//button[@id='ReservationTabViewRepeater:0:panelReservationRepeaterMandatoryGroups:1:mandatoryGroupDatatable:2:mandatoryGroupAdd']");
    private final By assuranceOptionButton=By.xpath("//*[@id=\"ReservationTabViewRepeater:0:panelReservationRepeaterMandatoryGroups:1:mandatoryGroupDatatable:2:mandatoryGroupAdd\"]");
    private final By assuranceOptionButtonCP=By.xpath("//button[@id='ReservationTabViewRepeater:0:panelReservationRepeaterMandatoryGroups:1:mandatoryGroupDatatable:3:j_id_m6:0:mandatoryGroupAddForCP']");

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
            //Thread.sleep(5000);
            wait.until(ExpectedConditions.visibilityOfElementLocated(bookingConfirmationOkButton));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }
    public Boolean fillReservationPredataForCP(String comment )
    {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(commentField)).sendKeys(comment);
            retryingFindClick(mandatoryGroupAmount);
            retryingFindClick(amountOption);
            retryingFindClick(AddBeneficiaryButtonCP);
            retryingFindClick(selectListValueFormatReqAttrLabelCP);
            retryingFindClick(selectListValueFormatReqAttrOptionCP);
            retryingFindClick(addBeneficiaryConfirmButtonCP);
            Thread.sleep(1000);
            retryingFindClick(serviceDetailsConfirmButton);
            Thread.sleep(1000);
            retryingFindClick(validateOptionButtonCP);
            Thread.sleep(5000);
            retryingFindClick(assuranceOptionButtonCP);
            Thread.sleep(5000);
            wait.until(ExpectedConditions.visibilityOfElementLocated(bookingConfirmationOkButton));
            Thread.sleep(5000);


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