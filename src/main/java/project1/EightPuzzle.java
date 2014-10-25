package project1;

import java.util.*;

/**
 * Created by Xing HU on 10/23/14.
 */
public class EightPuzzle {

    private final int PUZZLE_LEN_ = 9;
    private final List<int[]> BOARD_;
    private final HashMap<Integer, int[]> MANHATTAN_DIS_;
    private final Random RAND_ = new Random();

    private int numNode = 0;

    private boolean enableH1 = true;
    private boolean enableH2 = true;
    private boolean isBatchTest = false;

    private class Node {
        private int fn_ = 0;
        private int gn_ = 0;
        private int[] state_ = new int[PUZZLE_LEN_];
        private int zeroPos_ = -1;
        private Node parent_ = null;

        public Node(int fn, int gn, int[] state, int zeroPos, Node parent) {
            this.fn_ = fn;
            this.gn_ = gn;
            this.state_ = state;
            this.zeroPos_ = zeroPos;
            this.parent_ = parent;

            ++numNode;
        }

        public int getFn_() {
            return fn_;
        }

        public int getGn_() {
            return gn_;
        }

        public int[] getState_() {
            return state_;
        }

        public int getZeroPos_() {
            return zeroPos_;
        }

        public Node getParent_() {
            return parent_;
        }
    }

    // comparator for PriorityQueue
    private Comparator<Node> comparator = new Comparator<Node>() {
        @Override
        public int compare(Node o1, Node o2) {
            return o1.fn_ - o2.fn_;
        }
    };


    public EightPuzzle() {

        BOARD_ = new ArrayList<>();

        BOARD_.add(new int[] {1, 3});
        BOARD_.add(new int[] {0, 2, 4});
        BOARD_.add(new int[] {1, 5});
        BOARD_.add(new int[] {0, 4, 6});
        BOARD_.add(new int[] {1, 3, 5, 7});
        BOARD_.add(new int[] {2, 4, 8});
        BOARD_.add(new int[] {3, 7});
        BOARD_.add(new int[] {4, 6, 8});
        BOARD_.add(new int[] {5, 7});

        MANHATTAN_DIS_ = new HashMap<>();

        // distance to original place.   0  1  2  3  4  5  6  7  8
        MANHATTAN_DIS_.put(0, new int[] {0, 1, 2, 1, 2, 3, 2, 3, 4});
        MANHATTAN_DIS_.put(1, new int[] {1, 0, 1, 2, 1, 2, 3, 2, 3});
        MANHATTAN_DIS_.put(2, new int[] {2, 1, 0, 3, 2, 1, 4, 3, 2});
        MANHATTAN_DIS_.put(3, new int[] {1, 2, 3, 0, 1, 2, 1, 2, 3});
        MANHATTAN_DIS_.put(4, new int[] {2, 1, 2, 1, 0, 1, 2, 1, 2});
        MANHATTAN_DIS_.put(5, new int[] {3, 2, 1, 2, 1, 0, 3, 2, 1});
        MANHATTAN_DIS_.put(6, new int[] {2, 3, 4, 1, 2, 3, 0, 1, 2});
        MANHATTAN_DIS_.put(7, new int[] {3, 2, 3, 2, 1, 2, 1, 0, 1});
        MANHATTAN_DIS_.put(8, new int[] {4, 3, 2, 3, 2, 1, 2, 1, 0});
    }

    public void setEnableH1(boolean enableH1) {
        this.enableH1 = enableH1;
    }

    public void setEnableH2(boolean enableH2) {
        this.enableH2 = enableH2;
    }

    private int[] swap(int[] state, int pos1, int pos2) {
        int[] newState = state.clone();
        newState[pos1] = state[pos2];
        newState[pos2] = state[pos1];

        return newState;
    }

    private void printNode(Node node) {
        int[] state = node.getState_();
        System.out.println("-----");
//        System.out.println(state[0] + " " + state[1] + " " + state[2] + " fn=" + node.getFn_());
//        System.out.println(state[3] + " " + state[4] + " " + state[5] + " gn=" + node.getGn_());
        System.out.println(state[0] + " " + state[1] + " " + state[2]);
        System.out.println(state[3] + " " + state[4] + " " + state[5]);
        System.out.println(state[6] + " " + state[7] + " " + state[8]);
    }

    private int getZeroPos(int[] state) {
        for (int idx = 0; idx < state.length; ++idx)
            if (state[idx] == 0)
                return idx;

        return -1;
    }

    private boolean checkGoal(int[] record) {
        for (int idx = 0; idx < PUZZLE_LEN_; ++idx)
            if (record[idx] != idx)
                return false;

        return true;
    }

    private int getH1(int[] state) {
        int estimatedCost = 0;
        for (int idx = 0; idx < state.length; ++idx)
            if (state[idx] != idx)
                ++estimatedCost;

        if (enableH1)
            return estimatedCost;
        else
            return 0;
    }

    private int getH2(int[] state) {
        int estimatedCost = 0;
        for (int idx = 0; idx < state.length; ++idx)
            estimatedCost += MANHATTAN_DIS_.get(state[idx])[idx];

        if (enableH2)
            return estimatedCost;
        else
            return 0;
    }


    private void addFrontiers(PriorityQueue<Node> pq, Node current, Node parent) {
        int[] state = current.getState_();
        int zeroPos = current.getZeroPos_();
        int gn = current.getGn_() + 1;

        int[] prevState = null;
        if (current.getParent_() != null)
            prevState = current.getParent_().getState_();

        for (int move : BOARD_.get(zeroPos)) {
            int[] newState = swap(state, move, zeroPos);

            // skip the previous state to avoid stepping back
            if ((prevState == null) || (!Arrays.equals(newState, prevState))) {
                int fn = gn + getH1(newState) + getH2(newState);

                pq.add(new Node(fn, gn, newState, getZeroPos(newState), current));
            }

        }
    }


