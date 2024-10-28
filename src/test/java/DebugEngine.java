import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import simulations.DebugSimulation;
import simulations.PoloWSSimulation;

public class DebugEngine {
    public static void main(String[] args) {
        GatlingPropertiesBuilder props = new GatlingPropertiesBuilder()
                .resourcesDirectory(IDEPathHelper.mavenResourcesDirectory.toString())
                .resultsDirectory(IDEPathHelper.resultsDirectory.toString())
                .binariesDirectory(IDEPathHelper.mavenBinariesDirectory.toString())
                .simulationClass(DebugSimulation.class.getName());

        Gatling.fromMap(props.build());

        System.exit(0);
    }
}
