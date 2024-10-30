package scenarios;

import config.utils.XMLFileReader;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
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


    private ChainBuilder getQALarge = exec(http("GetQALarge")
            .post("/")
            .body(StringBody(getQALargeBody))
            .check(status().is(200))
    );

    private ChainBuilder getQAPrecis = exec(http("GetQAPrecis")
            .post("/")
            .body(StringBody(getQAPrecisBody))
            .check(status().is(200))
    );

    private ChainBuilder createBooking = exec(http("CreateBooking")
            .post("/")
            .body(StringBody(createBookingBody))
            .check(status().is(200))
            .check(bodyString().saveAs("responseBody"))
    );

    private ChainBuilder getBooking = exec(http("GetBooking")
            .post("/")
            .body(StringBody(session -> {
                String bookingId = XMLFileReader.extractValueFromXML(session.getString("responseBody"), "//Transaction/Booking/Price/@Value");
                return XMLFileReader.readXmlWithReplacePlaceholders("src/test/resources/WS/xmlBodies/getBooking.xml", Map.of("booking_id", bookingId));
            })) // Specify the XML body
            .check(status().is(200))
    );

    private ChainBuilder searchBooking = exec(http("SearchBooking")
            .post("/")
            .body(StringBody(session -> {
                String bookingId = XMLFileReader.extractValueFromXML(session.getString("responseBody"), "//Transaction/Booking/Price/@Value");
                return XMLFileReader.readXmlWithReplacePlaceholders("src/test/resources/WS/xmlBodies/searchBooking.xml", Map.of("booking_id", bookingId));
            })) // Specify the XML body
            .check(status().is(200))
    );

    private ChainBuilder updateBooking = exec(http("UpdateBooking")
            .post("/")
            .body(StringBody(session -> {
                String bookingId = XMLFileReader.extractValueFromXML(session.getString("responseBody"), "//Transaction/Booking/Price/@Value");
                return XMLFileReader.readXmlWithReplacePlaceholders("src/test/resources/WS/xmlBodies/updateBooking.xml", Map.of("booking_id", bookingId, "Prestation", prestation));
            })) // Specify the XML body
            .check(status().is(200))
    );

    private ChainBuilder cancelBooking = exec(http("CancelBooking")
            .post("/")
            .body(StringBody(session -> {
                String bookingId = XMLFileReader.extractValueFromXML(session.getString("responseBody"), "//Transaction/Booking/Price/@Value");
                return XMLFileReader.readXmlWithReplacePlaceholders("src/test/resources/WS/xmlBodies/cancelBooking.xml", Map.of("booking_id", bookingId));
            })) // Specify the XML body
            .check(status().is(200))
    );

    public ScenarioBuilder mainScenario() {
        return scenario("WSChain")
                .during(this.duration)
                .on(
                    exec(getQALarge)
                    .exec(getQAPrecis)
                    .exec(createBooking)
                    .exec(getBooking)
                    .exec(searchBooking)
                    .exec(updateBooking)
                    .exec(cancelBooking)
                );
    }

}
