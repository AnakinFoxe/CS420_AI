package project2;

import java.util.*;

/**
 * Created by Xing HU on 11/2/14.
 */
public class GAForNQueen {

    private int n_;
    private List<NQueen> population_;
    private int[] populationProb_;
    private int populationSize_;

    private Random RAND_ = new Random();

    private boolean isBatchTest = false;

    public GAForNQueen(int n, int initPopSize) {
        n_ = n;
        population_ = new ArrayList<>();
        populationProb_ = new int[initPopSize];
        populationSize_ = initPopSize;

        // generate initial population
        for (int idx = 0; idx < initPopSize; ++idx) {
            NQueen child = new NQueen(n);
            child.genChessboard();

            population_.add(child);
        }
    }

    public void setBatchTest(boolean isBatchTest) {
        this.isBatchTest = isBatchTest;
    }

    // use num of non-attack queens as fitness
    class FitnessComparator implements Comparator<NQueen> {
        @Override
        public int compare(NQueen q1, NQueen q2) {
            return q1.getNumOfNonAttacks_() - q2.getNumOfNonAttacks_();
        }
    }


    private void calFitness() {
        // sort the population according to fitness
        Collections.sort(population_, new FitnessComparator());

        int sum = 0;
        int idx = 0;
        for (NQueen child : population_) {
            sum += child.getNumOfNonAttacks_() + 1; // add one smoothing
            populationProb_[idx] = sum;
            ++idx;
        }
    }

    private List<NQueen> randomSelect() {
        int sum = populationProb_[populationSize_-1];

        // randomly select two parents according to their probability distribution
        int parent1 = 0;
        int rand = RAND_.nextInt(sum);
        for (; parent1 < populationSize_; ++parent1)
            if (rand < populationProb_[parent1])
                break;

        int parent2 = parent1;
        while (parent2 == parent1) {
            rand = RAND_.nextInt(sum);
            for (parent2 = 0; parent2 < populationSize_; ++parent2)
                if (rand < populationProb_[parent2])
                    break;
        }

        // get those two NQueen objects
        List<NQueen> parents = new ArrayList<>();
        parents.add(population_.get(parent1));
        parents.add(population_.get(parent2));

        return parents;
    }

    private List<NQueen> reproduce(List<NQueen> parents) {
        // clone DNA in case the parents will be used again
        int[] dna1 = parents.get(0).getColumnMark_().clone();
        int[] dna2 = parents.get(1).getColumnMark_().clone();

        // randomly select the crossover point and half
        int crossover = RAND_.nextInt(n_);
        int start, end;
        if (RAND_.nextBoolean()) {
            start = 0;
            end = crossover + 1;
        } else {
            start = crossover;
            end = n_;

        }

        // exchange
        for (int idx = start; idx < end; ++idx) {
            int temp = dna1[idx];
            dna1[idx] = dna2[idx];
            dna2[idx] = temp;
        }

        List<NQueen> children = new ArrayList<>();
        children.add(new NQueen(dna1));
        children.add(new NQueen(dna2));

        return children;
    }

    private NQueen mutate(NQueen child, int mutationRate) {
        if (RAND_.nextInt(mutationRate) < 1) {
            int[] dna = child.getColumnMark_().clone();
            dna[RAND_.nextInt(n_)] = RAND_.nextInt(n_);
            child.setColumnMark_(dna);
        }

        return child;
    }


    public int breed(int maxGeneration) {
        int generation = 0;

        int maxNonAttacks = 0;
        while (generation < maxGeneration) {
            calFitness();

            List<NQueen> newPopulation = new ArrayList<>();

            for (int pair = 0; pair < populationSize_ / 2; ++pair) {
                List<NQueen> parents = randomSelect();

                List<NQueen> children = reproduce(parents);

                newPopulation.addAll(children);
            }

            for (NQueen child : newPopulation) {
                child = mutate(child, 10);

                if (child.getNumOfNonAttacks_() > maxNonAttacks) {
                    maxNonAttacks = child.getNumOfNonAttacks_();
                }

                // success
                if (child.reachedGoal()) {
                    if (!isBatchTest)
                        child.printChessboard();
                    return generation;
                }
            }

            population_ = newPopulation;
            populationSize_ = newPopulation.size();
            populationProb_ = new int[populationSize_];

            ++generation;
            if (!isBatchTest && generation % 100000 == 0)
                System.out.println(generation + "th generation: current non-attack queens = " + maxNonAttacks);
        }

        return generation;
    }

    public static void run() {
        int maxGeneration = 1000000000; // limit
        int maxRound = 2;

        String choice;
        System.out.println("(GA)>> Please select mode.");
        System.out.print("(GA)>> (a) one round, (b) batch: ");
        Scanner sc = new Scanner(System.in);
        choice = sc.nextLine();
        if (choice.equals("a") || choice.equals("A")) {
            while (!choice.equals("x") && !choice.equals("X")) {
                System.out.println("(GA)>> Please specify the number of queens:");

                sc = new Scanner(System.in);
                int n = Integer.valueOf(sc.nextLine());

                System.out.println("(GA)>> Please specify the size of population (even number):");

                sc = new Scanner(System.in);
                int populationSize = Integer.valueOf(sc.nextLine());

                GAForNQueen ga = new GAForNQueen(n, populationSize);
                int generation = ga.breed(maxGeneration);

                if (generation < maxGeneration)
                    System.out.printf("Reached goal at %d generation.\n", generation);
                else
                    System.out.println("Failed to find a goal.");

                System.out.println("(GA)>> Enter x to exit GA, other to continue...");
                sc = new Scanner(System.in);
                choice = sc.nextLine();
            }
        } else if (choice.equals("b") || choice.equals("B")) {
            System.out.println("(GA)>> Please specify the max number of queens:");

            sc = new Scanner(System.in);
            int n = Integer.valueOf(sc.nextLine());

            for (; n >= 4; --n) {
                long totalGeneration = 0;
                System.out.println("Number of queens: " + n);
                for (int round = 0; round < maxRound; ++round) {
                    GAForNQueen ga = new GAForNQueen(n, 4);
                    ga.setBatchTest(true);

                    int generation = ga.breed(maxGeneration);

                    if (generation < maxGeneration)
                        System.out.printf("Reached goal at %d generation.\n", generation);
                    else
                        System.out.println("Failed to find a goal.");

                    totalGeneration += generation;
                }

                System.out.println("number of queens = " + n + ", round = " + maxRound
                        + ", average generation = " + totalGeneration / maxRound);

                maxRound *= 2;
            }
        }

    }

}
