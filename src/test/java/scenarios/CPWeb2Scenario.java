package scenarios;

import config.utils.BrowserManager;
import io.gatling.javaapi.core.ScenarioBuilder;
import org.openqa.selenium.WebDriver;
import pages.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import static gatling.generic.plugin.GenericDsl.genericAction;
import static io.gatling.javaapi.core.CoreDsl.*;

public class CPWeb2Scenario {

    protected Properties config;
    protected Properties testData;

    public String baseUrl = null;
    public String username = null;
    public String email = null;
    public String password = null;
    public String site = null;
    public String dateDebut = null;
    public String dateFin = null;
    public String commentaire = null;
    public String duration = null;

    public String driver_id = null;
    public WebDriver driver = null;

    public CPWeb2Scenario() {
        config = new Properties();
        try {
            config.load(CPWeb2Scenario.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        testData = new Properties();
        try {
            testData.load(CPWeb2Scenario.class.getClassLoader().getResourceAsStream("testdata2.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        baseUrl = System.getProperty("url");
        username = config.getProperty("username");
        email = config.getProperty("emailcp");
        password = config.getProperty("password");
        site = System.getProperty("site");
        dateDebut = System.getProperty("dateDebut");
        dateFin = System.getProperty("dateFin");
        commentaire = System.getProperty("commentaire");
        duration = System.getProperty("duration");

        driver_id = BrowserManager.createWebDriver("chrome");
        driver = BrowserManager.getWebDriver(driver_id);
    }

    public ScenarioBuilder mainScenario() {

        return scenario("Polo Web - Scenario 2")
                .during(this.duration)
                .on(
                        pause(1) // Brief delay to ensure the user is registered as active
                                .exec(session -> session.set("error", false).set("errorMessage", "").markAsSucceeded())
                                .exec(session -> {
                                    // Init driver and driver_id
                                    System.out.println(driver_id);
                                    System.out.println(driver);
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
                                    return session;
                                })
                                .doIf(session -> !session.isFailed()).then(exec(genericAction("LoginPage", session -> {
                                    LoginPage loginPage = (LoginPage) session.get("loginPage");
                                    if (!loginPage.goToApp(baseUrl))
                                        return session.set("error", true).set("errorMessage", "Problem on going to login page").markAsFailed();
                                    return session;
                                })))
                                .doIf(session -> !session.isFailed()).then(exec(session -> {
                                    LoginPage loginPage = (LoginPage) session.get("loginPage");
                                    if (!loginPage.loginCred(username, password))
                                        return session.set("error", true).set("errorMessage", "Problem on login").markAsFailed();
                                    return session;
                                }))
                                .doIf(session -> !session.isFailed()).then(exec(genericAction("Authorization", session -> {
                                    LoginPage loginPage = (LoginPage) session.get("loginPage");
                                    if (!loginPage.login())
                                        return session.set("error", true).set("errorMessage", "Problem on login").markAsFailed();
                                    return session;
                                })))
                                .doIf(session -> !session.isFailed()).then(exec(session -> {
                                    CustomerSearchPage customerSearchPage = (CustomerSearchPage) session.get("customerSearchPage");
                                    if (!customerSearchPage.searchCustomerByEmail(email))
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
                                    if (!availabilityPage.enterStartAndEndDate(dateDebut, dateFin))
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
                                .doIf(session -> !session.isFailed()).then(exec(session -> {
                                    ReservationPage reservationPage = (ReservationPage) session.get("reservationPage");
                                    if (!reservationPage.fillReservationPredataForCP(commentaire))
                                        return session.set("error", true).set("errorMessage", "Problem on Pre FillReservationData").markAsFailed();
                                    return session;
                                }))
                                .doIf(session -> !session.isFailed()).then(exec(genericAction("FillReservationData", session -> {
                                    ReservationPage reservationPage = (ReservationPage) session.get("reservationPage");
                                    if (!reservationPage.fillReservationDetails())
                                        return session.set("error", true).set("errorMessage", "Problem on validating filled Data").markAsFailed();
                                    return session;
                                })))
                                .doIf(session -> !session.isFailed()).then(exec(session -> {
                                    ReservationPage reservationPage = (ReservationPage) session.get("reservationPage");

                                    String bookingNumber = reservationPage.getBookingNumber();
                                    if (bookingNumber == null || bookingNumber.isEmpty() || bookingNumber.isBlank())
                                        return session.set("error", true).set("errorMessage", "Invalid booking number").markAsFailed();

                                    try {
                                        saveBookingNumberToFile(bookingNumber, "result/bookingNumbers.txt");
                                    } catch (IOException e) {
                                        return session.set("error", true).set("errorMessage", "Error saving booking number to file: " + e.getMessage()).markAsFailed();
                                    }

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
                                    return session;
                                }))
                );
    }

    private void saveBookingNumberToFile(String bookingNumber, String filePath) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(bookingNumber + " " + timestamp);
            writer.newLine();
        }
    }

}
