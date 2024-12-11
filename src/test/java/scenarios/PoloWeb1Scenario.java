package scenarios;

import config.utils.BrowserManager;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Session;
import org.openqa.selenium.WebDriver;
import pages.*;

import java.io.IOException;
import java.util.Properties;

import static gatling.generic.plugin.GenericDsl.genericAction;
import static io.gatling.javaapi.core.CoreDsl.*;

public class PoloWeb1Scenario {

    protected Properties config;
    protected Properties testData;

    public String baseUrl = null;
    public String username = null;
    public String password = null;
    public String site = null;
    public String dateDebut = null;
    public String commentaire = null;
    public String duration = null;

    public String driver_id = null;
    public WebDriver driver = null;

    public PoloWeb1Scenario() {
        config = new Properties();
        try {
            config.load(PoloWeb1Scenario.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        testData = new Properties();
        try {
            testData.load(PoloWeb1Scenario.class.getClassLoader().getResourceAsStream("testdata2.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        baseUrl = System.getProperty("url");
        username = config.getProperty("username");
        password = config.getProperty("password");
        site = System.getProperty("site");
        dateDebut = System.getProperty("dateDebut");
        commentaire = System.getProperty("commentaire");
        duration = System.getProperty("duration");

        driver_id = BrowserManager.createWebDriver("chrome");
        driver = BrowserManager.getWebDriver(driver_id);
    }

    public ScenarioBuilder mainScenario() {

        return scenario("Polo Web - Scenario 1")
                .during(this.duration)
                .on(
                        pause(1) // Brief delay to ensure the user is registered as active
                                .exec(session -> session.set("error", false).set("errorMessage", "").markAsSucceeded())
                                .exec(session -> {
                                    // Init driver and driver_id
                    /*String driver_id = BrowserManager.createWebDriver("chrome");
                    WebDriver driver = BrowserManager.getWebDriver(driver_id);*/
                                    // Save driver and driver_id in session
                                    System.out.println(driver_id);
                                    System.out.println(driver);
                                    session = session.set("driver_id", driver_id);
                                    session = session.set("driver", driver);
                                    // Init Page Objects
                                    LoginPage loginPage = new LoginPage(driver);
                                    System.out.println("login page" + loginPage);
                                    CustomerSearchPage customerSearchPage = new CustomerSearchPage(driver);
                                    AvailabilityPage availabilityPage = new AvailabilityPage(driver);
                                    ReservationPage reservationPage = new ReservationPage(driver);
                                    ConfirmationPage confirmationPage = new ConfirmationPage(driver);
                                    // Save page objects in session
                                    session = session.set("loginPage", loginPage);
                                    session = session.set("customerSearchPage", customerSearchPage);
                                    session = session.set("availabilityPage", availabilityPage);
                                    session = session.set("reservationPage", reservationPage);
                                    session = session.set("confirmationPage", confirmationPage);
                                    // Launch browser
                                    try {
                                        driver.get(baseUrl);

                                    } catch (Exception e) {
                                        return session.set("error", true).set("errorMessage", e.getMessage()).markAsFailed();
                                    }


                                    return session;
                                })
                                .doIf(session -> !session.isFailed()).then(exec(genericAction("Authorization", session -> {
                                    // Step 1: Authorization
                                    LoginPage loginPage = (LoginPage) session.get("loginPage");
                                    if (!loginPage.loginCred(username, password))
                                        return session.set("error", true).set("errorMessage", "Problem on login").markAsFailed();
                                    if (!loginPage.login())
                                        return session.set("error", true).set("errorMessage", "Problem on login").markAsFailed();
                                    return session;
                                })))
                                .doIf(session -> !session.isFailed()).then(exec(session -> {
                                    CustomerSearchPage customerSearchPage = (CustomerSearchPage) session.get("customerSearchPage");
                                    if (!customerSearchPage.searchCustomerByNameData("SANTIER", "JEAN-MARC", "50350"))
                                        return session.set("error", true).set("errorMessage", "Problem on searching customer").markAsFailed();
                                    return session;
                                }))
                                .doIf(session -> !session.isFailed()).then(exec(genericAction("FillClientData", session -> {
                                    CustomerSearchPage customerSearchPage = (CustomerSearchPage) session.get("customerSearchPage");
                                    if (!customerSearchPage.selectCustomer())
                                        return session.set("error", true).set("errorMessage", "Problem on selecting customer").markAsFailed();
                                    return session;
                                })))
                                .doIf(session -> !session.isFailed()).then(exec(genericAction("ClientFound", session -> {
                                    CustomerSearchPage customerSearchPage = (CustomerSearchPage) session.get("customerSearchPage");
                                    if (!customerSearchPage.goToAvailability())
                                        return session.set("error", true).set("errorMessage", "Problem on going to availability").markAsFailed();
                                    return session;
                                })))
                                .doIf(session -> !session.isFailed()).then(exec(session -> {
                                    AvailabilityPage availabilityPage = (AvailabilityPage) session.get("availabilityPage");
                                    if (!availabilityPage.enterStartDate(dateDebut))
                                        return session.set("error", true).set("errorMessage", "Problem on entering start/end date").markAsFailed();
                                    if (!availabilityPage.selectSite(site))
                                        return session.set("error", true).set("errorMessage", "Problem on selecting site").markAsFailed();
                                    return session;
                                }))
                                .doIf(session -> !session.isFailed()).then(exec(genericAction("SearchAvailability", session -> {
                                    AvailabilityPage availabilityPage = (AvailabilityPage) session.get("availabilityPage");
                                    if (!availabilityPage.searchAvailability())
                                        return session.set("error", true).set("errorMessage", "Problem on searching availability").markAsFailed();
                                    return session;
                                })))
                                .doIf(session -> !session.isFailed()).then(exec(genericAction("GoToReservationDetail", session -> {
                                    AvailabilityPage availabilityPage = (AvailabilityPage) session.get("availabilityPage");
                                    if (!availabilityPage.selectDestination())
                                        return session.set("error", true).set("errorMessage", "Problem on selecting destination").markAsFailed();
                                    return session;
                                })))
                                .doIf(session -> !session.isFailed()).then(exec(genericAction("FillReservationData", session -> {
                                    ReservationPage reservationPage = (ReservationPage) session.get("reservationPage");
                                    if (!reservationPage.fillReservationDetails(commentaire))
                                        return session.set("error", true).set("errorMessage", "Problem on filling reservation details").markAsFailed();
                                    return session;
                                })))
                                .doIf(session -> !session.isFailed()).then(exec(session -> {
                                    // Get Booking Number
                                    ReservationPage reservationPage = (ReservationPage) session.get("reservationPage");

                                    String bookingNumber = reservationPage.getBookingNumber();
                                    if (bookingNumber == null || bookingNumber.isEmpty() || bookingNumber.isBlank())
                                        return session.set("error", true).set("errorMessage", "Invalid booking number").markAsFailed();

                                    session = session.set("bookingNumber", bookingNumber);
                                    if (!reservationPage.clickOkOnBookingConfirmation())
                                        return session.set("error", true).set("errorMessage", "Problem on clicking booking confirmation").markAsFailed();

                                    return session;
                                }))
                                .doIf(session -> !session.isFailed()).then(exec(genericAction("Confirmation", session -> {
                                    ReservationPage reservationPage = (ReservationPage) session.get("reservationPage");
                                    if (!reservationPage.confirmBooking())
                                        return session.set("error", true).set("errorMessage", "Problem on confirm booking").markAsFailed();
                                    return session;
                                })))
                                .doIf(session -> !session.isFailed()).then(exec(session -> {
                                    ConfirmationPage confirmationPage = (ConfirmationPage) session.get("confirmationPage");
                                    if (!confirmationPage.cancelBooking())
                                        return session.set("error", true).set("errorMessage", "problem on cancel booking").markAsFailed();
                                    /*BrowserManager.deleteWebDriver((String)session.get("driver_id"));*/
                                    return session;
                                }))
                );
    }
}
