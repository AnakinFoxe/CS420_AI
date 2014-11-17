package project2;

import java.util.Random;
import java.util.Scanner;

/**
 * Created by Xing HU on 10/31/14.
 */
public class NQueen {

    private int[][] chessboard_;
    private int n_;
    private int[] columnMark_;   // record the row of each queen

    private int numOfNonAttacks_ = 0;

    private final Random RAND_ = new Random();

    /**
     * Construct a N-Queen object
     * @param n             Size of N-Queen problem
     */
    public NQueen(int n) {
        chessboard_ = new int[n][n];
        columnMark_ = new int[n];
        n_ = n;
    }

    /**
     * Construct a N-Queen object according to column mark
     * @param columnMark    Row position of each column
     */
    public NQueen(int[] columnMark) {
        n_ = columnMark.length;
        chessboard_ = new int[n_][n_];
        columnMark_ = columnMark.clone();

        genChessboard(columnMark);
    }

    /**
     * Default constructor will create a 8x8 chessboard
     */
    public NQueen() {
        chessboard_ = new int[8][8];
        columnMark_ = new int[8];
        n_ = 8;
    }

    public int getNumOfNonAttacks_() {
        return numOfNonAttacks_;
    }

    public int[] getColumnMark_() {
        return columnMark_;
    }

    public void setColumnMark_(int[] columnMark) {
        genChessboard(columnMark);
    }

    public void printDebugInfo() {
        StringBuffer sb = new StringBuffer();
        for (int idx = 0; idx < n_; ++idx)
            sb.append(columnMark_[idx]);
        sb.append(" = ");
        sb.append(countNonAttacks());

        System.out.println(sb.toString());
    }
    public void printChessboard() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n_; ++i) {
            for (int j = 0; j < n_; ++j) {
                sb.append(chessboard_[i][j]);
                sb.append(" ");
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }

    public void genChessboard() {
        // clear the chessboard before generate new one
        clearChessboard();

        int[] col = new int[n_];    // place one queen each column

        for (int nQueen = 0; nQueen < n_; ++nQueen) {
            int i = RAND_.nextInt(n_);
            int j = RAND_.nextInt(n_);
            if (col[j] == 0) {
                chessboard_[i][j] = 1;
                columnMark_[j] = i;
                ++col[j];
            }
            else
                --nQueen;
        }

        numOfNonAttacks_ = countNonAttacks();
    }

    public void genChessboard(int[] columnMark) {
        // clear the chessboard before generate new one
        clearChessboard();

        columnMark_ = columnMark;

        for (int idx = 0; idx < n_; ++idx) {
            int i = columnMark[idx];
            int j = idx;

            chessboard_[i][j] = 1;
        }

        numOfNonAttacks_ = countNonAttacks();
    }

    public void clearChessboard() {
        for (int i = 0; i < n_; ++i) {
            columnMark_[i] = 0;
            for (int j = 0; j < n_; ++j)
                chessboard_[i][j] = 0;
        }

        numOfNonAttacks_ = 0;
    }

    private int countLineAttacks(int[] line) {
        int numOfAttacks = 0;
        for (int elm : line)
            if (elm > 1)
                numOfAttacks += elm;
        return numOfAttacks;
    }

    public int countAttacks() {
        int[] row = new int[n_];
        int[] col = new int[n_];
        int[] dia1 = new int[2 * n_ - 1];   // "/"-diagonal direction
        int[] dia2 = new int[2 * n_ - 1];   // "\"-diagonal direction

        for (int i = 0; i < n_; ++i)
            for (int j = 0; j < n_; ++j)
                if (chessboard_[i][j] != 0) {
                    ++row[i];
                    ++col[j];
                    ++dia1[i + j];
                    ++dia2[n_ - 1 + j - i];
                }

        int numOfAttacks = countLineAttacks(row);
        numOfAttacks += countLineAttacks(col);
        numOfAttacks += countLineAttacks(dia1);
        numOfAttacks += countLineAttacks(dia2);

        return numOfAttacks;
    }

    public int countNonAttacks() {
        int numOfNonAttacks = 0;
        for (int j1 = 0; j1 < n_; ++j1) {
            int numOfAttacks = -1;
            int i1 = columnMark_[j1];
            for (int j2 = 0; j2 < n_; ++j2 ) {
                int i2 = columnMark_[j2];

                if ((i1 == i2) || (i1 + j1 == i2 + j2) || (i1 - j1 == i2 - j2))
                    ++numOfAttacks;   // attacks each other
            }

            if (numOfAttacks == 0)
                ++numOfNonAttacks;
        }

        return numOfNonAttacks;
    }

    public boolean reachedGoal() {
        return (countNonAttacks() == n_);
    }

    public static void main(String[] args) {
        String choice = "";

        while (!choice.equals("x") && !choice.equals("X")) {
            System.out.println(">> Please select algorithm.");
            System.out.print(">> (a) Hill Climbing Algorithm, (b) Genetic Algorithm: ");
            Scanner sc = new Scanner(System.in);
            choice = sc.nextLine();

            if (choice.equals("a") || choice.equals("A")) {
                HillClimbForNQueen.run();
            } else if (choice.equals("b") || choice.equals("B")) {
                GAForNQueen.run();
            }

            System.out.println(">> Enter x to exit, other to continue...");
            sc = new Scanner(System.in);
            choice = sc.nextLine();
        }
    }
}
