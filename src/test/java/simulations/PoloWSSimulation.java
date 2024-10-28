package simulations;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import scenarios.PoloWSScenario;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;

public class PoloWSSimulation extends Simulation {
    ScenarioBuilder scenario = new PoloWSScenario().mainScenario();

    {
        setUp(scenario.injectOpen(atOnceUsers(1)));
    }
}
