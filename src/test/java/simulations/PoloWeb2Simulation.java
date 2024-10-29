package simulations;

import config.utils.BrowserManager;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import scenarios.PoloWeb1Scenario;
import scenarios.PoloWeb2Scenario;

import java.io.IOException;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;

public class PoloWeb2Simulation extends Simulation {
    public PoloWeb2Scenario poloWeb2Scenario = new PoloWeb2Scenario();
    ScenarioBuilder scenario = poloWeb2Scenario.mainScenario();

    public PoloWeb2Simulation() throws IOException {
    }

    @Override
    public void before() {
        System.out.println("Before Simulation !!");
    }

    @Override
    public void after() {
        System.out.println("After Simulation !!");
        BrowserManager.cleanUpAll();
    }

    {
        setUp(
                scenario.injectOpen(atOnceUsers(1))
        );
    }
}
