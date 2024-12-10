package scenarios;

import config.utils.XMLFileReader;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class PoloWSScenario {
    private static Properties props = new Properties();
    static {
        try {
            props.load(PoloWSScenario.class.getClassLoader().getResourceAsStream("WS/simulation2.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(props.getProperty("url"));
    }

    private String site = System.getProperty("site");
    private String hebergement = System.getProperty("hebergement");
    private String date = System.getProperty("date");
    private String assurance = System.getProperty("assurance");
    private String prestation = System.getProperty("prestation");
    private String url = System.getProperty("url");
    private int duration = Integer.parseInt(System.getProperty("duration"));

    Map<String, String> placeholders = Map.of(
            "Site" , site,
            "Hebergement" , hebergement,
            "Date" , date,
            "Assurance" , assurance,
            "Prestation" , prestation,
            "Url" , url
    );


    public final HttpProtocolBuilder httpProtocol = http
            .baseUrl(url)
            .disableCaching();

    private final String getQALargeBody = XMLFileReader.readXmlWithReplacePlaceholders("src/test/resources/WS/xmlBodies/getQALarge.xml", placeholders);
    private final String getQAPrecisBody = XMLFileReader.readXmlWithReplacePlaceholders("src/test/resources/WS/xmlBodies/getQAPrecis.xml", placeholders);
    private final String createBookingBody = XMLFileReader.readXmlWithReplacePlaceholders("src/test/resources/WS/xmlBodies/createBooking.xml", placeholders);
    private final String getBookingBody = XMLFileReader.readXmlFile("src/test/resources/WS/xmlBodies/getBooking.xml");
    private final String searchBookingBody = XMLFileReader.readXmlFile("src/test/resources/WS/xmlBodies/searchBooking.xml");
    private final String updateBookingBody = XMLFileReader.readXmlWithReplacePlaceholders("src/test/resources/WS/xmlBodies/updateBooking.xml", placeholders);
    private final String cancelBookingBody = XMLFileReader.readXmlFile("src/test/resources/WS/xmlBodies/cancelBooking.xml");



    private ChainBuilder getQALarge = exec(http("GetQALarge")
            .post("/")
            .body(StringBody(getQALargeBody))
            .check(status().is(200))
            .check(substring("<Status Severity=\"Success\"/>"))
    );

    private ChainBuilder getQAPrecis = doIf(session -> !session.isFailed()).then(
            exec(http("GetQAPrecis")
                .post("/")
                .body(StringBody(getQAPrecisBody))
                .check(status().is(200))
                .check(substring("<Status Severity=\"Success\"/>")))
    );

    private ChainBuilder createBooking = doIf(session -> !session.isFailed()).then(
            exec(http("CreateBooking")
                .post("/")
                .body(StringBody(createBookingBody))
                .check(status().is(200))
                .check(substring("<Status Severity=\"Success\"/>"))
                .check(bodyString().saveAs("responseBody"))
            ).exec(session -> {
                String bookingId = XMLFileReader.extractValueFromXML(session.getString("responseBody"), "//Transaction/Booking/Codes/Code[1]/@Value");
                session = session.set("bookingId", bookingId);
                if (bookingId == null || bookingId.isEmpty() || bookingId.isBlank())
                    session = session.markAsFailed();
                return session;
            })
    );

    private ChainBuilder getBooking = doIf(session -> !session.isFailed()).then(
            exec(http("GetBooking")
                .post("/")
                .body(StringBody(session -> XMLFileReader.replacePlaceholders(getBookingBody, Map.of("booking_id", session.getString("bookingId"))))) // Specify the XML body
                .check(status().is(200))
                .check(substring("<Status Severity=\"Success\"/>"))
            )
    );

    private ChainBuilder searchBooking = doIf(session -> !session.isFailed()).then(
            exec(http("SearchBooking")
                .post("/")
                .body(StringBody(session -> XMLFileReader.replacePlaceholders(searchBookingBody, Map.of("booking_id", session.getString("bookingId"))))) // Specify the XML body
                .check(status().is(200))
                .check(substring("<Status Severity=\"Success\"/>"))
            )
    );

    private ChainBuilder updateBooking = doIf(session -> !session.isFailed()).then(
            exec(http("UpdateBooking")
                .post("/")
                .body(StringBody(session -> XMLFileReader.replacePlaceholders(updateBookingBody, Map.of("booking_id", session.getString("bookingId"))))) // Specify the XML body
                .check(status().is(200))
                .check(substring("<Status Severity=\"Success\"/>"))
            )
    );

    private ChainBuilder cancelBooking = doIf(session -> !session.isFailed()).then(
            exec(http("CancelBooking")
                .post("/")
                .body(StringBody(session -> XMLFileReader.replacePlaceholders(cancelBookingBody, Map.of("booking_id", session.getString("bookingId"))))) // Specify the XML body
                .check(status().is(200))
                .check(substring("<Status Severity=\"Success\"/>"))
            )
    );

    public PoloWSScenario() throws IOException {
    }

    public ScenarioBuilder mainScenario() {
        return scenario("WSChain")
                .during(this.duration)
                .on(
                    exec(Session::markAsSucceeded)
                    .exec(getQALarge)
                    .exec(getQAPrecis)
                    .exec(createBooking)
                    .exec(getBooking)
                    .exec(searchBooking)
                    .exec(updateBooking)
                    .exec(cancelBooking)
                );
    }

}
