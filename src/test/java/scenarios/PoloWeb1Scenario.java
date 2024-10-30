package scenarios;

import config.utils.BrowserManager;
import io.gatling.javaapi.core.ScenarioBuilder;
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

    public PoloWeb1Scenario() {
        config = new Properties();
        try {
            config.load(PoloWeb1Scenario.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        testData = new Properties();
        try {
            testData.load(PoloWeb1Scenario.class.getClassLoader().getResourceAsStream("testdata.properties"));
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
    }

    public ScenarioBuilder mainScenario() {

        return scenario("Polo Web - Scenario 1")
            .during(this.duration)
            .on(
                pause(1) // Brief delay to ensure the user is registered as active
                .exec(session -> {
                    // Init driver and driver_id
                    String driver_id = BrowserManager.createWebDriver("chrome");
                    WebDriver driver = BrowserManager.getWebDriver(driver_id);
                    // Save driver and driver_id in session
                    session = session.set("driver_id", driver_id);
                    session = session.set("driver", driver);
                    // Init Page Objects
                    LoginPage loginPage = new LoginPage(driver);
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
                    driver.get(baseUrl);
                    return session;
                })
                .exec(genericAction("Authorization", session -> {
                    // Step 1: Authorization
                    LoginPage loginPage = (LoginPage) session.get("loginPage");
                    loginPage.loginCred(username, password);
                    loginPage.login();
                    return session;
                }))
                .exec(session -> {
                    CustomerSearchPage customerSearchPage = (CustomerSearchPage) session.get("customerSearchPage");
                    customerSearchPage.searchCustomerByNameData("SANTIER", "JEAN-MARC", "50350");
                    return session;
                })
                .exec(genericAction("Fill Client Data", session -> {
                    CustomerSearchPage customerSearchPage = (CustomerSearchPage) session.get("customerSearchPage");
                    customerSearchPage.selectCustomer();
                    return session;
                }))
                .exec(genericAction("Client Found", session -> {
                    CustomerSearchPage customerSearchPage = (CustomerSearchPage) session.get("customerSearchPage");
                    customerSearchPage.goToAvailability();
                    return session;
                }))
                .exec(session -> {
                    AvailabilityPage availabilityPage = (AvailabilityPage) session.get("availabilityPage");
                    availabilityPage.enterStartDate(dateDebut);
                    availabilityPage.selectSite(site);
                    return session;
                })
                .exec(genericAction("Search Availability", session -> {
                    AvailabilityPage availabilityPage = (AvailabilityPage) session.get("availabilityPage");
                    availabilityPage.searchAvailability();
                    return session;
                }))
                .exec(genericAction("Go To Reservation Detail", session -> {
                    AvailabilityPage availabilityPage = (AvailabilityPage) session.get("availabilityPage");
                    availabilityPage.selectDestination();
                    return session;
                }))
                .exec(genericAction("Fill Reservation Data", session -> {
                    ReservationPage reservationPage = (ReservationPage) session.get("reservationPage");
                    reservationPage.fillReservationDetails(commentaire);
                    return session;
                }))
                .exec(session -> {
                    // Get Booking Number
                    ReservationPage reservationPage = (ReservationPage) session.get("reservationPage");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    String bookingNumber = reservationPage.getBookingNumber();
                    reservationPage.clickOkOnBookingConfirmation();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    bookingNumber = "";

                    if (bookingNumber == null || bookingNumber.isEmpty() || bookingNumber.isBlank())
                        throw new RuntimeException("Booking number should not be null");

                    session = session.set("bookingNumber", bookingNumber);
                    return session;
                })
                .exec(genericAction("Confirmation", session -> {
                    ReservationPage reservationPage = (ReservationPage) session.get("reservationPage");
                    reservationPage.confirmBooking();
                    return session;
                }))
                .exec(session -> {
                    ConfirmationPage confirmationPage = (ConfirmationPage) session.get("confirmationPage");
                    confirmationPage.cancelBooking();
                    BrowserManager.deleteWebDriver((String)session.get("driver_id"));
                    return session;
                })
            );
    }
}
