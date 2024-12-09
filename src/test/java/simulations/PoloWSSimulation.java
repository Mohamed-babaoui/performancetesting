package simulations;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import scenarios.PoloWSScenario;

import java.io.IOException;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;

public class PoloWSSimulation extends Simulation {
    PoloWSScenario poloWS = new PoloWSScenario();

    {
        setUp(
                poloWS.mainScenario()
                        .injectOpen(atOnceUsers(1))
                        .protocols(poloWS.httpProtocol));
    }

    public PoloWSSimulation() throws IOException {
    }
}
