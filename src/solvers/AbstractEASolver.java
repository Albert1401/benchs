package solvers;

import javafx.util.Pair;
import org.apache.commons.math3.distribution.BinomialDistribution;
import tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public abstract class AbstractEASolver implements EASolver {
    protected final int fcallslimit;

    protected AbstractEASolver(int fcallslimit) {
        this.fcallslimit = fcallslimit;
    }


    boolean[] init(int dim) {
        Random random = new Random();
        boolean[] x = new boolean[dim];
        for (int i = 0; i < x.length; i++) {
            x[i] = random.nextBoolean();
        }
        return x;
    }

    List<int[]> initInds(int dim, int lambda){
        List<int[]> gen = new ArrayList<>(lambda);
        for (int i = 0; i < lambda; i++) {
            gen.add(new int[dim + 1]);
        }
        return gen;
    }

    private int[] inxs;
    Random random = new Random();

//    void shuffle(int[] a) {
//        for (int i = a.length; i > 1; i--) {
//            int j = random.nextInt(i);
//            int co = a[i - 1];
//            a[i - 1] = a[j];
//            a[j] = co;
//        }
//    }

    void getInds(int dim, double p, int[] to) {
        int i = -1;
        to[0] = 0;

        for (int j = 1; j < dim; j++) {
            i = (int) (i + 1 + Math.log(random.nextDouble()) / Math.log(1 - p));
            if (i < dim) {
                to[j] = i;
                to[0]++;
            } else {
                break;
            }
        }
        if (to[0] == 0) {
            getInds(dim, p, to);
        }
    }



//    List<int[]> generate(boolean[] x, double p, int n) {
//        int dim = x.length;
//        List<int[]> gen = new ArrayList<>();
//
//        BinomialDistribution distr = new BinomialDistribution(dim, p);
//
//        for (int i = 0; i < n; i++) {
//            int l = 0;
//            while (l == 0) l = distr.sample();
//            gen.add(getInds(x.length, l));
//        }
//        return gen;
//    }

    List<int[]> generate(boolean[] x, double p, int n) {
        throw new RuntimeException("Refactor me");
    }

//    Pair<Double, int[]> best(boolean[] x, double fx, List<int[]> inds, Task task){
//        double[] fs = new double[inds.size()];
//        for (int i = 0; i < inds.size(); i++) {
//            fs[i] = task.fitness(x, inds.get(i), fx);
//        }
//        double mx =
//    }

    void generate(boolean[] x, double p, List<int[]> tos) {
        for (int[] to : tos) {
            getInds(x.length, p, to);
        }
    }

    public static void mutate(boolean x[], int[] inds) {
        for (int i = 1; i <= inds[0]; i++) {
            x[inds[i]] ^= true;
        }
    }
}
