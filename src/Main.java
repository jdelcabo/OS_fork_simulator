import java.util.ArrayList;
import java.util.List;

public class Main {
    static void main() {
        // Execution of forking simulation
        final ForkNode initialNode = new ForkNode(1, Type.PARENT, new ArrayList<>(List.of(0)));
        initialNode.start();

        // User Interface (to interact while the simulation is running)
        final UserInterface ui = new UserInterface();
        ui.start();

        // Display of data analysis results after the UI is closed
        final SingletonDataAnalysis dataAnalysis = SingletonDataAnalysis.getInstance();
        dataAnalysis.printDataAnalysis(initialNode);
    }
}
