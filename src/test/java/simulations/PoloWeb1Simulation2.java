package simulations;

import config.utils.BrowserManager;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import scenarios.PoloWeb1Scenario;
import scenarios.PoloWeb1Scenario2;

import java.io.IOException;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;

public class PoloWeb1Simulation2 extends Simulation {
    public PoloWeb1Scenario2 poloWeb1Scenario = new PoloWeb1Scenario2();
    ScenarioBuilder scenario = poloWeb1Scenario.mainScenario();

    public PoloWeb1Simulation2() throws IOException {
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
        ).maxDuration(600);
    }
}
