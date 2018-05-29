package tasks;

import org.apache.commons.math3.distribution.BinomialDistribution;

import java.util.*;

import static tasks.PoissonDistribution.getInds;

public abstract class BTask implements Task<boolean[], int[]> {

    @Override
    public List<int[]> initInds(int lambda) {
        List<int[]> gen = new ArrayList<>(lambda);
        for (int i = 0; i < lambda; i++) {
            gen.add(new int[dimension() + 1]);
        }
        return gen;
    }

    @Override
    public void generate(double p, List<int[]> tos) {
        for (int[] to : tos) {
            getInds(dimension(), p, to, true);
        }
    }


    @Override
    public boolean[] init() {
        Random random = new Random();
        boolean[] x = new boolean[dimension()];
        for (int i = 0; i < x.length; i++) {
            x[i] = random.nextBoolean();
        }
        return x;
    }

    @Override
    public void mutate(boolean[] x, int[] inds) {
        for (int i = 1; i <= inds[0]; i++) {
            x[inds[i]] ^= true;
        }
    }

    @Override
    public void rev(boolean[] x, int[] inds) {
        for (int i = 1; i <= inds[0]; i++) {
            x[inds[i]] ^= true;
        }
    }
}
