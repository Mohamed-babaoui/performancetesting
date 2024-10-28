package simulations;

import io.gatling.javaapi.core.Simulation;
import scenarios.PoloWSScenario;
import scenarios.PoloWeb1Scenario;

import static io.gatling.javaapi.core.CoreDsl.*;

public class DebugSimulation extends Simulation {
    PoloWSScenario poloWS = new PoloWSScenario();
    PoloWeb1Scenario poloWeb1 = new PoloWeb1Scenario();

    {
        setUp(
                poloWS.mainScenario()
                        .injectOpen(
                                atOnceUsers(1)
                        )
                        .protocols(poloWS.httpProtocol),

                poloWeb1.mainScenario()
                        .injectOpen(
                                nothingFor(10), // Wait for 1 hour before starting the second scenario
                                atOnceUsers(1)
                        )
        ).maxDuration(60); // Total duration of 2 hours
    }
}
