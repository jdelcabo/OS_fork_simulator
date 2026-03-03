import java.util.ArrayList;

public class SingletonDataAnalysis {
    // Attributes
    private static SingletonDataAnalysis instance;
    private int forkCounter;

    // Constructor
    private SingletonDataAnalysis() {
        forkCounter = 0;
    }

    // Methods
    public static SingletonDataAnalysis getInstance() {
        if (instance == null) {
            instance = new SingletonDataAnalysis();
        }
        return instance;
    }

    public synchronized void incrementForkCounter() {
        forkCounter++;
    }

    // Getter
    public synchronized int getForkCounter() {
        return forkCounter;
    }

    // Sorted postorder binary tree list of all fork nodes (for structural representation purposes)
    public ArrayList<ForkNode> getSortedPostorderBinaryTreeList(final ForkNode root) {
        final ArrayList<ForkNode> result = new ArrayList<>();

        root.postorder(result);

        return result;
    }

    public void printDataAnalysis(final ForkNode root) {

        System.out.println("================================================");
        System.out.println("------------------DATA ANALYSIS-----------------");
        System.out.println("================================================" + System.lineSeparator());
        System.out.println("* Total number of fork nodes: " + getForkCounter() + System.lineSeparator());

        System.out.println("* Tree structure of fork nodes:" + System.lineSeparator());
        ArrayList<ForkNode> sortedPostorderBinaryTreeList = getSortedPostorderBinaryTreeList(root);
        for (int i = 0; i < sortedPostorderBinaryTreeList.size(); i++) {
            ForkNode temp = sortedPostorderBinaryTreeList.get(i);
            StringBuilder str = new StringBuilder();
            str.append("\t".repeat(temp.getLayer()-1));
            str
                    .append("Fork node with PID ")
                    .append(temp.getPid())
                    .append(" is a ")
                    .append(temp.getType())
                    .append(" and is located at layer ")
                    .append(temp.getLayer());

            System.out.println(str);
        }
    }

}
