package simulations;

import config.utils.BrowserManager;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import scenarios.PoloWeb1Scenario;

import java.io.IOException;

import static io.gatling.javaapi.core.CoreDsl.*;

public class PoloWeb1Simulation extends Simulation{
    public PoloWeb1Scenario poloWeb1Scenario = new PoloWeb1Scenario();
    ScenarioBuilder scenario = poloWeb1Scenario.mainScenario();

    public PoloWeb1Simulation() throws IOException {
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
