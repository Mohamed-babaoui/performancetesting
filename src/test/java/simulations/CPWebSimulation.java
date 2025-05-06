package simulations;

import config.utils.BrowserManager;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import scenarios.CPWeb1Scenario;
import scenarios.CPWeb2Scenario;


import java.io.IOException;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;

public class CPWebSimulation extends Simulation {
    public CPWeb2Scenario cpWeb2Scenario = new CPWeb2Scenario();
    ScenarioBuilder scenario2 = cpWeb2Scenario.mainScenario();

    public CPWeb1Scenario cpWeb1Scenario = new CPWeb1Scenario();
    ScenarioBuilder scenario1 = cpWeb1Scenario.mainScenario();





    public CPWebSimulation() throws IOException {
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
