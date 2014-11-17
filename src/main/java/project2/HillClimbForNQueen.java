package project2;

import java.util.Scanner;

/**
 * Created by Xing HU on 11/4/14.
 */
public class HillClimbForNQueen {

    private int n_;
    private NQueen problem_;

    private boolean isBatchTest = false;

    public HillClimbForNQueen(int n) {
        n_ = n;

        problem_ = new NQueen(n);
        problem_.genChessboard();
    }

    public void setBatchTest(boolean isBatchTest) {
        this.isBatchTest = isBatchTest;
    }

    public boolean solve() {
        boolean foundNextStep = true;
        int currentNonAttacks = 0;
        NQueen currentState = null;

        while (!problem_.reachedGoal() && foundNextStep) {
            foundNextStep = false;
            int[] columnMark = problem_.getColumnMark_();

            for (int j = 0; j < n_; ++j) {
                for (int i = 0; i < n_; ++i) {
                    if (i != columnMark[j]) {
                        // generate the possible next move
                        int[] possibleMove = columnMark.clone();
                        possibleMove[j] = i;

                        // generate the possible state according to this move
                        NQueen possibleState = new NQueen(possibleMove);

                        // compute non-attack queens for this state
                        int possibleNonAttacks = possibleState.countNonAttacks();

                        // record the first highest one
                        if (possibleNonAttacks > currentNonAttacks) {
                            currentNonAttacks = possibleNonAttacks;
                            currentState = possibleState;

                            foundNextStep = true;   // at least one move found
                        }
                    }
                }
            }

            // assign found state as new problem
            problem_ = currentState;
        }

        if (!isBatchTest)
            problem_.printChessboard();

        if (problem_.reachedGoal())
            return true;
        else
            return false;
    }

    public static void run() {
        String choice;
        System.out.println("(HC)>> Please select mode.");
        System.out.print("(HC)>> (a) one round, (b) batch: ");
        Scanner sc = new Scanner(System.in);
        choice = sc.nextLine();
        if (choice.equals("a") || choice.equals("A")) {
            while (!choice.equals("x") && !choice.equals("X")) {
                System.out.println("(HC)>> Please specify the number of queens:");

                sc = new Scanner(System.in);
                int n = Integer.valueOf(sc.nextLine());

                HillClimbForNQueen hc = new HillClimbForNQueen(n);
                if (hc.solve())
                    System.out.println("Problem solved");
                else
                    System.out.println("Failed to solve the problem this time");

                System.out.println("(HC)>> Enter x to exit HC, other to continue...");
                sc = new Scanner(System.in);
                choice = sc.nextLine();
            }

        } else if (choice.equals("b") || choice.equals("B")) {
            System.out.println("(HC)>> Please specify the number of queens:");

            sc = new Scanner(System.in);
            int n = Integer.valueOf(sc.nextLine());

            int numOfSolved = 0;
            for (int idx = 0; idx < 100; ++idx) {
                HillClimbForNQueen hc = new HillClimbForNQueen(n);
                hc.setBatchTest(true);
                if (hc.solve())
                    ++numOfSolved;
            }

            System.out.println(numOfSolved + " out of 100 solved.");
        }
    }
}
