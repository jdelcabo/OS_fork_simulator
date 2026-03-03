import java.util.ArrayList;

public class ForkNode extends Thread {
    // Attributes
    private final SingletonDataAnalysis dataAnalysis;
    private final int layer;
    private Type type;
    private ArrayList<Integer> indexList;
    private final ArrayList<ForkNode> forks;
    private final int pid;

    // Constructor
    public ForkNode(final int layer, final Type type, final ArrayList<Integer> indexList) {
        if (layer < 1) {
            throw new IllegalArgumentException("Id must be greater than or equal to 1");
        }
        this.dataAnalysis = SingletonDataAnalysis.getInstance();
        this.dataAnalysis.incrementForkCounter();
        this.layer = layer;
        this.type = type;
        this.forks = new ArrayList<>();
        this.pid = dataAnalysis.getForkCounter();
        this.indexList = new ArrayList<>(indexList);
    }

    @Override
    public void run() {
        if (layer == 1) {
            ForkNode x = generateNextForkNode().setType(Type.PARENT);
            x.start();
        } else if (layer > 1) {
            for (int i = indexList.get(0); i < 2; i++) {
                if (type == Type.PARENT) {
                    ForkNode x = generateNextForkNode().setIndex(0, i);
                    x.start();
                }
                if (type == Type.CHILD) {
                    ForkNode y = generateNextForkNode().setIndex(0, i+1).setType(Type.PARENT);
                    y.start();
                    setType(Type.PARENT);
                }
            }
        }
    }

    // Setter applying the builder design pattern
    private ForkNode setIndex(final int position, final int value) {
        indexList.set(position, value);
        return this;
    }

    // Setter applying the builder design pattern
    private ForkNode setType(final Type type) {
        this.type = type;
        return this;
    }

    // Method to generate the next fork node and add it to the list of forks
    private ForkNode generateNextForkNode() {
        ForkNode newNode = new ForkNode(layer + 1, Type.CHILD, indexList);
        forks.add(newNode);
        return newNode;
    }

    public void postorder(final ArrayList<ForkNode> forks) {
        for (ForkNode child : this.forks) {
            child.postorder(forks);
        }
        forks.addFirst(this);
    }

    // Getters
    public int getPid() {
        return pid;
    }

    public int getLayer() {
        return layer;
    }

    public Type getType() {
        return type;
    }
}
