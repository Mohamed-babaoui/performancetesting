import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import simulations.PoloWSSimulation;
import simulations.PoloWeb1Simulation;

public class PoloWSEngine {
    public static void main(String[] args) {
        GatlingPropertiesBuilder props = new GatlingPropertiesBuilder()
                .resourcesDirectory(IDEPathHelper.mavenResourcesDirectory.toString())
                .resultsDirectory(IDEPathHelper.resultsDirectory.toString())
                .binariesDirectory(IDEPathHelper.mavenBinariesDirectory.toString())
                .simulationClass(PoloWSSimulation.class.getName());

        Gatling.fromMap(props.build());

        System.exit(0);
    }
}
