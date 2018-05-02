package tasks;

import java.util.List;

public interface Task {
    double fitness(boolean[] x);

    double fitness(boolean[] x, int[] inds, double f);

    int dimension();

    double fitnessIWant();

    String getName();
}