import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import simulations.PoloWeb1Simulation;
import simulations.PoloWeb2Simulation;

public class PoloWeb2Engine {
    public static void main(String[] args) {
        GatlingPropertiesBuilder props = new GatlingPropertiesBuilder()
                .resourcesDirectory(IDEPathHelper.mavenResourcesDirectory.toString())
                .resultsDirectory(IDEPathHelper.resultsDirectory.toString())
                .binariesDirectory(IDEPathHelper.mavenBinariesDirectory.toString())
                .simulationClass(PoloWeb2Simulation.class.getName());

        Gatling.fromMap(props.build());

        System.exit(0);
    }
}
