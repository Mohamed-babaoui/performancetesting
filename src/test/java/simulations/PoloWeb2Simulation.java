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
    ScenarioBuilder scenario2 = poloWeb2Scenario.mainScenario();

    public PoloWeb1Scenario poloWeb1Scenario = new PoloWeb1Scenario();
    ScenarioBuilder scenario1 = poloWeb1Scenario.mainScenario();

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
                scenario2.injectOpen(atOnceUsers(1)),
                scenario1.injectOpen(atOnceUsers(1))
        );
    }
}
