package solvers;

import tasks.Task;

public interface EASolverExplorer extends EASolverManual {
    <T, R> Info<T> solve(Task<T, R> task, T x, double p, int lambda, int fcallslimit);

    State.Type pType();
}
