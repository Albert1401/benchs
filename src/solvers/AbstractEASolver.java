package solvers;

import org.apache.commons.math3.distribution.BinomialDistribution;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractEASolver implements EASolver {
    protected final int fcallslimit;

    protected AbstractEASolver(int fcallslimit) {
        this.fcallslimit = fcallslimit;
    }


    boolean[] init(int dim){
        Random random = new Random();
        boolean[] x = new boolean[dim];
        for (int i = 0; i < x.length; i++) {
            x[i] = random.nextBoolean();
        }
        return x;
    }


    private List<Integer> inxs;

    void mutate(boolean x[], int l) {
        if (inxs == null || inxs.size() != x.length) {
            inxs = IntStream.range(0, x.length).boxed().collect(Collectors.toList());
        }
        Collections.shuffle(inxs);
        for (Integer inx : inxs.subList(0, l)) {
            x[inx] ^= true;
        }
    }

//    void mutate(boolean x[], int l) {
//        List<Integer> inxs = IntStream.range(0, x.length).boxed().collect(Collectors.toList());
//        Collections.shuffle(inxs);
//        for (Integer inx : inxs.subList(0, l)) {
//            x[inx] ^= true;
//        }
//    }

    boolean[][] generate(boolean[] x, double p, int n) {
        int dim = x.length;
        boolean[][] gen = new boolean[n][dim];
        for (int i = 0; i < n; i++) {
            System.arraycopy(x, 0, gen[i], 0, dim);
        }


        BinomialDistribution distr = new BinomialDistribution(dim, p);

        for (int i = 0; i < n; i++) {
            int l = 0;
            while (l == 0) l = distr.sample();
            mutate(gen[i], l);
        }
        return gen;
    }
}
