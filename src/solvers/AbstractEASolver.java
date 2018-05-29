package solvers;

import javafx.util.Pair;
import org.apache.commons.math3.distribution.BinomialDistribution;
import tasks.Task;

import java.util.*;
import java.util.stream.IntStream;

public abstract class AbstractEASolver implements EASolver {
    protected final int fcallslimit;

    protected AbstractEASolver(int fcallslimit) {
        this.fcallslimit = fcallslimit;
    }

//    private int[] inxs;
//    void shuffle(int[] a) {
//        for (int i = a.length; i > 1; i--) {
//            int j = random.nextInt(i);
//            int co = a[i - 1];
//            a[i - 1] = a[j];
//            a[j] = co;
//        }
//    }

}
