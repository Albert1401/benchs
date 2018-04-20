package solvers;

import tasks.Task;

public interface EASolver {
    Info solve(Task task);

    String getName();

    EASolver copy();
}
