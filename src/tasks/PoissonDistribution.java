package tasks;

import javafx.scene.paint.RadialGradient;
import org.apache.commons.math3.distribution.BinomialDistribution;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class PoissonDistribution {
    public static void getInds(int dim, double p, int[] to, boolean approx) {
        Random random = new Random();
        if (approx) {
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
                getInds(dim, p, to, true);
            }
        } else {
            BinomialDistribution distr = new BinomialDistribution(dim, p);
            Set<Integer> set = new TreeSet<>();
            int l = 0;
            while (l == 0) l = distr.sample();
            while (set.size() != l){
                set.add(random.nextInt(dim));
            }
            to[0] = l;
            int i = 1;
            for (int x : set) {
                to[i++] = x;
            }
        }
    }


}
