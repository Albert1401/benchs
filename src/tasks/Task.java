package tasks;

public interface Task {
    double fitness(boolean[] x);

    int dimension();

    double fitnessIWant();

    String getName();
}