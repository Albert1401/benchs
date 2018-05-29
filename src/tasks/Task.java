package tasks;

import java.util.List;

public interface Task<T, R> {
    T init();

    List<R> initInds(int lambda);

    void generate(double p, List<R> tos);

    void mutate(T x, R inds);

    void rev(T x, R inds);

    double fitness(T x);

    double fitness(T x, R inds, double f);

    int dimension();

    double fitnessIWant();

    String getName();
}
/*
Task
Mutator mutate
Mutator
 */