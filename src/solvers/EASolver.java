package solvers;

import tasks.Task;

public interface EASolver {
    <T, R> Info<T> solve(Task<T, R> task);

    String getName();

    EASolver copy();
}