    private int[] getInitState() {
        Scanner sc = new Scanner(System.in);
        int[] state = new int[PUZZLE_LEN_];
        for (int idx = 0; idx < 3; ++idx) {
            String[] line = sc.nextLine().split(" ");
            state[idx*3] = Integer.valueOf(line[0]);
            state[idx*3+1] = Integer.valueOf(line[1]);
            state[idx*3+2] = Integer.valueOf(line[2]);
        }

        return state;
    }

    private boolean checkSolvable(int[] state) {
        int numInversions = 0;
        for (int i = 0; i < state.length; ++i)
            for (int j = i + 1; j < state.length; ++j)
                if ((state[j] != 0) && (state[i] > state[j]))
                    ++numInversions;

//        System.out.println("Number of Inversions: " + numInversions);

        if (numInversions % 2 == 0)
            return true;
        else
            return false;
    }

    private boolean isStateExplored(List<Node> explored, Node current) {
        for (Node node : explored)
            if (node.getState_().equals(current.getState_()))
                return true;

        return false;
    }

    private int search(int[] state) {
        List<Node> explored = new ArrayList<>();    // record explored nodes
        PriorityQueue<Node> frontiers               // record frontier nodes
                = new PriorityQueue<>(9, comparator);
        int zeroPos = getZeroPos(state);            // zero's position in current state
        int fn = 0;                                 // evaluation function
        int gn = 0;                                 // steps have taken so far

        // must have zero in the input
        if (zeroPos < 0) {
            System.out.println("Input wrong");
            return 0;
        }

        fn = gn + getH1(state) + getH2(state);

        if (fn == 0) {
            System.out.println("Already at the original state");
            return 0;
        }

        numNode = 0;

        // create the root node
        Node root = new Node(fn, gn, state, zeroPos, null);
        addFrontiers(frontiers, root, null);

        if (!isBatchTest)
            printNode(root);

        while (frontiers.size() > 0) {
            Node current = frontiers.remove();
//            if (!explored.contains(current.getParent_()))
//                explored.add(current.getParent_());
            if (isStateExplored(explored, current))
                continue;

            if (!isStateExplored(explored, current.getParent_()))
                explored.add(current.getParent_());

            if (!checkGoal(current.getState_()))
                addFrontiers(frontiers, current, current.getParent_());
            else {
                explored.add(current);
                break;
            }
        }

        List<Node> forPrint = new ArrayList<>();
        Node node = explored.get(explored.size()-1);
        while (node != null) {
            forPrint.add(0, node);  // add front, reverse the list
            node = node.getParent_();
        }

        if (!isBatchTest) {
            for (Node n : forPrint) {
                printNode(n);
            }
            System.out.println("Number of Steps: " + (forPrint.size() - 1));
            System.out.println("Number of Nodes: " + numNode);
        }

        return (forPrint.size() - 1); // for analysis
    }


    public void run() {
        int[] state = getInitState();

        if (checkSolvable(state)) {
            search(state);
        } else
            System.out.println("Unsolvable!");
    }

    public void runRandom(int times) {
        int[] runTimes = new int[20];
        int[] nodeNum = new int[20];

        for (int time = 0; time < times; ++time) {
            int[] state = new int[PUZZLE_LEN_];
            HashSet<Integer> record = new HashSet<>();

            // generate randomized state
            for (int idx = 0; idx < PUZZLE_LEN_; ++idx) {
                int num = RAND_.nextInt(9);
                if (record.contains(num)) {
                    --idx;
                    continue;
                } else {
                    state[idx] = num;
                    record.add(num);
                }
            }

            if (checkSolvable(state)) {
                System.out.println("Test " + (time + 1) + ":");
                int length = search(state);
                if (length % 2 == 0) {
                    int idx = length / 2;
                    ++runTimes[idx];
                    nodeNum[idx] += numNode;
                }
            } else
                --time;
        }

        for (int idx = 0; idx < 13; ++idx) {
            if (runTimes[idx] != 0)
                System.out.println("length " + (idx * 2) + ": " + nodeNum[idx] / runTimes[idx]);
        }
    }

    public static void main(String[] args) {
        EightPuzzle ep = new EightPuzzle();

//        ep.runRandom(10000);
//        ep.run();

        String choice;
        System.out.println(">> Please select mode.");
        System.out.print(">> (a) manual, (b) random: ");
        Scanner sc = new Scanner(System.in);
        choice = sc.nextLine();
        if (choice.equals("a") || choice.equals("A")) {
            while (!choice.equals("x") && !choice.equals("X")) {
                System.out.println(">> Please paste puzzle at here.");

                ep.run();

                System.out.println(">> Enter x to exit, other to continue...");
                sc = new Scanner(System.in);
                choice = sc.nextLine();
            }
        } else if (choice.equals("b") || choice.equals("B")) {
            while (!choice.equals("x") && !choice.equals("X")) {
                System.out.println(">> Test with random sample.");

                ep.runRandom(1);

                System.out.println(">> Enter x to exit, other to continue...");
                sc = new Scanner(System.in);
                choice = sc.nextLine();
            }
        } else {
            System.out.println(">> Please enter a or b.");
        }
        System.out.println(">> Bye.");
    }
}
