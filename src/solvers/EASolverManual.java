package solvers;

import javafx.util.Pair;
import tasks.Task;

public interface EASolverManual extends EASolver {
    <T, R> Info<T> solve(Task<T, R> task, T x, int fcallslimit);
}
